package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.ThreadService;
import com.mojang.brigadier.CommandDispatcher;

public class RestartCommand {
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("restart").executes((context) -> {
			return stop(context.getSource());
		}));
	}
	
	@SuppressWarnings("rawtypes")
	private static int stop(MessageContext context) {
		if(context.isAdmin()) {
			try {
				context.sendMessage("Restarting!");
				DiscordUser.messageAllAdmins(context.getDiscordAuthor().toDetailedString() + " is restarting the bot!");
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
			finally {
				ThreadService.shutdown(context, -1);
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
}
