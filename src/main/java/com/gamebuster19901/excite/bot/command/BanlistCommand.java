package com.gamebuster19901.excite.bot.command;

//import java.time.Instant;

import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class BanlistCommand extends WiimmfiCommand{
	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!banlist").executes((context) -> {
			return sendBannedUsers(context.getSource());
		})
		.then(Commands.argument("user", StringArgumentType.greedyString()).executes((context) -> {
			//return getBanInfo(context.getSource(), context.getArgument("user", String.class));
			return 1;
		})));
	}
	
	@SuppressWarnings("rawtypes")
	public static int sendBannedUsers(MessageContext context) {
		if(context.isAdmin()) {
			String response = "";
			int amount = 0;
			for(DiscordUser user : DiscordUser.getKnownUsers()) {
				if(user.isBanned()) {
					response += user.toString() + "\n";
					amount++;
				}
			}
			context.sendMessage("There are (" + amount + ") discord users banned from using this bot. \n" + response);
			return 1;
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
			return 1;
		}
	}
	
/*	@SuppressWarnings("rawtypes")
	public static int getBanInfo(MessageContext context, String discordUser) {
		if(context.isAdmin()) {
			DiscordUser bannedUser;
			bannedUser = DiscordUser.getDiscordUser(discordUser);
			if(bannedUser == null) {
				try {
					long userid = Long.parseLong(discordUser);
					if(DiscordUser.getDiscordUser(userid) != null) {
						
					}
				}
				catch(NumberFormatException e) {
					//expected exception
				}
			}
			if(bannedUser != null) {
				//String banReason = bannedUser.getBanReason().replaceFirst("You have been ");
				Instant banTime = bannedUser.getBanExpireTime();
				//context.sendMessage(discordUser.toString() + " was banned for the following reason: \n" + );
			}
			context.sendMessage("There are no banned discord users with the username or id of: " + discordUser);
		}
		
	}*/
	
}
