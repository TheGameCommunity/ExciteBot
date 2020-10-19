package com.gamebuster19901.excite.bot.command;

import org.apache.commons.lang3.StringUtils;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.audit.Audit;
import com.gamebuster19901.excite.bot.audit.CommandAudit;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.StacktraceUtil;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

@SuppressWarnings("rawtypes")
public class Commands {
	private final CommandDispatcher<MessageContext> dispatcher = new CommandDispatcher<>();
	public static final Commands DISPATCHER = new Commands();
	public static final String DEFAULT_PREFIX = "!";
	
	public Commands() {
		OnlineCommand.register(dispatcher);
		WhoIsCommand.register(dispatcher);
		BanCommand.register(dispatcher);
		PardonCommand.register(dispatcher);
		RegisterCommand.register(dispatcher);
		NotifyCommand.register(dispatcher);
		StopCommand.register(dispatcher);
		HelpCommand.register(dispatcher);
		BackupCommand.register(dispatcher);
		BanlistCommand.register(dispatcher);
		RestartCommand.register(dispatcher);
		BanInfoCommand.register(dispatcher);
		PlayersCommand.register(dispatcher);
		IconDumpCommand.register(dispatcher);
		VideoCommand.register(dispatcher);
		GameDataCommand.register(dispatcher);
		RankCommand.register(dispatcher);
		CDCommand.register(dispatcher);
		PrefixCommand.register(dispatcher);
	}
	
	public void handleCommand(String command) {
		MessageContext context = new MessageContext(Main.CONSOLE);
		try {
			Audit.addAudit(new CommandAudit(context, command));
			this.dispatcher.execute(command, context);
		}
		catch (CommandSyntaxException e) {
			context.sendMessage(e.getRawMessage().getString());
		}
		catch(Throwable t) {
			context.sendMessage(StacktraceUtil.getStackTrace(t));
			if(!context.isConsoleMessage()) {
				t.printStackTrace();
			}
			if(t instanceof Error) {
				throw t;
			}
		}
	}
	
	public void handleCommand(GuildMessageReceivedEvent e) {
		MessageContext<GuildMessageReceivedEvent> context = new MessageContext<GuildMessageReceivedEvent>(e);
		try {
			String message = e.getMessage().getContentRaw();
			String prefix = context.getServer().getPrefix();
			if(message.startsWith(prefix)) {
				message = StringUtils.replaceOnce(message, prefix, "");
				DiscordUser sender = DiscordUser.getDiscordUser(e.getAuthor().getIdLong());
				if(!sender.isBanned()) {
					sender.sentCommand(context);
					if(!sender.isBanned()) {
						Audit.addAudit(new CommandAudit(context, e.getMessage().getContentRaw()));
						this.dispatcher.execute(message, context);
					}
				}
			}
		}
		catch (CommandSyntaxException ex) {
			if(ex.getMessage() != null && !ex.getMessage().startsWith("Unknown command at position")) {
				context.sendMessage(ex.getClass() + " " + ex.getMessage());
			}
		}
		catch(Throwable t) {
			if(t instanceof StackOverflowError) {
				context.sendMessage(t.getClass().getCanonicalName());
				throw t;
			}
			context.sendMessage(StacktraceUtil.getStackTrace(t));
			if(!context.isConsoleMessage()) {
				t.printStackTrace();
			}
			if(t instanceof Error) {
				throw t;
			}
		}
	}
	
	public void handleCommand(PrivateMessageReceivedEvent e) {
		MessageContext<PrivateMessageReceivedEvent> context = new MessageContext<PrivateMessageReceivedEvent>(e);
		try {
			DiscordUser sender = DiscordUser.getDiscordUser(e.getAuthor().getIdLong());
			if(!sender.isBanned()) {
				sender.sentCommand(context);
				if(!sender.isBanned()) {
					Audit.addAudit(new CommandAudit(context, e.getMessage().getContentRaw()));
					this.dispatcher.execute(e.getMessage().getContentRaw(), context);
				}
			}
		}
		catch (CommandSyntaxException ex) {
			context.sendMessage(ex.getMessage());
		}
		catch(Throwable t) {
			context.sendMessage(StacktraceUtil.getStackTrace(t));
			if(!context.isConsoleMessage()) {
				t.printStackTrace();
			}
			if(t instanceof Error) {
				throw t;
			}
		}
	}
	
	public static LiteralArgumentBuilder<MessageContext> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}
	
	public static <T> RequiredArgumentBuilder<MessageContext, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
	public CommandDispatcher<MessageContext> getDispatcher() {
		return this.dispatcher;
	}
	
	public boolean setPrefix(MessageContext context, String prefix) {
		if(context.isAdmin() && context.isGuildMessage() && isValidPrefix(prefix)) {
			return context.getServer().setPrefix(prefix);
		}
		return false;
	}
	
	public String getPrefix(MessageContext context) {
		if(context.isGuildMessage()) {
			return context.getServer().getPrefix();
		}
		return DEFAULT_PREFIX;
	}
	
	public static boolean isValidPrefix(String prefix) {
		if(prefix == null || prefix.isEmpty()) {
			return false;
		}
		for(int c : prefix.toCharArray()) {
			if(Character.isWhitespace(c) || Character.isSupplementaryCodePoint(c) || Character.isISOControl(c) || c == '@' || c == '#' || c == '`') {
				return false;
			}
		}
		return true;
	}
	
}
