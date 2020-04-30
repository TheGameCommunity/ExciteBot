package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.mojang.brigadier.CommandDispatcher;

@SuppressWarnings("rawtypes") 
public class OnlineCommand extends WiimmfiCommand{

	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!online").executes((command) -> {
			send(getResponse(command.getSource()), command.getSource().getEvent());
			return 1;
		}));
	}
	
	public static String getResponse(MessageContext messageContext) {
		Wiimmfi wiimmfi = Main.discordBot.getWiimmfi();
		if(checkNotErrored(messageContext)) {
			Player[] players = Wiimmfi.getOnlinePlayers();
			String response = "Players Online: (" + players.length + ")" + "\n\n";
			int maxLines = 10000;
			if(messageContext.isGuildMessage() || messageContext.isPrivateMessage()) {
				maxLines = 24;
			}
			
			for(int i = 0; i < players.length && i < maxLines; i++) {
				response += players[i].toString() + '\n';
				if (i == maxLines - 1 && players.length > maxLines) {
					response += "and (" + (players.length - 24) + ") others";
				}
			}
			return response;
		}
		return "Bot offline due to an error: " + wiimmfi.getError().getClass().getCanonicalName() + ": " + wiimmfi.getError().getMessage();
	}
	
}
