package com.gamebuster19901.excite.bot.audit;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.common.preferences.LongPreference;

public class UnknownAudit extends Audit{

	public static final UnknownAudit DEFAULT_INSTANCE = new UnknownAudit();
	
	protected UnknownAudit() {
		super();
	}
	
	protected UnknownAudit(long auditId) {
		super();
		this.auditId = new LongPreference(auditId);
	}
	
	@Override
	public long getIssuerId() {
		throw new AssertionError();
	}
	
	@Override
	public long getAuditId() {
		return -1;
	}

	@Override
	protected Audit parseAudit(CSVRecord csv) {
		throw new AssertionError();
	}

	public String toString() {
		if(this.auditId != null) {
			return "UNKNOWN_VERDICT(" + auditId.getValue() + ")";
		}
		return "UNKNOWN_VERDICT";
	}
	
}
