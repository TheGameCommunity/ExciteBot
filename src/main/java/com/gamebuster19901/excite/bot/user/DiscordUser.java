package com.gamebuster19901.excite.bot.user;

import java.io.IOError;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.audit.Audit;
import com.gamebuster19901.excite.bot.audit.ban.DiscordBan;
import com.gamebuster19901.excite.bot.audit.ban.Pardon;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.DatabaseConnection;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.gamebuster19901.excite.util.TimeUtils;

import static com.gamebuster19901.excite.bot.database.Table.DISCORD_USERS;
import static com.gamebuster19901.excite.bot.database.Table.PLAYERS;

import static com.gamebuster19901.excite.Player.PLAYER_ID;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class DiscordUser {
	
	public static final String DISCORD_ID = "discordID";
	public static final String DISCORD_ID_EQUALS = DISCORD_ID + " =";
	public static final String DISCORD_NAME = "discord_name";
	public static final String DISCORD_NAME_EQUALS = DISCORD_NAME + " =";
	public static final String THRESHOLD = "threshold";
	public static final String FREQUENCY = "frequency";
	public static final String LAST_NOTIFICATION = "lastNotification";
	public static final String BELOW_THRESHOLD = "dippedBelowThreshold";
	public static final String NOTIFY_CONTINUOUSLY = "notifyContinuously";
	
	private static final Set<DesiredProfile> desiredProfiles = Collections.newSetFromMap(new ConcurrentHashMap<DesiredProfile, Boolean>());
	
	protected transient User user;
	protected transient DatabaseConnection connection;
	
	private final long discordId;
	
	public DiscordUser(ResultSet results) {
		if(user == null) {
			throw new NullPointerException();
		}
		this.discordId = user.getIdLong();
		initConnection();
	}
	
	protected DiscordUser(long userId) {
		this.discordId = userId;
		initConnection();
	}
	
	public static DiscordUser addDiscordUser(MessageContext context, long discordID, String name) throws SQLException {
		PreparedStatement ps = context.getConnection().prepareStatement("INSERT INTO ? (?, ?) VALUES (?, ?);");
		ps.setString(1, Table.DISCORD_USERS.toString());
		ps.setString(2, DISCORD_ID);
		ps.setString(3, DISCORD_NAME);
		ps.setLong(4, discordID);
		ps.setString(5, name);
		ps.execute();
		return getDiscordUser(context, discordID);
	}

	public DatabaseConnection getDatabaseConnection() {
		return connection;
	}
	
	public Connection getConnection() {
		return connection.getConnection();
	}
	
	protected void initConnection() {
		try {
			connection = new DatabaseConnection(this);
		} catch (IOException | SQLException e) {
			throw new IOError(e);
		}
	}
	
	@Nullable
	public User getJDAUser() {
		if(discordId == -1) {
			return null;
		}
		if(user == null) {
			try {
				user = Main.discordBot.jda.retrieveUserById(getId()).complete();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			if(user == null) {
				System.out.println("Could not find JDA user for " + discordId);
			}
		}
		return user;
	}
	
	public long getId() {
		return discordId;
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordBan ban(MessageContext context, Duration duration, String reason) {
		DiscordBan discordBan = new DiscordBan(context, reason, duration, this);
		discordBan = Audit.addAudit(discordBan); //future proofing, just in case we ever return a different audit in the future
		sendMessage(context, toString() + " " + reason);
		return discordBan;
	}
	
	public DiscordBan getLongestActiveBan() {
		DiscordBan longest = null;
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
		if(getLongestActiveBan() != null) {
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
	
	@SuppressWarnings("rawtypes")
	public Set<Player> getProfiles(MessageContext context) throws SQLException {
		HashSet<Player> players = new HashSet<Player>();
		ResultSet results = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, PLAYER_ID, PLAYERS, idEqualsThis());
		while(results.next()) {
			players.add(Player.getPlayerByID(context, results.getInt(1)));
		}
		return players;
	}

	public boolean isBanned() {
		return false; //TODO: Implement
	}
	
	public boolean isAdmin() {
		try {
			if(isOperator()) {
				return true;
			}
			ResultSet result = Table.selectAllFromWhere(ConsoleContext.INSTANCE, Table.ADMINS, idEqualsThis());
			return result.next();
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	public boolean isOperator() {
		try {
			ResultSet result = Table.selectAllFromWhere(ConsoleContext.INSTANCE, Table.OPERATORS, idEqualsThis());
			return result.next();
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void setAdmin(MessageContext promoter, boolean admin) {
		if(admin) {
			Table.addAdmin(promoter, this);
		}
		else {
			if(isAdmin())
			Table.removeAdmin(promoter, this);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void setOperator(MessageContext promoter, boolean operator) {
		if(operator) {
			Table.addOperator(promoter, this);
		}
		else {
			Table.removeOperator(promoter, this);
		}
	}
	
	public Instant getBanExpireTime() {
		return getLongestActiveBan().getBanExpireTime();
	}
	
	public String getBanReason() {
		return getLongestActiveBan().getDescription();
	}
	
	public int getUnpardonedBanCount() {
		return 0;
		//return preferences.getUnpardonedBanCount();
	}
	
	public int getTotalBanCount() {
		return 0;
		//return preferences.getTotalBanCount();
	}
	
	public String getDiscordName() {
		return getJDAUser().getAsTag();
	}
	
	public int getNotifyThreshold() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, THRESHOLD, DISCORD_USERS, idEqualsThis());
			if(result.next()) {
				return result.getInt(1);
			}
			else {
				throw new AssertionError("Could not find threshold for discord user " + discordId);
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public void setNotifyThreshold(int threshold) {
		if(threshold > 0 || threshold == -1) {
			try {
				Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, THRESHOLD, threshold + "", idEqualsThis());
			} catch (SQLException e) {
				throw new IOError(e);
			}
		}
		else {
			throw new IndexOutOfBoundsException(threshold + " < 1");
		}
	}
	
	public Duration getNotifyFrequency() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, FREQUENCY, DISCORD_USERS, idEqualsThis());
			if(result.next()) {
				return Duration.parse(result.getString(1));
			}
			else {
				throw new AssertionError("Could not get notification frequency for " + discordId);
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public void setNotifyFrequency(Duration frequency) {
		Duration min = Duration.ofMinutes(5);
		if(frequency.compareTo(min) > -1) {
			try {
				Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, FREQUENCY, frequency.toString(), idEqualsThis());
			} catch (SQLException e) {
				throw new IOError(e);
			}
		}
		else {
			throw new IllegalArgumentException("Frequency is less than 5 minutes!");
		}
	}
	
	public boolean isNotifyingContinuously() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, NOTIFY_CONTINUOUSLY, DISCORD_USERS, idEqualsThis());
			if(result.next()) {
				return result.getBoolean(1);
			}
			throw new AssertionError("Could not get notification frequency for " + discordId);
		} catch (SQLException e) {
			throw new IOError(e);
		} 
	}
	
	public void setNotifyContinuously(boolean continuous) {
		String value = continuous ? "b'1'" : "b'0'";
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, NOTIFY_CONTINUOUSLY, value, idEqualsThis());
			if(continuous) {
				setDippedBelowThreshold(false);
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public Instant getLastNotification() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, LAST_NOTIFICATION, DISCORD_USERS, idEqualsThis());
			if(result.next()) {
				return TimeUtils.parseInstant(result.getString(1));
			}
			throw new AssertionError("Could not get last notification of " + discordId);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public void setLastNotification() {
		setLastNotification(Instant.now());
	}
	
	public void setLastNotification(Instant instant) {
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, LAST_NOTIFICATION, instant.toString(), idEqualsThis());
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public boolean dippedBelowThreshold() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, BELOW_THRESHOLD, DISCORD_USERS, idEqualsThis());
			if(result.next()) {
				return result.getBoolean(1);
			}
			throw new AssertionError("Could not get theshold state of " + discordId);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public void setDippedBelowThreshold(boolean dippedBelow) {
		String value = dippedBelow ? "b'1'" : "b'0'";
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, BELOW_THRESHOLD, value, DISCORD_ID_EQUALS + discordId);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public String requestRegistration(Player profile) {
		DesiredProfile desiredProfile = new DesiredProfile(this, profile);
		if(desiredProfiles.contains(desiredProfile)) {
			throw new IllegalStateException(profile.toString() + " is already being registered, please wait.");
		}
		else {
			desiredProfiles.add(desiredProfile);
			return desiredProfile.getRegistrationCode();
		}
	}
	
	public boolean requestingRegistration() {
		for(DesiredProfile profile : desiredProfiles) {
			if(profile.getRequester().equals(this)) {
				return true;
			}
		}
		return false;
	}
	
	public void clearRegistration() {
		for(DesiredProfile profile : desiredProfiles) {
			if(profile.getRequester() == this) {
				desiredProfiles.remove(profile);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void sentCommand(MessageContext context) {
		
	}
	
	@SuppressWarnings("rawtypes")
	public void sentCommand(MessageContext context, int amount) {
		
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
		return Long.valueOf(discordId).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DiscordUser) {
			return ((DiscordUser) o).discordId == discordId;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.getJDAUser().getAsTag();
	}
	
	public String toDetailedString() {
		return this + " (" + discordId + ")";
	}
	
	public String getMySQLUsername() {
		return "DiscordUser#" + discordId;
	}
	
	public static void addUser(User user) {
		if(getDiscordUserIncludingUnknown(ConsoleContext.INSTANCE, user.getIdLong()) instanceof UnknownDiscordUser) {
			try {
				addDiscordUser(ConsoleContext.INSTANCE, user.getIdLong(), user.getAsTag());
			} catch (SQLException e) {
				new AssertionError("Unable to add new discord user " + user.getAsTag() + "(" + user.getIdLong() + ")", e);
			}
		}
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
	@SuppressWarnings("rawtypes")
	public static final DiscordUser getDiscordUser(MessageContext context, long id) {
		try {
			ResultSet results = Table.selectAllFromWhere(context, DISCORD_USERS, "discord_id = " + id);
			
			if(results.next()) {
				return new DiscordUser(results.getLong("discord_id"));
			}
			return null;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static final DiscordUser getDiscordUserIncludingUnknown(MessageContext context, long id) {
		DiscordUser user;
		user = getDiscordUser(context, id);
		if(user == null) {
			user = new UnknownDiscordUser(id);
		}
		return user;
	}
	
	public static final DiscordUser getDiscordUserIncludingUnknown(MessageContext context, String discriminator) {
		DiscordUser user;
		user = getDiscordUser(context, discriminator);
		if(user == null) {
			if(discriminator.contains("#")) {
				user = new UnknownDiscordUser(discriminator.substring(0, discriminator.indexOf('#')), discriminator.substring(discriminator.indexOf('#') + 1, discriminator.length()));
			}
			else {
				user = new UnknownDiscordUser(discriminator, "????");
			}
		}
		return user;
	}
	
	public static final DiscordUser getDiscordUserTreatingUnknownsAsNobody(MessageContext context, long id) {
		DiscordUser user;
		user = getDiscordUser(context, id);
		if(user == null) {
			user = Nobody.INSTANCE;
		}
		return user;
	}
	
	public static final DiscordUser getDiscordUser(MessageContext context, String discriminator) {
		try {
			ResultSet results = Table.selectAllFromWhere(context, DISCORD_USERS, DISCORD_NAME_EQUALS + discriminator);
			if(results.next()) {
				return new DiscordUser(results.getLong("discord_id"));
			}
			return null;
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static final DiscordUser[] getKnownUsers() {
		try {
			ArrayList<DiscordUser> users = new ArrayList<DiscordUser>();
			ResultSet results = Table.selectAllFrom(ConsoleContext.INSTANCE, DISCORD_USERS);
			while(results.next()) {
				users.add(new DiscordUser(results));
			}
			return users.toArray(new DiscordUser[]{});
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static final void messageAllAdmins(String message) {
		
	}
	
	public static final void messageAllOperators(String message) {
		
	}
	
	@Override
	public void finalize() {
		try {
			connection.getConnection().close();
		} catch (SQLException e) {
			ConsoleUser.getConsoleUser().sendMessage(StacktraceUtil.getStackTrace(e));
		}
	}
	
	public String idEqualsThis() {
		return DISCORD_ID_EQUALS + discordId;
	}
	
	@SuppressWarnings("rawtypes")
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
			context.sendMessage("Ban " + banId + " does not belong to " + this + ", it belongs to " + DiscordUser.getDiscordUserTreatingUnknownsAsNobody(context, discordBan.getBannedDiscordId()));
		}
		return false;
	}

	public static void attemptRegister() {
		DesiredProfile[] profiles = desiredProfiles.toArray(new DesiredProfile[] {});
		for(DesiredProfile profile : profiles) {
			if(profile.getRegistrationTimeout().isAfter(Instant.now())) {
				profile.getRequester().sendMessage("Registration for " + profile.getDesiredProfile() + " timed out");
				desiredProfiles.remove(profile);
				continue;
			}
			if(profile.isVerified()) {
				profile.register();
				desiredProfiles.remove(profile);
				continue;
			}
		}
	}
	
}
