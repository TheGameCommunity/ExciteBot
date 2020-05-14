package com.gamebuster19901.excite.bot.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.FileUtils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiscordUser implements OutputCSV{
	
	private static final File USER_PREFS = new File("./run/userPreferences.csv");
	private static final File OLD_USER_PREFS = new File("./run/userPreferences.csv.old");
	private static HashMap<Long, DiscordUser> users = new HashMap<Long, DiscordUser>();
	
	static {
		try {
			if(!USER_PREFS.exists()) {
				USER_PREFS.getParentFile().mkdirs();
				USER_PREFS.createNewFile();
			}
			else {
				if(OLD_USER_PREFS.exists()) {
					if(!FileUtils.contentEquals(USER_PREFS, OLD_USER_PREFS)) {
						throw new IOException("File content differs!");
					}
				}
			}
			for(DiscordUser user : getEncounteredUsersFromFile()) {
				addUser(user);
			}
		}
		catch(IOException e) {
			throw new IOError(e);
		}
	}
	
	private final long id;
	UserPreferences preferences;
	
	public DiscordUser(User user) {
		if(user == null) {
			throw new NullPointerException();
		}
		this.id = user.getIdLong();
		this.preferences = new UserPreferences(this);
	}
	
	protected DiscordUser(long userId) {
		this.id = userId;
		this.preferences = new UserPreferences(this);
	}
	
	@Nullable
	public User getJDAUser() {
		return Main.discordBot.jda.getUserById(id);
	}
	
	public long getId() {
		return id;
	}
	
	@SuppressWarnings("rawtypes")
	public void ban(MessageContext context, Duration duration, String reason) {
		this.preferences.ban(context, duration, reason);
	}
	
	public void pardon(int amount) {
		this.preferences.pardon(amount);
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
	
	public void setNotifyThreshold(int threshold) {
		if(threshold > 0 || threshold == -1) {
			preferences.setNotifyThreshold(threshold);
		}
		else {
			throw new IndexOutOfBoundsException(threshold + " < 1");
		}
	}
	
	public void setNotifyFrequency(Duration frequency) {
		Duration min = Duration.ofMinutes(5);
		if(frequency.compareTo(min) > -1) {
			preferences.setNotifyFrequency(frequency);
		}
		else {
			throw new IllegalArgumentException("Frequency is less than 5 minutes!");
		}
	}
	
	public String requestRegistration(Player desiredProfile) {
		return preferences.requestRegistration(desiredProfile);
	}
	
	public boolean requestingRegistration() {
		return preferences.requestingRegistration();
	}
	
	@SuppressWarnings("rawtypes")
	public void sentCommand(MessageContext context) {
		this.preferences.sentCommand(context);
	}
	
	public void sendMessage(String message) {
		getJDAUser().openPrivateChannel().complete().sendMessage(message).complete();
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
	
	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DiscordUser) {
			return ((DiscordUser) o).id == id;
		}
		return false;
	}
	
	public static void addUser(DiscordUser user) {
		for(Entry<Long, DiscordUser> userEntry : users.entrySet()) {
			DiscordUser u = userEntry.getValue();
			if(user.equals(u)) {
				if(u instanceof UnloadedDiscordUser && !(user instanceof UnloadedDiscordUser)) {
					UserPreferences preferences = u.preferences;
					user.preferences = preferences;
					users.put(u.id, user);
					System.out.println("Loaded previously unloaded user " + user.getJDAUser().getAsTag());
				}
				return;
			}
		}
		users.put(user.id, user);
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
			return Main.discordBot.jda.getUserByTag(discriminator);
		}
		return null;
	}
	
	public static final DiscordUser getDiscordUser(long id) {
		DiscordUser discordUser = users.get(id);
		if(discordUser == null || discordUser instanceof UnloadedDiscordUser) {
			return null;
		}
		return discordUser;
	}
	
	public static final DiscordUser getDiscordUser(String discriminator) {
		for(Entry<Long, DiscordUser> userEntry : users.entrySet()) {
			DiscordUser discordUser = userEntry.getValue();
			if(discordUser == null || discordUser instanceof UnloadedDiscordUser) {
				return null;
			}
			if(discordUser.getJDAUser().getAsTag().trim().equalsIgnoreCase(discriminator)) {
				return discordUser;
			}
		}
		return null;
	}
	
	public static final DiscordUser[] getKnownUsers() {
		return users.values().toArray(new DiscordUser[]{});
	}
	
	public static void updateCooldowns() {
		for(DiscordUser user : getKnownUsers()) {
			user.preferences.updateCooldowns();
		}
	}
	
	public static void updateUserList() {
		for(User user : Main.discordBot.jda.getUsers()) {
			addUser(new DiscordUser(user.getIdLong()));
		}
	}
	
	public static void updateUserPreferencesFile() {
		BufferedWriter writer = null;
		try {
			if(OLD_USER_PREFS.exists()) {
				OLD_USER_PREFS.delete();
			}
			if(!USER_PREFS.renameTo(OLD_USER_PREFS)) {
				throw new IOException();
			}
			USER_PREFS.createNewFile();
			writer = new BufferedWriter(new FileWriter(USER_PREFS));
			for(Entry<Long, DiscordUser> discordUser : users.entrySet()) {
				writer.write(discordUser.getValue().toCSV());
				writer.newLine();
			}
		}
		catch(IOException e) {
			throw new AssertionError(e);
		}
		finally {
			try {
				if(writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				throw new IOError(e);
			}
		}
	}
	
	private static final DiscordUser[] getEncounteredUsersFromFile() {
		HashSet<DiscordUser> discordUsers = new HashSet<DiscordUser>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(USER_PREFS));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			try {
				
				String discord;
				long discordId;
				int notifyThreshold;
				Duration notifyFrequency;
				Player[] profiles;
				Instant banTime;
				Duration banDuration;
				Instant banExpire;
				String banReason;
				int banCount;
				Instant lastNotification;
				boolean dippedBelowThreshold;
				
				for(CSVRecord csvRecord : csvParser) {
					DiscordUser discordUser;
					UserPreferences preferences = new UserPreferences();

					discord = csvRecord.get(0);
					discordId = Long.parseLong(csvRecord.get(1).replaceFirst("'", ""));
					notifyThreshold = Integer.parseInt(csvRecord.get(2));
					notifyFrequency = Duration.parse(csvRecord.get(3));
					String[] players = csvRecord.get(4).replaceAll("\"", "").replaceFirst("'", "").split(",");
					int[] playerIDs = new int[players.length];
					for(int i = 0; i < players.length; i++) {
						if(!players[i].isEmpty()) {
							playerIDs[i] = Integer.parseInt(players[i]);
						}
					}
					profiles = Player.getPlayersFromIds(playerIDs);
					banTime = Instant.parse(csvRecord.get(5));
					banDuration = Duration.parse(csvRecord.get(6));
					banExpire = Instant.parse(csvRecord.get(7));
					banReason = csvRecord.get(8);
					banCount = Integer.parseInt(csvRecord.get(9));
					
					if(csvRecord.size() > 10) { //legacy data may not have this record
						lastNotification = Instant.parse(csvRecord.get(10));
					}
					else {
						lastNotification = Instant.MIN;
					}
					
					if(csvRecord.size() > 11) { //legacy data may not have this record
						dippedBelowThreshold = Boolean.parseBoolean(csvRecord.get(11));
					}
					else {
						dippedBelowThreshold = false;
					}
					
					preferences.parsePreferences(discord, discordId, notifyThreshold, notifyFrequency, profiles, banTime, banDuration, banExpire, banReason, banCount, lastNotification, dippedBelowThreshold);
					
					User jdaUser = getJDAUser(discordId);
					if(jdaUser != null) {
						discordUser = new DiscordUser(jdaUser);
					}
					else {
						System.out.println("Could not find JDA user for " + discord + "(" + discordId + ")");
						discordUser = new UnloadedDiscordUser(discordId);
					}
					discordUser.preferences = preferences;
					
					discordUsers.add(discordUser);
				}
			}
			finally {
				if(reader != null) {
					reader.close();
				}
				if(csvParser != null) {
					csvParser.close();
				}
			}
		}
		catch(IOException e) {
			throw new AssertionError(e);
		}
		return discordUsers.toArray(new DiscordUser[]{});
	}
}
