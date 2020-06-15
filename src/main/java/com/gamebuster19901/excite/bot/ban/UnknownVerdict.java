package com.gamebuster19901.excite.bot.ban;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.common.preferences.LongPreference;

public class UnknownVerdict extends Verdict{

	protected UnknownVerdict() {
		super();
	}
	
	protected UnknownVerdict(long verdictId) {
		super();
		this.verdictId = new LongPreference(verdictId);
	}
	
	@Override
	public long getBannerDiscordId() {
		throw new AssertionError();
	}

	@Override
	protected Verdict parseVerdict(CSVRecord csv) {
		throw new AssertionError();
	}

	public String toString() {
		if(this.verdictId != null) {
			return "UNKNOWN_VERDICT(" + verdictId.getValue() + ")";
		}
		return "UNKNOWN_VERDICT";
	}
	
}
