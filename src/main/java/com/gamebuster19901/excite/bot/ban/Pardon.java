package com.gamebuster19901.excite.bot.ban;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.command.MessageContext;

public class Pardon extends Verdict{

	@SuppressWarnings("rawtypes")
	public Pardon(MessageContext pardonContext, long banId) {
		super(pardonContext);
	}
	
	@SuppressWarnings("rawtypes")
	public Pardon(MessageContext pardonContext, Verdict verdict) {
		super(pardonContext);
		String errMessage;
		if(verdict == null || verdict instanceof Pardon) {
			throw new IllegalArgumentException(errMessage = verdict != null ? verdict.verdictId.getValue() + "" : "null");
		}
	}

	@Override
	protected Verdict parseVerdict(CSVRecord csv) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
