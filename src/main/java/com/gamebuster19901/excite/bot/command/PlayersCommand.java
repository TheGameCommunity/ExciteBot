package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.CommandDispatcher;

public class PlayersCommand {
	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("players").executes((command) -> {
			return sendResponse(command.getSource());
		}));	
	}
	
	@SuppressWarnings("rawtypes")
	public static int sendResponse(MessageContext context) {
		if(context.isAdmin() || context.isPrivateMessage()) {
			Player[] players = Player.getEncounteredPlayers(ConsoleContext.INSTANCE);
			String response = "Known players: (" + players.length + ") \n\n";
			for(Player player : players) {
				response += player.toFullString() + "\n";
			}
			context.sendMessage(response);
			if(!context.isConsoleMessage()) {
				DiscordUser.getDiscordUser(ConsoleContext.INSTANCE, context.getSenderId()).sentCommand(context, 2);
			}
		}
		else {
			context.sendMessage("You must be an administrator to execute this command in a server.");
		}
		return 0;
	}
}
