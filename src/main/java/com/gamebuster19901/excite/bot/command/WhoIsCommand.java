package com.gamebuster19901.excite.bot.command;

import java.util.Arrays;
import java.util.HashSet;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

@SuppressWarnings("rawtypes")
public class WhoIsCommand {

	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		LiteralCommandNode<MessageContext> builder = dispatcher.register(Commands.literal("whois")
			.then(Commands.argument("player", StringArgumentType.greedyString()).executes((command) -> {
				return sendResponse(command.getSource(), command.getArgument("player", String.class));
			}
		)));
		
		dispatcher.register(Commands.literal("wi").redirect(builder));
	}
	
	public static int sendResponse(MessageContext context, String lookingFor) {
		Wiimmfi wiimmfi = Main.discordBot.getWiimmfi();
		String response = "";
		if(wiimmfi.getError() == null) {
			if(!lookingFor.isEmpty()) {
				DiscordUser[] users = DiscordUser.getDiscordUsersWithUsernameOrID(ConsoleContext.INSTANCE, lookingFor);
				HashSet<Player> claimedPlayers = new HashSet<Player>();
				HashSet<Player> unclaimedPlayers = new HashSet<Player>();
				for(DiscordUser user : users) {
					response = response + user.toDetailedString() + "\n";
					for(Player player : user.getProfiles(context)) {
						response = response + player.toFullString() + "\n";
						claimedPlayers.add(player);
					}
				}
				
				unclaimedPlayers.addAll(Arrays.asList(Player.getPlayersByAnyIdentifier(ConsoleContext.INSTANCE, lookingFor)));
				unclaimedPlayers.removeAll(claimedPlayers);
				if(response != null && unclaimedPlayers.size() > 0 && claimedPlayers.size() > 0) {
					response = response + "\nUnclaimed Profiles :\n";
				}
				for(Player player : unclaimedPlayers) {
					response = response + player.toFullString() + "\n";
				}
				if(response.isEmpty()) {
					response = "No players found.";
				}
				
			}
			else  {
				response = "Usage: !whois <Player>";
			}
		}
		else {
			response = "Bot offline due to an error: " + wiimmfi.getError().getClass().getCanonicalName() + ": " + wiimmfi.getError().getMessage();
		}
		context.sendMessage(response);
		return 1;
	}
	
}
