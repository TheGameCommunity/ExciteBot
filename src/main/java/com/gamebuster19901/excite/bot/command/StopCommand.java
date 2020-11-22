package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.user.DiscordUser;
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
				System.out.println(context.getTag() + "(" + context.getSenderId() + ") Stopped the bot!");
				context.sendMessage("Stopping!");
				if(Main.botOwner != null) {
					DiscordUser botOwner = DiscordUser.getDiscordUser(Main.botOwner);
					botOwner.sendMessage(botOwner.getJDAUser().getAsMention() + ", " + context.getTag() + "(" + context.getSenderId() + ") Stopped the bot!");
				}
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
			finally {
				System.exit(0);
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
