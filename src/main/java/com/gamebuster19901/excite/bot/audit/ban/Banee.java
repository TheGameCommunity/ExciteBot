package com.gamebuster19901.excite.bot.audit.ban;

import org.jetbrains.annotations.Nullable;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.Named;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public interface Banee<T> extends Named<T> {
	
	public String getName();
	
	public T asObj();
	
	@SuppressWarnings("rawtypes")
	public default Ban getLongestActiveBan(CommandContext context) {
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
	public static Banee getBanee(CommandContext context, long id) {
		User discord = DiscordUser.getUser(id);
		Player player = Player.getPlayerByID(context, (int) id);
		
		if(!player.isKnown()) {
			return Banee.of(discord);
		}
		return player;
	}
	
	@Nullable
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static Banee getBanee(CommandContext context, String name) {
		User[] discords = DiscordUser.getUsers(name);
		Player[] players = Player.getPlayersByName(context, name);
		int amount = discords.length + players.length;
		if(amount == 1) {
			if(discords.length == 1) {
				return Banee.of(discords[0]);
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
				for(User discord : discords) {
					ret = ret + DiscordUser.toDetailedString(discord);
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
	
	public static Banee<User> of(User user) {
		return new Banee<User>() {
			@Override
			public long getID() {
				return user.getIdLong();
			}

			@Override
			public String getName() {
				return user.getName() + user.getDiscriminator();
			}

			@Override
			public User asObj() {
				return user;
			}
		};
	}
	
	public static Banee<User> of(Member member) {
		return of(member.getUser());
	}
	
}
