package com.gamebuster19901.excite.bot.user;

import java.util.HashSet;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.output.OutputCSV;

import net.dv8tion.jda.api.entities.User;

public class DiscordUser implements OutputCSV{
	
	private static final HashSet<DiscordUser> knownUsers = new HashSet<DiscordUser>();
	
	private User user;
	private final long id;
	private final HashSet<Player> profiles = new HashSet<Player>();
	
	public DiscordUser(User user) {
		if(user == null) {
			throw new NullPointerException();
		}
		this.user = user;
		this.id = user.getIdLong();
	}
	
	public DiscordUser(String name, String discriminator) {
		this(getJDAUser(name, discriminator));
	}
	
	public DiscordUser(long id) {
		this.id = id;
	}
	
	@Nullable
	public User getJDAUser() {
		return user;
	}
	
	public boolean isValid() {
		return user != null;
	}
	
	public long getId() {
		return user.getIdLong();
	}
	
	@Override
	public String toCSV() {
		String output = user.getId();
		for(Player player : profiles) {
			output += "," + player.getPlayerID();
		}
		return output;
	}
	
	public static final User getJDAUser(long id) {
		return Main.discordBot.jda.getUserById(id);
	}
	
	public static final User getJDAUser(String name, String discriminator) {
		return Main.discordBot.jda.getUserByTag(name, discriminator);
	}
}
