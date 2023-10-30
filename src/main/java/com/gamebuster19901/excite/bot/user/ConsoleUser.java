package com.gamebuster19901.excite.bot.user;

import com.gamebuster19901.excite.Main;

import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

public class ConsoleUser extends CustomUser {
	
	public static final long CONSOLE_USER_ID = 0;
	
	private static final String name = "CONSOLE";
	
	@Deprecated
	public ConsoleUser() {
		super(name, "0000", CONSOLE_USER_ID);
	}

	@Override
	public String getAsMention() {
		return name;
	}
	
	public static ConsoleUser getConsoleUser() {
		return Main.CONSOLE;
	}
	
	@Override
	public CacheRestAction<PrivateChannel> openPrivateChannel() {
		throw new UnsupportedOperationException();
	}
	
	public void sendMessage(String message) {
		DiscordUser.sendMessage(this, message);
	}

	@Override
	public String getGlobalName() {
		return name;
	}
	
}
