package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

@SuppressWarnings("rawtypes") 
public class OnlineCommand extends WiimmfiCommand{

	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		LiteralArgumentBuilder<MessageContext> builder = Commands.literal("!online").executes((command) -> {
			return sendResponse(command.getSource());
		});
		
		dispatcher.register(Commands.literal("!o").executes(builder.getCommand()));
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
