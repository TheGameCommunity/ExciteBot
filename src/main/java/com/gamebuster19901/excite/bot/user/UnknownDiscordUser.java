package com.gamebuster19901.excite.bot.user;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.bot.command.MessageContext;

import net.dv8tion.jda.api.entities.User;

public class UnknownDiscordUser extends DiscordUser{

	public UnknownDiscordUser(long id) {
		super(id);
	}
	
	@Override
	@Nullable
	public User getJDAUser() {
		return null;
	}
	
	@Override
	public boolean isValid() {
		return false;
	}
	
	@Override
	public void sendMessage(String message) {
		System.out.println("Could not send message to unknown user " + id);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void sendMessage(MessageContext context, String message) {
		System.out.println("Could not send message to unknown user " + id);
	}

}
