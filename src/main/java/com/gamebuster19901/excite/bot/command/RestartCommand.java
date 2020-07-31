package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.CommandDispatcher;

public class RestartCommand {
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!restart").executes((context) -> {
			return stop(context.getSource());
		}));
	}
	
	@SuppressWarnings("rawtypes")
	private static int stop(MessageContext context) {
		if(context.isAdmin()) {
			try {
				context.sendMessage("Restarting!");
				DiscordUser.messageAllAdmins(context.getAuthor().toDetailedString() + " is restarting the bot!");
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
			finally {
				System.exit(-1);
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
}
