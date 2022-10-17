package com.gamebuster19901.excite.bot.audit.ban;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.Named;

public interface Banee extends Named<Long> {
	
	public String getName();
	
	@SuppressWarnings("rawtypes")
	public default Ban getLongestActiveBan(MessageContext context) {
		Ban longest = null;
		for(Ban ban : Ban.getBansOfID(context, this.getID())) {
			if(ban.isActive()) {
				if(longest == null || ban.getBanExpireTime().isAfter(longest.getBanExpireTime())) {
					longest = ban;
				}
			}
		}
		return longest;
	}
	
	@SuppressWarnings("rawtypes")
	public static Banee getBanee(MessageContext context, long id) {
		DiscordUser discord = DiscordUser.getDiscordUserIncludingUnknown(context, id);
		Player player = Player.getPlayerByID(context, (int) id);
		
		if(!player.isKnown()) {
			return discord;
		}
		return player;
	}
	
	@Nullable
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static Banee getBanee(MessageContext context, String name) {
		DiscordUser[] discords = DiscordUser.getDiscordUsersWithUsername(context, name);
		Player[] players = Player.getPlayersByName(context, name);
		int amount = discords.length + players.length;
		if(amount == 1) {
			if(discords.length == 1) {
				return discords[0];
			}
			return players[0];
		}
		else if(amount < 1) {
			context.sendMessage("Could not find a discord user or player named " + name);
		}
		else if(amount > 1) {
			String ret = name + " is ambiguous, please supply an ID instead.\n\nAmbiguities\n\n:";
			if(discords.length > 0) {
				ret = ret + "Discord Users:\n";
				for(DiscordUser discord : discords) {
					ret = ret + discord.toDetailedString();
				}
			}
			if(players.length > 0) {
				ret = ret + "Profiles:\n";
				for(Player player : players) {
					ret = ret + player.toFullString();
				}
			}
			context.sendMessage(ret);
		}
		return null;
	}
	
}
