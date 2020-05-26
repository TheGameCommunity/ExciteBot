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
				Commands.argument("player", StringArgumentType.greedyString())
					.executes(
						(command) -> {
							return sendResponse(command.getSource(), command.getArgument("player", String.class));
						}
					)
			)
		);
	}
	
	@SuppressWarnings("static-access")
	public static int sendResponse(MessageContext context, String lookingFor) {
		Wiimmfi wiimmfi = Main.discordBot.getWiimmfi();
		String response = "";
		if(checkNotErrored(context)) {
			Player[] players = wiimmfi.getKnownPlayers();
			
			if(!lookingFor.isEmpty()) {
				for(Player p : players) {
					if(p.getName().equalsIgnoreCase(lookingFor)) {
						response = response + p + "\n";
					}
					else if (p.getPrettyDiscord().toLowerCase().startsWith(lookingFor.toLowerCase())) {
						response = response + p + "\n";
					}
					else if (p.getFriendCode().equals(lookingFor)) {
						response = response + p + "\n";
					}
					else if (lookingFor.equals("" + p.getPlayerID())) {
						response = response + p + "\n";
					}
					else if (lookingFor.equals("" + p.getDiscord())) {
						response = response + p + "\n";
					}
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
