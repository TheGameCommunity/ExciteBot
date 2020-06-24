package com.gamebuster19901.excite.bot.ban;

import java.time.Duration;
import java.time.Instant;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.IntegerPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.util.TimeUtils;

public class ProfileBan extends Ban {
	private static final int DB_VERSION = 0;
	
	private IntegerPreference bannedPlayer;
	private StringPreference bannedUsername;
	
	@SuppressWarnings("rawtypes")
	public ProfileBan(MessageContext context, Player bannedPlayer) {
		this(context, "", bannedPlayer);
	}
	
	@SuppressWarnings("rawtypes")
	public ProfileBan(MessageContext context, String reason, Player bannedPlayer) {
		this(context, reason, TimeUtils.FOREVER, bannedPlayer);
	}
	
	@SuppressWarnings("rawtypes")
	public ProfileBan(MessageContext context, Duration banDuration, Player bannedPlayer) {
		this(context, "", banDuration, bannedPlayer);
	}
	
	@SuppressWarnings("rawtypes")
	public ProfileBan(MessageContext context, String reason, Duration banDuration, Player bannedPlayer) {
		this(context, reason, banDuration, Instant.now().plus(banDuration), bannedPlayer);
	}
	
	@SuppressWarnings("rawtypes")
	public ProfileBan(MessageContext context, String reason, Duration banDuration, Instant banExpire, Player bannedPlayer) {
		this(context, reason, banDuration, banExpire, NotPardoned.INSTANCE.verdictId.getValue(), bannedPlayer);
	}
	
	@SuppressWarnings("rawtypes")
	public ProfileBan(MessageContext context, String reason, Duration banDuration, Instant banExpire, long pardon, Player bannedPlayer) {
		this(context, reason, Instant.now(), banDuration, banExpire, pardon, bannedPlayer);
	}
	
	@SuppressWarnings("rawtypes")
	public ProfileBan(MessageContext context, String reason, Instant dateIssued, Duration banDuration, Instant banExpire, long pardon, Player bannedPlayer) {
		super(context, reason, dateIssued, banDuration, banExpire, pardon);
		this.bannedPlayer = new IntegerPreference(bannedPlayer.getPlayerID());
		bannedUsername = new StringPreference(bannedPlayer.getName());
	}
	
	public int getBannedPlayerId() {
		return bannedPlayer.getValue();
	}
	
	@Override
	public ProfileBan parseVerdict(CSVRecord record) {
		super.parseVerdict(record);
		
		//0-6 is Verdict
		//7-11 is Ban
		//12 is profileBan version
		bannedPlayer.setValue(Integer.parseInt(record.get(13)));
		bannedUsername.setValue(record.get(14));
		
		return this;
	}
	
	
	public static boolean isProfileBanned(Player profile) {
		if(profile.getDiscord() != -1) {
			if(DiscordBan.isDiscordBanned(profile.getDiscord())) {
				return true;
			}
		}
		for(Entry<Long, ProfileBan> verdict : Verdict.PROFILE_BANS.entrySet()) {
			ProfileBan ban = (ProfileBan) verdict.getValue();
			if(ban.bannedPlayer.getValue() == profile.getPlayerID()) {
				if(!ban.isPardoned()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isProfileBanned(int profileID) {
		return isProfileBanned(Player.getPlayerByID(profileID));
	}
}
