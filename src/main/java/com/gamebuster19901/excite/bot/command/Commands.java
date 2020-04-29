package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.EventReceiver;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

@SuppressWarnings("rawtypes")
public class Commands extends EventReceiver{
	private final CommandDispatcher<MessageContext> dispatcher = new CommandDispatcher<>();
	public static final Commands DISPATCHER = new Commands();
	
	public Commands() {
		OnlineCommand.register(dispatcher);
		WhoIsCommand.register(dispatcher);
	}
	
	public void handleCommand(String command) {
		try {
			MessageContext context = new MessageContext();
			this.dispatcher.execute(command, context);
		}
		catch (CommandSyntaxException e) {
			System.out.println(e.getRawMessage());
		}
	}
	
	public void handleCommand(GuildMessageReceivedEvent e) {
		try {
			MessageContext<GuildMessageReceivedEvent> context = new MessageContext<GuildMessageReceivedEvent>(e);
			if(e.getMessage().getContentRaw().startsWith("!")) {
				this.dispatcher.execute(e.getMessage().getContentRaw(), context);
			}
		}
		catch (Exception ex) {
			if(!ex.getMessage().startsWith("Unknown command at position")) {
				e.getChannel().sendMessage(ex.getClass() + " " + ex.getMessage()).complete();
			}
		}
	}
	
	public void handleCommand(PrivateMessageReceivedEvent e) {
		try {
			MessageContext<PrivateMessageReceivedEvent> context = new MessageContext<PrivateMessageReceivedEvent>(e);
			this.dispatcher.execute(e.getMessage().getContentRaw(), context);
		}
		catch (CommandSyntaxException ex) {
			e.getChannel().sendMessage(ex.getMessage()).complete();
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
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String message = e.getMessage().getContentRaw();
		if(message.startsWith("!")) {
			Commands.DISPATCHER.handleCommand(e);
		}
	}
	
}
