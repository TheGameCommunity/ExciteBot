package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.user.ConsoleUser;

public class ConsoleContext extends MessageContext<ConsoleUser>{

	public ConsoleContext() {
		super(ConsoleUser.getConsoleUser());
	}
	
}
