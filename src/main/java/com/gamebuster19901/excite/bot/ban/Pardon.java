package com.gamebuster19901.excite.bot.ban;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;

public class Pardon extends Audit{

	private static final int DB_VERSION = 0;
	
	private LongPreference banId = new LongPreference(UnknownAudit.DEFAULT_INSTANCE.getAuditId());
	
	@SuppressWarnings("rawtypes")
	public Pardon(MessageContext pardonContext, long banId) {
		this(pardonContext, banId, "");
	}
	
	@SuppressWarnings("rawtypes")
	public Pardon(MessageContext pardonContext, Audit audit) {
		this(pardonContext, audit, "");
	}
	
	@SuppressWarnings("rawtypes")
	public Pardon(MessageContext pardonContext, long banId, String reason) {
		super(pardonContext, reason);
		this.banId.setValue(banId);
	}
	
	@SuppressWarnings("rawtypes")
	public Pardon(MessageContext pardonContext, Audit audit, String reason) {
		super(pardonContext, reason);
		String errMessage;
		if(audit == null || audit instanceof Pardon) {
			throw new IllegalArgumentException(errMessage = audit != null ? audit.auditId.getValue() + "" : "null");
		}
		this.banId.setValue(audit.getAuditId());
	}
	
	public Pardon() {
		super();
	}

	@Override
	public Audit parseAudit(CSVRecord record) {
		super.parseAudit(record);
		//0-6 is audit
		//7 is pardon version
		banId = new LongPreference(Long.parseLong(record.get(8)));
		
		return this;
	}
	
	@Override
	public List<Object> getParameters() {
		List<Object> params = super.getParameters();
		params.addAll(Arrays.asList(new Object[] {new Integer(DB_VERSION), banId}));
		return params;
	}
	
	public long getBanId() {
		return banId.getValue();
	}
	
	/*@SuppressWarnings("rawtypes")
	public static void pardon(MessageContext context, long banId, String reason) {
		if(Audit.BANS.containsKey(banId)) {
			Ban ban = Audit.BANS.get(banId);
			ban.pardon(context, reason);
		}
		else {
			context.sendMessage("Could not find ban #" + banId);
		}
	}*/
	
}
