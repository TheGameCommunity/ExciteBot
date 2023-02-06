package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.util.ThreadService;
import com.mojang.brigadier.CommandDispatcher;

public class StopCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		dispatcher.register(Commands.literal("stop").executes((context) -> {
			return stop(context.getSource());
		}));
	}
	
	@SuppressWarnings("rawtypes")
	private static int stop(CommandContext context) {
		if(context.isAdmin()) {
			try {
				context.sendMessage("Stopping!");
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
			finally {
				ThreadService.shutdown(context, 0);
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
