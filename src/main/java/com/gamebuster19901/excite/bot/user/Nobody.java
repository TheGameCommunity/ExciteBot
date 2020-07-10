package com.gamebuster19901.excite.bot.user;

public class Nobody extends UnloadedDiscordUser {

	public static final Nobody INSTANCE = new Nobody();
	
	private Nobody() {
		super(-1);
	}

	@Override
	public String toString() {
		return "nobody";
	}
}
