package com.gamebuster19901.excite.bot.audit.ban;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;

public class NotDiscordBanned extends DiscordBan {
	
	public static final NotDiscordBanned INSTANCE = new NotDiscordBanned();
	
	@SuppressWarnings("rawtypes")
	private NotDiscordBanned() {
		super(new MessageContext(), "Not Banned", Duration.ZERO, Instant.MIN, new UnknownDiscordUser(-1));
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
	
}
