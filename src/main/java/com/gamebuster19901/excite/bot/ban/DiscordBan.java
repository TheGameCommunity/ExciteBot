package com.gamebuster19901.excite.bot.ban;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.BooleanPreference;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.DurationPreference;
import com.gamebuster19901.excite.bot.user.InstantPreference;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.FileUtils;

public class DiscordBan extends Verdict implements OutputCSV {
	
	private LongPreference bannedDiscordId;
	private StringPreference bannedUsername;
	
	@SuppressWarnings("rawtypes")
	public DiscordBan(MessageContext context, DiscordUser discordUser, Duration duration) {
		this(context, discordUser.toString(), discordUser.getId(), duration);
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordBan(MessageContext context, long discordId, Duration duration) {
		this(context, new UnknownDiscordUser(discordId).toString(), discordId, duration);
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordBan(MessageContext context, String name, long discordId, Duration duration) {
		super(context);
		//banDuration = new DurationPreference(duration);
		//banExpire = new InstantPreference(Instant.now().plus(duration));
		bannedDiscordId = new LongPreference(discordId);
		bannedUsername = new StringPreference(name);
	}
	
	
	private DiscordBan() {
		super();
	}
	
	@Override
	public String toCSV() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static boolean isUserBanned(DiscordUser user) {
		for(Entry<Long, Verdict> banEntry : Verdict.VERDICTS.entrySet()) {
			Verdict ban = banEntry.getValue();
			if(ban.getVerdictType() == DiscordBan.class) {
				/*if(banEntry.bannedDiscordId.getValue() == user.getId()) {
					if(!banEntry.getValue().pardoned.getValue()) {
						return true;
					}
				}*/
			}
		}
		return false;
	}
	
	public static boolean isUserBanned(long discordId) {
		return isUserBanned(DiscordUser.getDiscordUserIncludingUnknown(discordId));
	}
	
	public static boolean isProfileBanned(Player profile) {
		if(profile.getDiscord() != -1) {
			if(isUserBanned(profile.getDiscord())) {
				return true;
			}
		}
		return isUserBanned(profile.getDiscord());
	}
	
	public DiscordBan[] getBansOfUser(DiscordUser user) {
		return getBansOfUser(user.getId());
	}
	
	public DiscordBan[] getBansOfUser(long id) {
		if(id == -1 || id == -2) {
			throw new AssertionError();
		}
		HashSet<DiscordBan> bans = new HashSet<DiscordBan>();
		/*for(Entry<Long,DiscordBan> banEntry : VERDICTS.entrySet()) {
			DiscordBan ban = banEntry.getValue();
			if(ban.bannedDiscordId.getValue() == id) {
				bans.add(ban);
			}
		}*/
		return bans.toArray(new DiscordBan[]{});
	}
	
	public long getBannedDiscordId() {
		return bannedDiscordId.getValue();
	}
	
	public DiscordUser getBannedDiscord() {
		return DiscordUser.getDiscordUserIncludingUnknown(bannedDiscordId.getValue());
	}
	
	public DiscordUser getBannerDiscord() {
		if(verdictId.getValue() == -1) {
			return ConsoleUser.INSTANCE;
		}
		return DiscordUser.getDiscordUser(getBannerDiscordId());
	}
	
}
