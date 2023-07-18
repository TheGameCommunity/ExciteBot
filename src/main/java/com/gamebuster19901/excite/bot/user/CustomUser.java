package com.gamebuster19901.excite.bot.user;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.gamebuster19901.excite.Main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

abstract class CustomUser implements User {

	protected String name;
	protected String discriminator;
	protected long id;
	
	public CustomUser(String name, String discriminator, long id) {
		this(id);
		this.name = name;
		this.discriminator = discriminator;
	}
	
	public CustomUser(String name, String discriminator) {
		this(name, discriminator, -1);
	}
	
	public CustomUser(long id) {
		this.id = id;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getIdLong() {
		return id;
	}

	@Override
	public String getDiscriminator() {
		return discriminator;
	}

	@Override
	public String getAvatarId() {
		return null;
	}

	@Override
	public String getDefaultAvatarId() {
		return "0";
	}

	@Override
	public CacheRestAction<Profile> retrieveProfile() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAsTag() {
		return getName() + getDiscriminator();
	}

	@Override
	public boolean hasPrivateChannel() {
		return false;
	}

	@Override
	public CacheRestAction<PrivateChannel> openPrivateChannel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Guild> getMutualGuilds() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean isBot() {
		return false;
	}

	@Override
	public boolean isSystem() {
		return true;
	}

	@Override
	public JDA getJDA() {
		return Main.discordBot.jda;
	}

	@Override
	public EnumSet<UserFlag> getFlags() {
		return UserFlag.getFlags(0);
	}

	@Override
	public int getFlagsRaw() {
		return 0;
	}
	

}
