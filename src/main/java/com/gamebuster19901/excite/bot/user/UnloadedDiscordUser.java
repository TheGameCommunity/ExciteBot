package com.gamebuster19901.excite.bot.user;

import com.gamebuster19901.excite.bot.command.MessageContext;

public class UnloadedDiscordUser extends DiscordUser{

	UnloadedDiscordUser(long userId) {
		super(userId);
	}

	@Override
	public void sendMessage(String message) {}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void sendMessage(MessageContext context, String message) {
		if(context.getEvent() != this) {
			super.sendMessage(context, message);
		}
	}
	
	@Override
	public String toString() {
		return "UNLOADED_DISCORD_USER#???? " + "(" + getID() + ")"; 
	}
}
