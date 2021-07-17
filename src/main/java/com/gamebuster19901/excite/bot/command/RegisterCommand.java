package com.gamebuster19901.excite.bot.command;

import java.util.HashSet;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class RegisterCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("register")
			.then(Commands.literal("profile")
				.then(Commands.argument("player", StringArgumentType.greedyString())
					.executes(context -> {
						requestRegistration(context.getSource(), context.getArgument("player", String.class));
						return 1;
					})	
				)
			).then(Commands.literal("wii")
				.then(Commands.argument("code", StringArgumentType.greedyString())
					.executes(context -> {
						registerWii(context.getSource(), context.getArgument("code", String.class));
						return 1;
					})
				)
			)
		);
	}
	
	@SuppressWarnings("rawtypes")
	private static void requestRegistration(MessageContext context, String player) {
		HashSet<Player> players = new HashSet<Player>();
		DiscordUser discordUser = context.getDiscordAuthor();
		if(context.isConsoleMessage()) {
			context.sendMessage("This command must be executed from discord.");
			return;
		}
		if(discordUser.requestingRegistration()) {
			context.sendMessage("You are already trying to register a profile! Please wait until registration is complete or the registration code expires.");
			return;
		}
		
		try {
			int pid = Integer.parseInt(player);
			players.add(Player.getPlayerByID(ConsoleContext.INSTANCE, pid));
		}
		catch(NumberFormatException e) {
			for(Player p : Player.getPlayersByName(ConsoleContext.INSTANCE, player)) {
				players.add(p);
			}
		}

		switch(players.size()) {
			case 0:
				context.sendMessage("Could not find a player with name or PID of " + player);
				break;
			case 1:
				Player desiredProfile = players.toArray(new Player[]{})[0];
				if(desiredProfile.isBanned()) {
					context.sendMessage("You cannot register a banned profile.");
					return;
				}
				String securityCode = discordUser.requestRegistration(desiredProfile);
				sendInfo(context, discordUser, desiredProfile, securityCode);
				break;
			default:
				String ambiguities = "";
				for(Player p : players) {
					ambiguities += p.toFullString() + "\n";
				}
				context.sendMessage(player + " is ambiguous as there is more than one profile known with that name. Please supply your account's PID instead of it's name." 
					+ "\n\nAmbiguities:\n\n" + ambiguities);
				break;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void sendInfo(MessageContext context, DiscordUser discordUser, Player desiredProfile, String securityCode) {
		context.getDiscordAuthor().sendMessage(
				discordUser.getJDAUser().getAsMention() + 
				", you have requested registration of the following profile:\n\n"
				+ desiredProfile.toFullString() 
				+ "\n\nRegistration Code: `" + securityCode + "`\n"
				+ "Change the profile's username to the registration code, then log in and search for a match.\n\n"
				+ "Registration may take up to two minutes to complete. The registration code expires after 5 minutes.\n\n"
				+ "You will receive a reply upon registration completion. Please stay logged in and searching until registration is completed.\n\n"
				);
	}
	
	private static void registerWii(MessageContext context, String securityCode) {
		if(context.isGuildMessage()) {
			context.deletePromptingMessage(ConsoleContext.INSTANCE, context.getMention() + " - Woah! Send your code to me via direct message! Nobody else should be seeing your registration code!");
		}
	}
}
