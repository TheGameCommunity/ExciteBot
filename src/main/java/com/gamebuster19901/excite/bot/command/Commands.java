package com.gamebuster19901.excite.bot.command;

import org.apache.commons.lang3.StringUtils;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.audit.CommandAudit;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.StacktraceUtil;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@SuppressWarnings("rawtypes")
public class Commands {
	private final CommandDispatcher<CommandContext> dispatcher = new CommandDispatcher<>();
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
		RestartCommand.register(dispatcher);
		IconDumpCommand.register(dispatcher);
		GameDataCommand.register(dispatcher);
		RankCommand.register(dispatcher);
		PrefixCommand.register(dispatcher);
		ChangelogCommand.register(dispatcher);
		InsertCommand.register(dispatcher);
		ArchiveCommand.register(dispatcher);
		CRCCommand.register(dispatcher);
		Debug.register(dispatcher);
	}
	
	public void handleCommand(String command) {
		CommandContext context = new CommandContext(Main.CONSOLE, command);
		try {
			CommandAudit.addCommandAudit(context, command);
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
	
	public void handleCommand(MessageReceivedEvent e) {
		CommandContext<MessageReceivedEvent> context = new CommandContext<MessageReceivedEvent>(e);
		try {
			String message = e.getMessage().getContentRaw();
			String prefix = "";
			if(context.isGuildMessage()) {
				prefix = context.getServer().getPrefix();
			}
			if(message.startsWith(prefix)) {
				message = StringUtils.replaceOnce(message, prefix, "");
				DiscordUser sender = DiscordUser.getDiscordUser(ConsoleContext.INSTANCE, e.getAuthor().getIdLong());
				if(!sender.isBanned()) {
					if(!sender.isBanned()) {
						CommandAudit.addCommandAudit(context, message);
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
	
	public static LiteralArgumentBuilder<CommandContext> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}
	
	public static <T> RequiredArgumentBuilder<CommandContext, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
	public CommandDispatcher<CommandContext> getDispatcher() {
		return this.dispatcher;
	}
	
	public boolean setPrefix(CommandContext context, String prefix) {
		if(context.isAdmin() && context.isGuildMessage() && isValidPrefix(prefix)) {
			context.getServer().setPrefix(prefix);
			return true;
		}
		return false;
	}
	
	public String getPrefix(CommandContext context) {
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
	
	public static String readString(StringReader reader) {
		StringBuilder ret = new StringBuilder("");
		while(reader.canRead() && !Character.isSpaceChar(reader.peek())) {
			ret.append(reader.read());
		}
		return ret.toString();
	}
	
	public static String readQuotedString(StringReader reader) throws CommandSyntaxException {
		StringBuilder ret = new StringBuilder("");
		if(reader.canRead()) {
			if(reader.peek() == '"') {
				reader.read();
			}
			else {
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(reader);
			}
		}
		boolean foundEndQuote = false;
		while(reader.canRead()) {
			char c = reader.read();
			if(c == '"') {
				foundEndQuote = true;
				break;
			}
			ret.append(c);
		}
		if(!foundEndQuote) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext(reader);
		}
		return ret.toString();
	}
	
}
