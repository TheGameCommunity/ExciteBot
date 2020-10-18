package com.gamebuster19901.excite.bot.audit.ban;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.audit.Audit;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.IntegerPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.util.TimeUtils;

public class ProfileBan extends Ban {
	private static transient final int DB_VERSION = 0;
	
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
		this(context, reason, banDuration, TimeUtils.fromNow(banDuration), bannedPlayer);
	}
	
	@SuppressWarnings("rawtypes")
	public ProfileBan(MessageContext context, String reason, Duration banDuration, Instant banExpire, Player bannedPlayer) {
		this(context, reason, banDuration, banExpire, NotPardoned.INSTANCE.getAuditId(), bannedPlayer);
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
	
	public ProfileBan() {}
	
	public int getBannedPlayerId() {
		return bannedPlayer.getValue();
	}

	@Override
	public String getBannedUsername() {
		return (String) bannedUsername.getValue();
	}
	
	@Override
	public ProfileBan parseAudit(CSVRecord record) {
		super.parseAudit(record);
		
		//0-7 is Verdict
		//8-11 is Ban
		//12 is profileBan version
		bannedPlayer = new IntegerPreference(Integer.parseInt(record.get(13).substring(1)));
		bannedUsername = new StringPreference(record.get(14));
		
		return this;
	}
	
	@Override
	public List<Object> getParameters() {
		List<Object> params = super.getParameters();
		params.addAll(Arrays.asList(new Object[] {DB_VERSION, "`" + bannedPlayer, bannedUsername}));
		return params;
	}
	
	
	public static boolean isProfileBanned(Player profile) {
		if(profile.getDiscord() != -1) {
			if(DiscordBan.isDiscordBanned(profile.getDiscord())) {
				return true;
			}
		}
		for(Entry<Long, ProfileBan> verdict : Audit.PROFILE_BANS.entrySet()) {
			ProfileBan ban = (ProfileBan) verdict.getValue();
			if(ban.bannedPlayer.getValue() == profile.getPlayerID()) {
				if(ban.isActive()) {
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
