package com.gamebuster19901.excite.bot.user;

public class UnknownDiscordUser extends UnloadedDiscordUser {

	private String name = "UNKNOWN_DISCORD_USER";
	private String discriminator = "0";
	
	public UnknownDiscordUser(String name, String discriminator) {
		super(0);
		this.name = name;
		this.discriminator = discriminator;
	}
	
	public UnknownDiscordUser(long id) {
		super(id);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		if(hasID()) {
			return this.name + "#" + discriminator + " (" + getID() + ")";
		}
		return this.name + "#" + discriminator;
	}
	
	@Override
	public boolean isKnown() {
		return false;
	}
	
	public boolean hasID() {
		return this.getID() != 0;
	}
}
