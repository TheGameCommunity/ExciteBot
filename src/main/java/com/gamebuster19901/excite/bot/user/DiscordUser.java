package com.gamebuster19901.excite.bot.user;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.output.OutputCSV;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiscordUser implements OutputCSV{
	
	private static final HashSet<DiscordUser> knownUsers = new HashSet<DiscordUser>();
	
	private User user;
	private final long id;
	UserPreferences preferences;
	
	public DiscordUser(User user) {
		if(user == null) {
			throw new NullPointerException();
		}
		this.user = user;
		this.id = user.getIdLong();
		this.preferences = new UserPreferences(this);
	}
	
	public DiscordUser(String name, String discriminator) {
		this(getJDAUser(name, discriminator));
	}
	
	@Nullable
	public User getJDAUser() {
		return user;
	}
	
	public boolean isValid() {
		return user != null;
	}
	
	public long getId() {
		return id;
	}
	
	@SuppressWarnings("rawtypes")
	public void ban(MessageContext context, Duration duration, String reason) {
		this.preferences.ban(context, duration, reason);
	}
	
	public void pardon() {
		this.preferences.pardon();
	}
	
	@Override
	public String toCSV() {
		return preferences.toCSV();
	}
	
	public Set<Player> getProfiles() {
		return preferences.getProfiles();
	}
	
	public boolean isBanned() {
		return preferences.isBanned();
	}
	
	@SuppressWarnings("rawtypes")
	public void sentCommand(MessageContext context) {
		this.preferences.sentCommand(context);
	}
	
	public void sendMessage(String message) {
		user.openPrivateChannel().complete().sendMessage(message).complete();
	}
	
	@SuppressWarnings("rawtypes")
	public void sendMessage(MessageContext context, String message) {
		if(context.isGuildMessage()) {
			GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) context.getEvent();
			User receiver = this.getJDAUser();
			if(e.getGuild().isMember(receiver)) {
				if(e.getChannel().canTalk(e.getGuild().getMember(receiver))) {
					e.getChannel().sendMessage(message).complete();
					return;
				}
			}
		}
		sendMessage(message);
	}
	
	public static void addUser(DiscordUser user) {
		knownUsers.add(user);
	}
	
	public static final User getJDAUser(long id) {
		return Main.discordBot.jda.getUserById(id);
	}
	
	public static final User getJDAUser(String name, String discriminator) {
		if(Main.discordBot != null) {
			return Main.discordBot.jda.getUserByTag(name, discriminator);
		}
		return null;
	}
	
	public static final User getJDAUser(String discriminator) {
		if(Main.discordBot != null) {
			System.out.println(discriminator);
			return Main.discordBot.jda.getUserByTag(discriminator);
		}
		return null;
	}
	
	public static final DiscordUser getDiscordUser(long id) {
		for(DiscordUser user : knownUsers) {
			if(user.getId() == id) {
				return user;
			}
		}
		User JDAUser = getJDAUser(id);
		if(JDAUser != null) {
			DiscordUser user = new DiscordUser(JDAUser);
			addUser(user);
			return user;
		}
		return null;
	}
	
	public static final DiscordUser getDiscordUser(String discriminator) {
		for(DiscordUser user : knownUsers) {
			if(user.getJDAUser().getAsTag().equalsIgnoreCase(discriminator)) {
				return user;
			}
		}
		User JDAUser = getJDAUser(discriminator);
		if(JDAUser != null) {
			DiscordUser user = new DiscordUser(JDAUser);
			addUser(user);
			return user;
		}
		return null;
	}
	
	public static final DiscordUser[] getKnownUsers() {
		return knownUsers.toArray(new DiscordUser[]{});
	}
	
	public static final void updateDiscordUserListFile() {
		
	}
	
	public static void updateCooldowns() {
		for(DiscordUser user : getKnownUsers()) {
			user.preferences.updateCooldowns();
		}
	}
}
