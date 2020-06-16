package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

@SuppressWarnings("rawtypes") 
public class OnlineCommand extends WiimmfiCommand{

	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		LiteralCommandNode<MessageContext> node = dispatcher.register(Commands.literal("!online").executes((command) -> {
			return sendResponse(command.getSource());
		}));
		
		dispatcher.register(Commands.literal("!o").redirect(node));
	}
	
	public static int sendResponse(MessageContext context) {
		Wiimmfi wiimmfi = Main.discordBot.getWiimmfi();
		String response;
		if(checkNotErrored(context)) {
			response = Wiimmfi.getOnlinePlayerList(context);
		}
		else {
			response = "Bot offline due to an error: \n\n" + StacktraceUtil.getStackTrace(wiimmfi.getError());
		}
		context.sendMessage(response);
		return 1;
	}
	
}
