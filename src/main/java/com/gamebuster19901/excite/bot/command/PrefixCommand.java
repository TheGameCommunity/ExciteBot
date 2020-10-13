package com.gamebuster19901.excite.bot.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class PrefixCommand {	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("prefix")
			.then(Commands.argument("pre", StringArgumentType.greedyString())
				.executes((context) -> {
					return setPrefix(context.getSource(), context.getArgument("pre", String.class));
				})));
	}
	
	@SuppressWarnings("rawtypes")
	private static int setPrefix(MessageContext context, String prefix) {
		if(context.isAdmin()) {
			if(context.isGuildMessage()) {
				if(Commands.isValidPrefix(prefix)) {
					if(context.getServer().setPrefix(prefix)) {
						context.sendMessage("Prefix for " + context.getServer().getName() + " set to " + context.getServer().getPrefix());
						return 0;
					}
				}
				context.sendMessage(prefix + " is not a valid prefix");
			}
			else {
				context.sendMessage("This command must be executed in a discord server");
			}
		}
		else {
			context.sendMessage("You do not have permission to execute that command.");
		}
		return 1;
	}
	
}
