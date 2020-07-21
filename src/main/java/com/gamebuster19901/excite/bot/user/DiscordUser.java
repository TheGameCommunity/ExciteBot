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
import com.gamebuster19901.excite.bot.audit.Audit;
import com.gamebuster19901.excite.bot.audit.ban.DiscordBan;
import com.gamebuster19901.excite.bot.audit.ban.NotDiscordBanned;
import com.gamebuster19901.excite.bot.audit.ban.Pardon;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.FileUtils;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class DiscordUser implements OutputCSV{
	
	public static final File USER_PREFS = new File("./run/userPreferences.csv");
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
				System.out.println("Found user " + user);
			}
		}
		catch(IOException e) {
			throw new IOError(e);
		}
	}
	
	private final long id;
	protected User user;
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
		if(id == -1) {
			return null;
		}
		if(user == null) {
			user = Main.discordBot.jda.retrieveUserById(id).complete();
			if(user == null) {
				System.out.println("Could not find JDA user for " + preferences.getDiscordTag());
			}
		}
		return user;
	}
	
	public long getId() {
		return id;
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordBan ban(MessageContext context, Duration duration, String reason) {
		DiscordBan discordBan = new DiscordBan(context, reason, duration, this);
		discordBan = Audit.addAudit(discordBan); //future proofing, just in case we ever return a different audit in the future
		sendMessage(context, toString() + " " + reason);
		return discordBan;
	}
	
	public DiscordBan getLongestActiveBan() {
		DiscordBan longest = NotDiscordBanned.INSTANCE;
		for(DiscordBan ban : DiscordBan.getBansOfUser(this)) {
			if(ban.isActive()) {
				if(ban.endsAfter(longest)) {
					longest = ban;
				}
			}
		}
		return longest;
	}
	
	@SuppressWarnings("rawtypes")
	public void pardon(MessageContext context, Pardon pardon) {
		DiscordBan discordBan = DiscordBan.getBanById(pardon.getBanId());
		if(pardon.getBanId() == discordBan.getAuditId()) {
			if(checkPardon(context, pardon)) {
				Audit.addAudit(new Pardon(context, discordBan));
				context.sendMessage("Pardoned " + this);
			}
		}
		else {
			context.sendMessage(this.toString() + " is not discord banned. Provide a ban ID if you wish to pardon a ban which has expired.");
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void pardon(MessageContext context) {
		DiscordBan discordBan = getLongestActiveBan();
		if(!(getLongestActiveBan() instanceof NotDiscordBanned)) {
			Pardon pardon = new Pardon(context, discordBan);
			if(checkPardon(context, pardon)) {
				discordBan.pardon(context, pardon);
			}
			else {
				throw new AssertionError("This should not be possible...");
			}
		}
		else {
			context.sendMessage(this.toString() + " is not discord banned. Provide a ban ID if you wish to pardon a ban which has expired.");
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void pardon(MessageContext context, long banId) {
		Pardon pardon = new Pardon(context, banId);
		DiscordBan discordBan = DiscordBan.getBanById(banId);
		if(checkPardon(context, pardon)) {
			discordBan.pardon(context, pardon);
		}
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
	
	public Instant getBanExpireTime() {
		return getLongestActiveBan().getBanExpireTime();
	}
	
	public String getBanReason() {
		return getLongestActiveBan().getDescription();
	}
	
	public int getUnpardonedBanCount() {
		return preferences.getUnpardonedBanCount();
	}
	
	public int getTotalBanCount() {
		return preferences.getTotalBanCount();
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
	
	public void setNotifyContinuously(boolean continuous) {
		preferences.setNotifyContinuously(continuous);
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
	
	@SuppressWarnings("rawtypes")
	public void sentCommand(MessageContext context, int amount) {
		this.preferences.sentCommand(context, amount);
	}
	
	public void sendMessage(String message) {
		if(Main.discordBot != null && !getJDAUser().equals(Main.discordBot.jda.getSelfUser())) {
			if(!getJDAUser().isBot()) {
				PrivateChannel privateChannel = getJDAUser().openPrivateChannel().complete();
				System.out.println(privateChannel.getClass().getCanonicalName());
				try {
					privateChannel.sendMessage(message).complete();
				}
				catch(ErrorResponseException e) {
					e.printStackTrace();
				}
			}
		}
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
	
	@Override
	public String toString() {
		return this.getJDAUser().getAsTag();
	}
	
	public String toDetailedString() {
		return this + " (" + id + ")";
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
		return Main.discordBot.jda.retrieveUserById(id).complete();
	}
	
	public static final User getJDAUser(String name, String discriminator) {
		if(Main.discordBot != null) {
			User user = Main.discordBot.jda.getUserByTag(name, discriminator);
			return user;
		}
		return null;
	}
	
	public static final User getJDAUser(String discriminator) {
		if(Main.discordBot != null) {
			return Main.discordBot.jda.getUserByTag(discriminator);
		}
		return null;
	}
	
	@Deprecated
	public static final DiscordUser getDiscordUser(long id) {
		DiscordUser discordUser = users.get(id);
		if(discordUser == null || discordUser instanceof UnloadedDiscordUser) {
			return null;
		}
		return discordUser;
	}
	
	public static final DiscordUser getDiscordUserIncludingUnloaded(long id) {
		return users.get(id);
	}
	
	public static final DiscordUser getDiscordUserIncludingUnknown(long id) {
		DiscordUser user;
		user = users.get(id);
		if(user == null) {
			user = new UnknownDiscordUser(id);
		}
		return user;
	}
	
	public static final DiscordUser getDiscordUserTreatingUnknownsAsNobody(long id) {
		DiscordUser user;
		user = users.get(id);
		if(user == null) {
			user = Nobody.INSTANCE;
		}
		return user;
	}
	
	public static final DiscordUser getDiscordUser(String discriminator) {
		for(Entry<Long, DiscordUser> userEntry : users.entrySet()) {
			if(userEntry.getValue().getClass() == DiscordUser.class) {
				DiscordUser discordUser = userEntry.getValue();
				if(discordUser.getJDAUser().getAsTag().trim().equalsIgnoreCase(discriminator)) {
					return discordUser;
				}
			}
		}
		return null;
	}
	
	@Deprecated
	public static final DiscordUser getDiscordUserIncludingUnloaded(String discriminator) {
		for(Entry<Long, DiscordUser> userEntry : users.entrySet()) {
			DiscordUser discordUser = userEntry.getValue();
			if(discordUser.getJDAUser().getAsTag().trim().equalsIgnoreCase(discriminator)) {
				return discordUser;
			}
		}
		return null;
	}
	
	public static final DiscordUser[] getKnownUsers() {
		return users.values().toArray(new DiscordUser[]{});
	}
	
	public static void updateWarningCooldowns() {
		for(DiscordUser user : getKnownUsers()) {
			user.preferences.updateWarningCooldown();
		}
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
		for(DiscordUser discordUser : DiscordUser.getKnownUsers()) {
			if(discordUser.getClass() == DiscordUser.class && discordUser.getJDAUser() == null) {
				UnloadedDiscordUser unloadedUser = new UnloadedDiscordUser(discordUser.id);
				unloadedUser.preferences = discordUser.preferences;
				users.put(discordUser.id, unloadedUser);
				System.out.println("Unloaded previously loaded user with id (" + discordUser.id + ")");
			}
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
				//Instant banTime;
				//Duration banDuration;
				//Instant banExpire;
				//String banReason;
				//int unpardonedBanCount;
				Instant lastNotification;
				boolean dippedBelowThreshold;
				//int totalBanCount;
				boolean notifyContinuously;
				
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
					
					//records 5 - 9 were removed
					
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
					
					//record 12 was removed
					
					if(csvRecord.size() > 13) {
						notifyContinuously = Boolean.parseBoolean(csvRecord.get(13));
					}
					else {
						notifyContinuously = false;
					}
					
					preferences.parsePreferences(discord, discordId, notifyThreshold, notifyFrequency, profiles, lastNotification, dippedBelowThreshold, notifyContinuously);
					
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
	
	private boolean checkPardon(MessageContext context, Pardon pardon) {
		long banId = pardon.getBanId();
		DiscordBan discordBan = DiscordBan.getBanById(banId);
		if(discordBan.getBannedDiscordId() == this.getId()) {
			if(!discordBan.isPardoned()) {
				 return true;
			}
			else {
				context.sendMessage(discordBan.getBannedUsername() + " has already had ban " + banId + " pardoned!");
			}
		}
		else {
			context.sendMessage("Ban " + banId + " does not belong to " + this + ", it belongs to " + DiscordUser.getDiscordUserTreatingUnknownsAsNobody(discordBan.getBannedDiscordId()));
		}
		return false;
	}
	
}
