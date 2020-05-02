package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
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
			String response = Wiimmfi.getOnlinePlayerList(messageContext);
			return response;
		}
		return "Bot offline due to an error: " + wiimmfi.getError().getClass().getCanonicalName() + ": " + wiimmfi.getError().getMessage();
	}
	
}
