package com.gamebuster19901.excite.bot.ban;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.TimeUtils;

public class DiscordBan extends Ban {

	private static final int DB_VERSION = 1;
	
	private LongPreference bannedDiscordId;
	private StringPreference bannedUsername;
	
	@SuppressWarnings("rawtypes")
	public DiscordBan(MessageContext context, DiscordUser bannedDiscordUser) {
		this(context, "", bannedDiscordUser);
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordBan(MessageContext context, String reason, DiscordUser bannedDiscordUser) {
		this(context, reason, TimeUtils.FOREVER, bannedDiscordUser);
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordBan(MessageContext context, Duration banDuration, DiscordUser bannedDiscordUser) {
		this(context, "", banDuration, bannedDiscordUser);
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordBan(MessageContext context, String reason, Duration banDuration, DiscordUser bannedDiscordUser) {
		this(context, reason, banDuration, TimeUtils.fromNow(banDuration), bannedDiscordUser);
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordBan(MessageContext context, String reason, Duration banDuration, Instant banExpire, DiscordUser bannedDiscordUser) {
		this(context, reason, banDuration, banExpire, NotPardoned.INSTANCE.getAuditId(), bannedDiscordUser);
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordBan(MessageContext context, String reason, Duration banDuration, Instant banExpire, long pardon, DiscordUser bannedDiscordUser) {
		this(context, reason, Instant.now(), banDuration, banExpire, pardon, bannedDiscordUser);
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordBan(MessageContext context, String reason, Instant dateIssued, Duration banDuration, Instant banExpire, long pardon, DiscordUser bannedDiscordUser) {
		super(context, reason, dateIssued, banDuration, banExpire, pardon);
		bannedDiscordId = new LongPreference(bannedDiscordUser.getId());
		bannedUsername = new StringPreference(bannedDiscordUser.toString());
	}
	
	public DiscordBan() {
		super();
	}
	
	@Override
	public DiscordBan parseAudit(CSVRecord record) {
		super.parseAudit(record);
		
		//0-6 is Verdict
		//7-10 is Ban
		//11 is discordBan version
		bannedDiscordId = new LongPreference(Long.parseLong(record.get(12)));
		bannedUsername = new StringPreference(record.get(13));
		
		return this;
	}
	
	
	public long getBannedDiscordId() {
		return bannedDiscordId.getValue();
	}

	@Override
	public String getBannedUsername() {
		return (String) bannedUsername.getValue();
	}
	
	@Override
	public List<Object> getParameters() {
		List<Object> params = super.getParameters();
		params.addAll(Arrays.asList(new Object[] {DB_VERSION, bannedDiscordId, bannedUsername}));
		return params;
	}
	
	public static boolean isUserBanned(DiscordUser user) {
		for(Entry<Long, DiscordBan> banEntry : Audit.DISCORD_BANS.entrySet()) {
			DiscordBan ban = banEntry.getValue();
			if(ban.bannedDiscordId.getValue() == user.getId()) {
				if(!ban.isPardoned()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isDiscordBanned(long discordId) {
		return isUserBanned(DiscordUser.getDiscordUserIncludingUnknown(discordId));
	}
	
	public static DiscordBan[] getBansOfUser(DiscordUser user) {
		return getBansOfUser(user.getId());
	}
	
	public static DiscordBan[] getBansOfUser(long id) {
		if(id == -1 || id == -2) {
			throw new AssertionError();
		}
		HashSet<DiscordBan> bans = new HashSet<DiscordBan>();
		for(Entry<Long, DiscordBan> verdict : DISCORD_BANS.entrySet()) {
			DiscordBan ban = (DiscordBan) verdict.getValue();
			if(ban.bannedDiscordId.getValue() == id) {
				bans.add(ban);
			}
		}
		return bans.toArray(new DiscordBan[]{});
	}
	
}
