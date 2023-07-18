package com.gamebuster19901.excite.bot.user;

public class UnknownUser extends CustomUser {
	
	public UnknownUser(String name, String discriminator) {
		this(0);
		this.name = name;
		this.discriminator = discriminator;
	}
	
	public UnknownUser(long id) {
		super(id);
		this.id = 0;
	}

	@Override
	public String getAsMention() {
		return getName() + getDiscriminator();
	}

	@Override
	public String getGlobalName() {
		return getName();
	}
	

}
