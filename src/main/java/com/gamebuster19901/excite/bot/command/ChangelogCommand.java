package com.gamebuster19901.excite.bot.command;

import com.mojang.brigadier.CommandDispatcher;

public class ChangelogCommand {

	private static final String changelog = 
			"@everyone\n" + 
			"\n" + 
			"If you're here, you've been added as a beta tester for <@&778524349992927252>!\n" + 
			"\n" + 
			"The bot has been migrated to a new database. Please help me test it out by using the <@&778524349992927252> bot rather than the <@&718906591633604638> bot. \n" + 
			"\n" + 
			"The <@&718906591633604638> bot is still available for use just in case there are major issue with <@&778524349992927252>, but I have cleared your settings in it so you don't receive notifications from both bots. You should now be receiving notifications from <@&778524349992927252>.\n" + 
			"\n" + 
			"If you wish to use <@&718906591633604638> again, you will have to use `notify threshold` and `notify frequency` again.\n" + 
			"\n" + 
			"Any settings you make in <@&718906591633604638> will not be saved after the beta is over, and <@&778524349992927252> will become <@&718906591633604638> after the beta.\n" + 
			"\n" + 
			"All of your excitebots profiles will remain registered, you don't need to re-register.\n" + 
			"\n" + 
			"Here is a summary of the changes:\n" + 
			"\n" + 
			" * Changed `9043` lines of code in `92` files :woozy_face: <https://github.com/Gamebuster19901/ExciteBot/pull/67/files>\n" + 
			"\n" + 
			" * Migrated the bot database to MYSQL\n" + 
			" \n" + 
			" * Added !prefix command to change the bot's prefix for the server\n" + 
			" \n" + 
			" * Added audits for the following:\n" + 
			"     * Bot Bans issued\n" + 
			"     * Bot Commands issued\n" + 
			"     * Profile name changes\n" + 
			"     * Bot Pardons issued\n" + 
			"     * When the bot discovers a new ExciteBots profile\n" + 
			"     \n" + 
			" * New Icons for !online, !whois, etc.\n" + 
			"     * <:offline:722634659170746438> indicates that the player is offline\n" + 
			"     * <:online:722604720878780416> indicates that the player is online and racing in a global room\n" + 
			"     * <:hosting:722604675379101729> indicates that the player is online and is the host of a global room\n" + 
			"     * <:searching:722624243728384000> indicates that the player is online and is searching for a global room\n" + 
			"     * <:private_room:775937299652935691> indicates that the player is in a private room\n" + 
			"     * <:hosting_private_room:775936756234453043> indicates that the player is hosting a private room\n" + 
			"     * <:friend_list:722637824440139907> indicates that the player is viewing their friends list\n" + 
			"     * <:verified:737786667582226504> indicates that the excite bot profile is registered to a discord account\n" + 
			"     * <:banned:722627043858317382> indicates that the profile is banned from using the bot (the bot will ignore the user)\n" + 
			"     * :robot: means this user is used to test the bot\n" + 
			"     * <:legacy:722635266891972699> means the profile was created before the Wiimmfi shutdown\n" + 
			"     * <:bot_admin:737821922095530095> means the user is a bot administrator (has access to admin commands and read access to audits)\n" + 
			"     * <:bot_operator:737812142841004112> means the user is a bot operator (has access to operator commands and read access to the entire DB, and limited write access)\n" + 
			"     * You can also hover over the icons to see what they mean\n" + 
			"\n" + 
			" * Users who are: in private rooms, viewing the friend list, are banned, or are a bot will not count to the notification threshold.\n" + 
			"\n" + 
			" * Video command temporarily removed\n" + 
			"\n" + 
			" * The output of the `!o` command is more suitable for mobile\n" + 
			"\n" + 
			" * `!online` has the old behavior which shows friend codes and player ids\n" + 
			"     \n" + 
			"<@&700212709257642005> and <@&752643456022741136> I will send you instructions on how to access the database tomorrow.\n" + 
			"\n" + 
			"If there are bugs, please create a bug report at <https://github.com/Gamebuster19901/ExciteBot/issues> or ping <@138454176718913536>\n" + 
			"\n" + 
			"Cheers!\n" + 
			"-Gamebuster";
	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("cl").executes((context) -> {
			return message(context.getSource());
		}));
	}
	
	@SuppressWarnings("rawtypes")
	private static int message(MessageContext context) {
		if(context.isOperator()) {
				context.sendMessage(changelog);
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
