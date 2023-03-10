package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

@SuppressWarnings("rawtypes") 
public class OnlineCommand {

	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		LiteralArgumentBuilder<CommandContext> builder = Commands.userGlobal("online").executes((command) -> {
			return sendResponse(command.getSource(), command);
		});
		
		dispatcher.register(builder);
		dispatcher.register(Commands.literal("o").executes(builder.getCommand()));
	}
	
	public static int sendResponse(CommandContext context, com.mojang.brigadier.context.CommandContext<CommandContext> cmdContext) {
		Wiimmfi wiimmfi = Main.discordBot.getWiimmfi();
		String response;
		if(wiimmfi.getError() == null) {
			response = "Players Online " + Wiimmfi.getOnlinePlayerList(cmdContext.getInput().equals("online"));
		}
		else {
			response = "Bot offline due to an error: \n\n" + StacktraceUtil.getStackTrace(wiimmfi.getError());
		}
		context.sendMessage(response);
		return 1;
	}
	
}
