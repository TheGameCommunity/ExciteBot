package com.gamebuster19901.excite.bot.command;

import com.mojang.brigadier.CommandDispatcher;

public class HelpCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		dispatcher.register(Commands.userGlobal("help").executes((context) -> {
			return sendHelpInfo(context.getSource());
		}));
	}
	
	@SuppressWarnings("rawtypes")
	private static int sendHelpInfo(CommandContext context) {
		context.sendMessage("For help, see <https://github.com/gamebuster19901/ExciteBot/wiki>\n\nTo report a bug, create a new issue at <https://gamebuster19901.com/ExciteBot/issues> or notify the bot owner");
		return 1;
	}
	
}
