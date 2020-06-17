package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.CommandDispatcher;

public class UsersCommand {
	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!users").executes((command) -> {
			return sendResponse(command.getSource());
		}));	
	}
	
	@SuppressWarnings("rawtypes")
	public static int sendResponse(MessageContext context) {
		if(context.isAdmin() || context.isPrivateMessage()) {
			Player[] players = Player.getEncounteredPlayers();
			String response = "Known users: (" + players.length + ") \n\n";
			for(Player player : players) {
				response += player + "\n";
			}
			context.sendMessage(response);
			if(!context.isConsoleMessage()) {
				DiscordUser.getDiscordUser(context.getSenderId()).sentCommand(context, 2);
			}
		}
		else {
			context.sendMessage("You must be an administrator to execute this command in a server.");
		}
		return 0;
	}
}
