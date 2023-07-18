package com.gamebuster19901.excite.bot.user;

import java.util.EnumSet;
import java.util.List;

import com.gamebuster19901.excite.util.Named;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

public class NamedDiscordUser implements User, Named<NamedDiscordUser> {

	private final User user;
	
	public static final NamedDiscordUser of(User user) {
		return new NamedDiscordUser(user);
	}
	
	private NamedDiscordUser(User user) {
		this.user = user;
	}
	
	@Override
	public String toDetailedString() {
		return DiscordUser.toDetailedString(this);
	}
	
	@Override
	public long getID() {
		return user.getIdLong();
	}

	@Override
	public String getName() {
		return user.getName();
	}

	@Override
	public NamedDiscordUser asObj() {
		return this; 
	}

	@Override
	public String getAsMention() {
		return user.getAsMention();
	}

	@Override
	public long getIdLong() {
		return user.getIdLong();
	}

	@Override
	@Deprecated
	public String getDiscriminator() {
		return user.getDiscriminator();
	}

	@Override
	public String getAvatarId() {
		return user.getAvatarId();
	}

	@Override
	public String getDefaultAvatarId() {
		return user.getDefaultAvatarId();
	}

	@Override
	public CacheRestAction<Profile> retrieveProfile() {
		return user.retrieveProfile();
	}

	@Override
	public String getAsTag() {
		return user.getAsTag();
	}

	@Override
	public boolean hasPrivateChannel() {
		return user.hasPrivateChannel();
	}

	@Override
	public CacheRestAction<PrivateChannel> openPrivateChannel() {
		return user.openPrivateChannel();
	}

	@Override
	public List<Guild> getMutualGuilds() {
		return user.getMutualGuilds();
	}

	@Override
	public boolean isBot() {
		return user.isBot();
	}

	@Override
	public boolean isSystem() {
		return user.isSystem();
	}

	@Override
	public JDA getJDA() {
		return user.getJDA();
	}

	@Override
	public EnumSet<UserFlag> getFlags() {
		return user.getFlags();
	}

	@Override
	public int getFlagsRaw() {
		return user.getFlagsRaw();
	}

	@Override
	public String getGlobalName() {
		return user.getGlobalName();
	}

}
