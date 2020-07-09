package com.gamebuster19901.excite.bot.ban;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.common.preferences.LongPreference;

public class UnknownAudit extends Audit{

	protected UnknownAudit() {
		super();
	}
	
	protected UnknownAudit(long auditId) {
		super();
		this.auditId = new LongPreference(auditId);
	}
	
	@Override
	public long getIssuerDiscordId() {
		throw new AssertionError();
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
