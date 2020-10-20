package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.audit.Audit;
import com.gamebuster19901.excite.bot.audit.ban.DiscordBan;

//import java.time.Instant;

import com.mojang.brigadier.CommandDispatcher;

public class BanlistCommand {
	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("banlist").executes((context) -> {
			return sendBannedUsers(context.getSource());
		}));
	}
	
	@SuppressWarnings("rawtypes")
	public static int sendBannedUsers(MessageContext context) {
		if(context.isAdmin()) {
			String response = "Discord Users:\n\n";
			int discordAmount = 0;
			int playerAmount = 0;
			for(DiscordBan ban : Audit.getAuditsOfType(DiscordBan.class).values()) {
				if(ban.isActive()) {
					response += ban.getBannedUser().toDetailedString() + "\n";
					discordAmount++;
				}
			}
			response += "Profiles:\n\n";
			for(Player player : Player.getEncounteredPlayers()) {
				if(player.isBanned()) {
					response += player.toString() + "\n";
					playerAmount++;
				}
			}
			context.sendMessage("There are (" + discordAmount + ") discord users and " + playerAmount +" profiles banned from using this bot:\n\n" + response);
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
