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
import com.gamebuster19901.excite.util.TimeUtils;

public abstract class Ban extends Verdict{

	protected static final int DB_VERSION = 0;
	
	protected DurationPreference banDuration;
	protected InstantPreference banExpire;
	protected LongPreference pardon;
	protected StringPreference reason;
	
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
		this(context, reason, banDuration, banExpire, NotPardoned.INSTANCE.verdictId.getValue());
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
	
	@SuppressWarnings("rawtypes")
	public void pardon(MessageContext context) {
		if(!this.isPardoned()) {
			Pardon pardon = new Pardon(context, this);
			this.pardon = pardon.verdictId;
			Verdict.addVerdict(pardon);
		}
		else {
			context.sendMessage("Already pardoned!");
		}
	}
	
	public boolean isPardoned() {
		return pardon.getValue() != NotPardoned.INSTANCE.getVerdictId();
	}
	
	public boolean isActive() {
		return banExpire.getValue().compareTo(Instant.now()) < 0 && !isPardoned();
	}
	
	public boolean endsAfter(Ban ban) {
		return banExpire.getValue().compareTo(ban.banExpire.getValue()) > 0;
	}
	
	public String getReason() {
		return (String)reason.getValue();
	}
	
	public Instant getBanExpireTime() {
		return banExpire.getValue();
	}
	
	@Override
	protected Ban parseVerdict(CSVRecord record) {
		super.parseVerdict(record);
		
		//0-6 is Verdict
		//7 is ban version
		banDuration.setValue(Duration.parse(record.get(8)));
		banExpire.setValue(Instant.parse(record.get(9)));
		pardon.setValue(Long.parseLong(record.get(10)));
		reason.setValue(record.get(11));
		
		return this;
	}
	
	@Override
	public List<Object> getParameters() {
		List<Object> params = super.getParameters();
		params.addAll(Arrays.asList(new Object[] {new Integer(DB_VERSION), banDuration, banExpire, pardon, reason}));
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
	
}
