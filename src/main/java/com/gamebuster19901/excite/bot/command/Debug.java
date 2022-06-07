package com.gamebuster19901.excite.bot.command;

import com.mojang.brigadier.CommandDispatcher;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class Debug {

	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		
		dispatcher.register(
			Commands.literal("debug")
				.then(Commands.literal("out").executes((context -> {return setDebugOutput(context.getSource(), context.getSource().getChannel());})))
				
				
		);
		
	}
	
	private static int setDebugOutput(MessageContext context, MessageChannel channel) {
		if(context.isOperator()) {
			if(channel instanceof TextChannel) {
				TextChannel tChannel = (TextChannel) channel;
				context.sendMessage("Set debug output to " + tChannel.getAsMention() + " in " + context.getServer().getName());
			}
			else {
				context.sendMessage(channel.getName() + " in " + context.getServer() + " is not a text channel");
			}
		}
		else {
			context.sendMessage("You do not have permission to execute that command");
		}
		return 1;
	}
	
}
