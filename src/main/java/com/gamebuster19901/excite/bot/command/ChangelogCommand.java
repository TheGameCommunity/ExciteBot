package com.gamebuster19901.excite.bot.command;

import com.mojang.brigadier.CommandDispatcher;

import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class ChangelogCommand {

	private static final String changelog = 
			"Everyone,\n" + 
			"\n" + 
			"I've been updated!" +
			"\n" + 
			"Here is a summary of the changes:\n" + 
			"\n" + 
			" * The bot now uses slash commands, you may type a command using `/command`\n" + 
			"\n" + 
			" * The bot will now provide suggestions as you type\n" +
			"   Known issue: suggestions are buggy, this is an issue with discord itself, I've done the best I can to mitigate this issue: https://github.com/discord/discord-api-docs/issues/5878\n\n" + 
			" * Fixed issue where new users would not be added to the database, preventing notifications from occuring" + 
			" \n" + 
			" * Consider the bot as being in an open beta, not everything has been smoothly migrated to slash commands properly yet" + 
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
		MessageCreateData message = MessageCreateData.fromContent(changelog);
		if(context.isOperator()) {
				context.replyMessage(message, false, false);
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
