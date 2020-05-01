package com.gamebuster19901.excite.bot.command;

import java.util.HashSet;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class RegisterCommand extends WiimmfiCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!register").then(Commands.argument("player", StringArgumentType.greedyString()).executes(context -> {
			requestRegistration(context.getSource(), context.getArgument("player", String.class));
			return 1;
		})));
	}
	
	@SuppressWarnings("rawtypes")
	private static void requestRegistration(MessageContext context, String player) {
		HashSet<Player> players = new HashSet<Player>();
		DiscordUser discordUser = context.getAuthor();
		if(context.isConsoleMessage()) {
			context.sendMessage("This command must be executed from discord.");
			return;
		}
		if(discordUser.requestingRegistration()) {
			context.sendMessage("You are already trying to register a profile! Please wait until registration is complete or the registration code expires.");
			return;
		}
		if(context.getAuthor().isValid()) {
			for(Player p : Player.getEncounteredPlayers()) {
				if((p.getPlayerID() + "").equals(player)) {
					String securityCode = discordUser.requestRegistration(p);
					sendInfo(context, discordUser, p, securityCode);
					return;
				}
				if(p.getName().equalsIgnoreCase(player)) {
					players.add(p);
				}
			}
		

			switch(players.size()) {
				case 0:
					context.sendMessage("Could not find a player with name or PID of " + player);
					break;
				case 1:
					Player desiredProfile = players.toArray(new Player[]{})[0];
					String securityCode = discordUser.requestRegistration(desiredProfile);
					sendInfo(context, discordUser, desiredProfile, securityCode);
					break;
				default:
					String ambiguities = "";
					for(Player p : players) {
						ambiguities += p + "\n";
					}
					context.sendMessage(player + " is ambiguous as there is more than one profile known with that name. Please supply your account's PID instead of it's name." 
						+ "\n\nAmbiguities:\n\n" + ambiguities);
					break;
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void sendInfo(MessageContext context, DiscordUser discordUser, Player desiredProfile, String securityCode) {
		context.sendMessage(
				discordUser.getJDAUser().getAsMention() + 
				", you have requested registration of the following profile:\n\n"
				+ desiredProfile 
				+ "\n\nRegistration Code: `" + securityCode + "`\n"
				+ "Change the profile's username to the registration code, then log in and search for a match.\n\n"
				+ "Registration may take up to two minutes to complete. The registration code expires after 5 minutes.\n\n"
				+ "You will receive a reply upon registration completion. Please stay logged in and searching until registration is completed.\n\n"
				);
	}
}
