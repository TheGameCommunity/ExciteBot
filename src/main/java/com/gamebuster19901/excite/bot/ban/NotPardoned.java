package com.gamebuster19901.excite.bot.ban;

import com.gamebuster19901.excite.bot.command.MessageContext;

public class NotPardoned extends Pardon{

	public static final NotPardoned INSTANCE = new NotPardoned();
	
	@SuppressWarnings("rawtypes")
	private NotPardoned() {
		super(new MessageContext(), -1l);
	}

	
	
}
