package com.gamebuster19901.excite.bot.ban;

import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.UnknownPlayer;
import com.gamebuster19901.excite.bot.command.MessageContext;

public class NotProfileBanned extends ProfileBan {

	public static final NotProfileBanned INSTANCE = new NotProfileBanned();
	
	@SuppressWarnings("rawtypes")
	private NotProfileBanned() {
		super(new MessageContext(), UnknownPlayer.INSTANCE);
	}
	
	@Override
	public NotProfileBanned parseAudit(CSVRecord record) {
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
