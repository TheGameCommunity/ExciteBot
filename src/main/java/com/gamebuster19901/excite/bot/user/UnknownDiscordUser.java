package com.gamebuster19901.excite.bot.user;

public class UnknownDiscordUser extends UnloadedDiscordUser {

	private String name = "UNKNOWN_DISCORD_USER";
	private String discriminator = "-1";
	
	public UnknownDiscordUser(String name, String discriminator) {
		super(-1);
		this.name = name;
		this.discriminator = discriminator;
	}
	
	public UnknownDiscordUser(long id) {
		super(id);
	}
	
	@Override
	public String toString() {
		if(hasID()) {
			return this.name + "#" + discriminator + " (" + getID() + ")";
		}
		return this.name + "#" + discriminator;
	}
	
	public boolean hasID() {
		return this.getID() != -1;
	}
}
