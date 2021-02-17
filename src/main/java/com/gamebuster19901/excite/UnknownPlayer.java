package com.gamebuster19901.excite;

public class UnknownPlayer extends Player {
	
	public String name = "UNKNOWN_PLAYER";
	public String friendCode = "NOT REAL";
	
	public UnknownPlayer() {
		super(-1);
	}
	
	public UnknownPlayer(int id) {
		super(id);
	}
	
	public UnknownPlayer(String name) {
		this();
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getFriendCode() {
		return friendCode;
	}
	
	@Override
	public boolean isLegacy() {
		return false;
	}
	
	@Override
	public boolean isVerified() {
		return false;
	}
	
	@Override
	public boolean isBot() {
		return false;
	}
	
	@Override
	public boolean isBanned() {
		return false;
	}
	
	@Override
	public boolean isOnline() {
		return false;
	}
	
	@Override
	public boolean isHosting() {
		return false;
	}
	
	@Override
	public boolean isKnown() {
		return false;
	}

	@Override
	public long getDiscord() {
		return -1;
	}
	
	@Override
	public void setDiscord(long discordId) {
		throw new AssertionError();
	}
}
