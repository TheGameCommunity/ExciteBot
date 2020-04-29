package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

@SuppressWarnings("rawtypes")
public class WhoIsCommand extends WiimmfiCommand{

	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!whois")
			.then(
				Commands.argument("player", StringArgumentType.word())
					.executes(
						(command) -> {
							send(getResponse(command.getSource(), command.getArgument("player", String.class)), command.getSource().getEvent());
							return 0;
						}
					)
			)
		);
	}
	
	@SuppressWarnings("static-access")
	public static String getResponse(MessageContext messageContext, String lookingFor) {
		Wiimmfi wiimmfi = Main.discordBot.getWiimmfi();
		if(checkNotErrored(messageContext)) {
			Player[] players = wiimmfi.getKnownPlayers();
			int maxLines = 10000;
			if(messageContext.isGuildMessage() || messageContext.isPrivateMessage()) {
				maxLines = 24;
			}
			
			String response = "";
			if(!lookingFor.isEmpty()) {
				for(Player p : players) {
					if(p.getName().equalsIgnoreCase(lookingFor)) {
						response = response + p + "\n";
					}
					else if (p.getDiscord().toLowerCase().startsWith(lookingFor.toLowerCase())) {
						response = response + p + "\n";
					}
					else if (p.getFriendCode().equals(lookingFor)) {
						response = response + p + "\n";
					}
					else if (lookingFor.equals("" + p.getPlayerID())) {
						response = response + p + "\n";
					}
				}
				if(response.isEmpty()) {
					response = "No players found.";
				}
				else if(response.length() > 2000) {
					response = "There are too many players known as " + response + " to display them all. + \n" +
						"Sorry this is a limitation with discord, this bot could get banned for sending too many messages.";
				}
			}
			else  {
				response = "Usage: !whois <Player>";
			}
			return response;
		}
		return "Bot offline due to an error: " + wiimmfi.getError().getClass().getCanonicalName() + ": " + wiimmfi.getError().getMessage();
	}
	
}
