package com.gamebuster19901.excite.bot.command;

import com.mojang.brigadier.CommandDispatcher;

public class ChangelogCommand {

	private static final String changelog = 
			"Everyone,\n" + 
			"\n" + 
			"I've been updated!" +
			"\n" + 
			"Here is a summary of the changes:\n" + 
			"\n" + 
			" * LOTS of back end changes involving command parsing (https://github.com/TheGameCommunity/ExciteBot/commit/6b553095d08d3859934f81600908233c9278f7f2)\n" + 
			"\n" + 
			" * Most arguments for commands can be surrounded by quotation marks `\"\"`. This is useful if an argument you want to specify has a space in it.\n" +
			" \n" + 
			" * Command arguments containing special characters (ex: `#`, `/`) are now parsed properly\n" + 
			" \n" + 
			" * Nicknamed discord users now work properly with !whois and other commands" + 
			"\n\n" + 
			"If there are bugs, please create a bug report at <https://github.com/Gamebuster19901/ExciteBot/issues> or ping <@138454176718913536>\n" + 
			"\n" + 
			"Cheers!\n" + 
			"-Gamebuster";
	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		dispatcher.register(Commands.userGlobal("changelog").executes((context) -> {
			return message(context.getSource());
		}));
	}
	
	@SuppressWarnings("rawtypes")
	private static int message(CommandContext context) {
		if(context.isOperator()) {
				context.sendMessage(changelog);
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
