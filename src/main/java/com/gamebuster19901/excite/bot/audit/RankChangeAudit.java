package com.gamebuster19901.excite.bot.audit;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.PermissionPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import static com.gamebuster19901.excite.util.Permission.ADMIN_ONLY;

public class RankChangeAudit extends Audit {

	private static final int DB_VERSION = 1;
	
	StringPreference promotee;
	LongPreference promoteeDiscordId;
	
	@SuppressWarnings("rawtypes")
	public RankChangeAudit(MessageContext promoter, MessageContext<DiscordUser> promotee, String rank, boolean added) {
		super(promoter, getMessage(promoter, promotee, rank, added));
		if(promoter.isAdmin()) {
			this.secrecy = new PermissionPreference(ADMIN_ONLY);
		}
	}
	
	protected RankChangeAudit() {
		super();
	}
	
	@Override
	public Audit parseAudit(CSVRecord record) {
		super.parseAudit(record);
		//0-6 is audit
		//7 is RankChanceAudit version
		int i = super.getRecordSize();
		if(Integer.parseInt(record.get(i++)) == 0) { //8 is RankChangeAuditVersion;
			throw new IllegalArgumentException("Update RankChangeAudit!");
		}
		this.promotee = new StringPreference(record.get(i++));
		this.promoteeDiscordId = new LongPreference(record.get(i++));
		return this;
	}
	
	protected int getRecordSize() {
		return super.getRecordSize() + 3;
	}
	
	@Override
	public List<Object> getParameters() {
		List<Object> params = super.getParameters();
		params.addAll(Arrays.asList(new Object[] {new Integer(DB_VERSION), promotee, promoteeDiscordId}));
		return params;
	}
	
	@SuppressWarnings("rawtypes")
	private static final String getMessage(MessageContext promoter, MessageContext<DiscordUser> promotee, String rank, boolean added) {
		if(added) {
			return promoter.getDiscordAuthor().toDetailedString() + " made " + promotee.getDiscordAuthor().toDetailedString() + " a bot " + rank + " for " + Main.discordBot.getSelfUser().getName();
		}
		return promoter.getDiscordAuthor().toDetailedString() + " removed the bot " + rank + " rights from " + promotee.getDiscordAuthor().toDetailedString() + " for " + Main.discordBot.getSelfUser().getName();
	}
	
}