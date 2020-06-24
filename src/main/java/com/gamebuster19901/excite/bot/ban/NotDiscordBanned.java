package com.gamebuster19901.excite.bot.ban;

import java.time.Duration;
import java.time.Instant;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;

public class NotDiscordBanned extends DiscordBan implements NotBanned {
	
	public static final NotDiscordBanned INSTANCE = new NotDiscordBanned();
	
	@SuppressWarnings("rawtypes")
	private NotDiscordBanned() {
		super(new MessageContext(), "Not Banned", Duration.ZERO, Instant.MIN, new UnknownDiscordUser(-1));
	}
	
}
