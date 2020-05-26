package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.mojang.brigadier.CommandDispatcher;

@SuppressWarnings("rawtypes") 
public class OnlineCommand extends WiimmfiCommand{

	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!online").executes((command) -> {
			return sendResponse(command.getSource());
		}));
	}
	
	public static int sendResponse(MessageContext context) {
		Wiimmfi wiimmfi = Main.discordBot.getWiimmfi();
		String response;
		if(checkNotErrored(context)) {
			response = Wiimmfi.getOnlinePlayerList(context);
		}
		else {
			response = "Bot offline due to an error: " + wiimmfi.getError().getClass().getCanonicalName() + ": " + wiimmfi.getError().getMessage();
		}
		context.sendMessage(response);
		return 1;
	}
	
}
