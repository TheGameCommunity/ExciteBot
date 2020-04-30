package com.gamebuster19901.excite.bot.user;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.output.OutputCSV;

import net.dv8tion.jda.api.entities.User;

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
	
	public void ban(Duration duration, String reason) {
		this.preferences.ban(duration, reason);
	}
	
	public void pardon() {
		this.preferences.pardon();
	}
	
	@Override
	public String toCSV() {
		String output = user.getId();
		for(Player player : getProfiles()) {
			output += "," + player.getPlayerID();
		}
		return output;
	}
	
	public Set<Player> getProfiles() {
		return preferences.getProfiles();
	}
	
	public boolean isBanned() {
		return preferences.isBanned();
	}
	
	public void sentCommand() {
		this.preferences.sentCommand();
	}
	
	public void sendMessage(String message) {
		user.openPrivateChannel().complete().sendMessage(message).complete();
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
}
