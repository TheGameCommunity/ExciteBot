package com.gamebuster19901.excite.bot.ban;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.DurationPreference;
import com.gamebuster19901.excite.bot.user.InstantPreference;
import com.gamebuster19901.excite.util.DataPoint;
import com.gamebuster19901.excite.util.TimeUtils;

public abstract class Ban extends Audit{

	protected static final int DB_VERSION = 1;
	
	protected DurationPreference banDuration;
	protected InstantPreference banExpire;
	protected LongPreference pardon = new LongPreference(NotPardoned.INSTANCE.getAuditId());
	protected StringPreference bannedUsername;
	
	@SuppressWarnings("rawtypes")
	public Ban(MessageContext context) {
		this(context, "");
	}
	
	@SuppressWarnings("rawtypes")
	public Ban(MessageContext context, String reason) {
		this(context, reason, TimeUtils.FOREVER);
	}
	
	@SuppressWarnings("rawtypes")
	public Ban(MessageContext context, Duration banDuration) {
		this(context, "", banDuration);
	}
	
	@SuppressWarnings("rawtypes")
	public Ban(MessageContext context, String reason, Duration banDuration) {
		this(context, reason, banDuration, Instant.now().plus(banDuration));
	}
	
	@SuppressWarnings("rawtypes")
	public Ban(MessageContext context, String reason, Duration banDuration, Instant banExpire) {
		this(context, reason, banDuration, banExpire, NotPardoned.INSTANCE.auditId.getValue());
	}
	
	@SuppressWarnings("rawtypes")
	public Ban(MessageContext context, String reason, Duration banDuration, Instant banExpire, long pardon) {
		this(context, reason, Instant.now(), banDuration, banExpire, pardon);
	}
	
	@SuppressWarnings("rawtypes")
	public Ban(MessageContext context, String reason, Instant dateIssued, Duration banDuration, Instant banExpire, long pardon) {
		super(context, reason, dateIssued);
		this.banDuration = new DurationPreference(banDuration);
		this.banExpire = new InstantPreference(banExpire);
		this.pardon = new LongPreference(pardon);
	}
	
	public Ban() {
		super();
	}
	
	@SuppressWarnings("rawtypes")
	public void pardon(MessageContext context, Pardon pardon) {
		if(!this.isPardoned()) {
			this.pardon = pardon.auditId;
			Audit.addAudit(pardon);
			context.sendMessage("Pardoned " + getBannedUsername());
		}
		else {
			context.sendMessage("Already pardoned!");
		}
	}
	
	@DataPoint
	public boolean isPardoned() {
		return pardon.getValue() != NotPardoned.INSTANCE.getAuditId();
	}
	
	@DataPoint
	public boolean isActive() {
		return Instant.now().isBefore(banExpire.getValue()) && !isPardoned();
	}
	
	@DataPoint
	public abstract String getBannedUsername();
	
	public boolean endsAfter(Ban ban) {
		return banExpire.getValue().compareTo(ban.banExpire.getValue()) > 0;
	}
	
	public Instant getBanExpireTime() {
		return banExpire.getValue();
	}
	
	@Override
	protected Ban parseAudit(CSVRecord record) {
		super.parseAudit(record);
		
		//0-6 is audit
		//7 is ban version
		banDuration = new DurationPreference(Duration.parse(record.get(8)));
		banExpire = new InstantPreference(Instant.parse(record.get(9)));
		pardon = new LongPreference(Long.parseLong(record.get(10)));
		
		return this;
	}
	
	@Override
	public List<Object> getParameters() {
		List<Object> params = super.getParameters();
		params.addAll(Arrays.asList(new Object[] {new Integer(DB_VERSION), banDuration, banExpire, pardon}));
		return params;
	}
	
	public static Ban[] getBansOfUser(DiscordUser user) {
		HashSet<Ban> bans = new HashSet<Ban>();
		Set<Player> profiles = user.getProfiles();
		for(Entry<Long, Ban> verdict : BANS.entrySet()) {
			Ban ban = verdict.getValue();
			if(ban instanceof DiscordBan) {
				if(((DiscordBan)ban).getBannedDiscordId() == user.getId()) {
					bans.add(ban);
				}
			}
			else if (ban instanceof ProfileBan){
				for(Player profile : profiles) {
					if(((ProfileBan)ban).getBannedPlayerId() == profile.getPlayerID()) {
						bans.add(ban);
					}
				}
			}
		}
		return bans.toArray(new DiscordBan[]{});
	}
	
	public static Ban[] getBansOfUser(long id) {
		if(id == -1 || id == -2) {
			throw new AssertionError();
		}
		return getBansOfUser(DiscordUser.getDiscordUserIncludingUnknown(id));
	}
	
	public static Ban getBanById(long id) throws IllegalArgumentException {
		Ban ban = Audit.BANS.get(id);
		if(ban == null) {
			throw new IllegalArgumentException("No ban with id " + id + " exists ");
		}
		return ban;
	}
	
}
