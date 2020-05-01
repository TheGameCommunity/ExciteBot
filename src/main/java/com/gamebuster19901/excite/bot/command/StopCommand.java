package com.gamebuster19901.excite.bot.command;

import com.mojang.brigadier.CommandDispatcher;

public class StopCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!stop").executes((context) -> {
			return stop(context.getSource());
		}));
	}
	
	@SuppressWarnings("rawtypes")
	private static int stop(MessageContext context) {
		if(context.isAdmin()) {
			try {
				context.sendMessage("Stopping!");
				System.out.println(context.getTag() + " Stopped the bot!");
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
			System.exit(1);
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
