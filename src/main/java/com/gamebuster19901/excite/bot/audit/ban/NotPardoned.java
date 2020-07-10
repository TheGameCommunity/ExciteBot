package com.gamebuster19901.excite.bot.audit.ban;

import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.command.MessageContext;

public class NotPardoned extends Pardon {

	public static transient final NotPardoned INSTANCE = new NotPardoned();
	
	@SuppressWarnings("rawtypes")
	private NotPardoned() {
		super(new MessageContext(), -1l);
	}
	
	@Override
	public NotPardoned parseAudit(CSVRecord record) {
		throw new AssertionError();
	}
	
	@Override
	public List<Object> getParameters() {
		throw new AssertionError();
	}
	
	@Override
	public long getAuditId() {
		return -1;
	}
	
}
