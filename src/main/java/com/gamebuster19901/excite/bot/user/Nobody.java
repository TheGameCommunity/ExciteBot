package com.gamebuster19901.excite.bot.user;

public class Nobody extends CustomUser {

	public static final Nobody INSTANCE = new Nobody();
	
	private Nobody() {
		super(-1);
	}
	
	@Override
	public String toString() {
		return "Nobody";
	}

	@Override
	public String getAsMention() {
		return "Nobody";
	}
}
