package com.gamebuster19901.excite.bot.ban;

import com.gamebuster19901.excite.UnknownPlayer;
import com.gamebuster19901.excite.bot.command.MessageContext;

public class NotProfileBanned extends ProfileBan implements NotBanned {

	public static final NotProfileBanned INSTANCE = new NotProfileBanned();
	
	@SuppressWarnings("rawtypes")
	private NotProfileBanned() {
		super(new MessageContext(), UnknownPlayer.INSTANCE);
	}
	
}
