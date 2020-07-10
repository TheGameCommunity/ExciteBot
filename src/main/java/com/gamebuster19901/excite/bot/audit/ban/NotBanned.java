package com.gamebuster19901.excite.bot.audit.ban;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.command.MessageContext;

public class NotBanned extends Ban {
	
	public static final NotBanned INSTANCE = new NotBanned();
	
	@SuppressWarnings("rawtypes")
	private NotBanned() {
		super(new MessageContext(), "Not Banned", Duration.ZERO, Instant.MIN);
	}
	
	@Override
	public NotDiscordBanned parseAudit(CSVRecord record) {
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

	@Override
	public String getBannedUsername() {
		throw new AssertionError();
	}
	
}
