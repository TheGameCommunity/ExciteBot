package com.gamebuster19901.excite.bot.user;

import java.io.IOError;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.audit.RankChangeAudit;
import com.gamebuster19901.excite.bot.audit.ban.Ban;
import com.gamebuster19901.excite.bot.audit.ban.Banee;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Result;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.transaction.CurrencyType;
import com.gamebuster19901.excite.util.TimeUtils;

import static com.gamebuster19901.excite.bot.database.Comparator.*;
import static com.gamebuster19901.excite.bot.database.Table.DISCORD_USERS;
import static com.gamebuster19901.excite.bot.database.Table.PLAYERS;
import static com.gamebuster19901.excite.bot.database.Table.WIIS;

import static com.gamebuster19901.excite.bot.database.Column.*;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class DiscordUser{
	
	private static final Set<DesiredProfile> desiredProfiles = Collections.newSetFromMap(new ConcurrentHashMap<DesiredProfile, Boolean>());
	
	public static void addUser(User user) {
		if(!isKnown(user)) {
			try {
				addDiscordUser(ConsoleContext.INSTANCE, user.getIdLong(), user.getAsTag());
			} catch (SQLException e) {
				throw new AssertionError("Unable to add new discord user " + user.getAsTag() + "(" + user.getIdLong() + ")", e);
			}
		}
	}
	
	public static void addUser(Member member) {
		addUser(member.getUser());
	}
	
	@SuppressWarnings("rawtypes")
	private static User addDiscordUser(CommandContext context, long discordID, String name) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = context.getConnection().prepareStatement("INSERT INTO " + DISCORD_USERS + " (" + DISCORD_ID + ", "+ DISCORD_NAME + ", " + LAST_NOTIFICATION + ") VALUES (?, ?, ?);");
			
			Table.insertValue(ps, 1, discordID);
			Table.insertValue(ps, 2, name);
			Table.insertValue(ps, 3, TimeUtils.PLAYER_EPOCH);
			
			ps.execute();
		}
		catch(Exception e) {
			sendMessage(ConsoleUser.getConsoleUser(), ps.toString());
			throw new IOError(e);
		}
		return getUser(discordID);
	}
	
	public static Member getMember(User user, Guild server) {
		return server.getMemberById(user.getIdLong());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Ban ban(CommandContext context, User user, Duration duration, String reason) {
		Ban discordBan = Ban.addBan(context, Banee.of(user), reason, duration);
		context.sendMessage(user.getAsMention() + " has been banned for " + TimeUtils.readableDuration(duration) + " with the reason: \n\n\"" + reason + "\"");
		new CommandContext(user).sendMessage(user.getAsMention() + ", " + reason);
		return discordBan;
	}
	
	@SuppressWarnings("rawtypes")
	public static Set<Player> getProfiles(CommandContext context, User user) {
		try {
			HashSet<Player> players = new HashSet<Player>();
			Result results = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, PLAYER_ID, PLAYERS, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			while(results.next()) {
				players.add(Player.getPlayerByID(context, results.getInt(PLAYER_ID)));
			}
			return players;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}

	public static boolean isBanned(User user) {
		for(Ban ban : Ban.getBansOf(ConsoleContext.INSTANCE, Banee.of(user))) {
			if(ban.isActive()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isAdmin(User user) {
		try {
			if(isOperator(user)) {
				return true;
			}
			Result result = Table.selectAllFromWhere(ConsoleContext.INSTANCE, Table.ADMINS, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			return result.next();
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static boolean isOperator(User user) {
		try {
			Result result = Table.selectAllFromWhere(ConsoleContext.INSTANCE, Table.OPERATORS, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			return result.next();
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static boolean isKnown(long discordID) {
		return Table.existsWhere(ConsoleContext.INSTANCE, Table.DISCORD_USERS, new Comparison(DISCORD_ID, EQUALS, discordID));
	}
	
	public static boolean isKnown(User user) {
		return isKnown(user.getIdLong());
	}
	
	public static boolean isKnown(Member member) {
		return isKnown(member.getIdLong());
	}
	
	@SuppressWarnings("rawtypes")
	public static void setAdmin(CommandContext promoter, User user, boolean admin) {
		if(admin) {
			Table.addAdmin(promoter, user);
		}
		else {
			Table.removeAdmin(promoter, user);
		}
		if(admin == false && isOperator(user)) {
			setOperator(promoter, user, false);
		}
		RankChangeAudit.addRankChange(promoter, user, "admin", admin);
	}
	
	@SuppressWarnings("rawtypes")
	public static void setOperator(CommandContext promoter, User user, boolean operator) {
		if(operator) {
			if(!isAdmin(user)) {
				setAdmin(promoter, user, operator);
			}
			Table.addOperator(promoter, user);
		}
		else {
			Table.removeOperator(promoter, user);
		}
		RankChangeAudit.addRankChange(promoter, user, "operator", operator);
	}
	
	public static int getNotifyThreshold(User user) {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, THRESHOLD, DISCORD_USERS, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			if(result.next()) {
				return result.getInt(THRESHOLD);
			}
			else {
				throw new AssertionError("Could not find threshold for discord user " + user.getAsMention());
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static void setNotifyThreshold(User user, int threshold) {
		if(threshold > 0 || threshold == -1) {
			try {
				Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, THRESHOLD, threshold, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			} catch (SQLException e) {
				throw new IOError(e);
			}
		}
		else {
			throw new IndexOutOfBoundsException(threshold + " < 1");
		}
	}
	
	public static Duration getNotifyFrequency(User user) {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, FREQUENCY, DISCORD_USERS, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			if(result.next()) {
				return Duration.parse(result.getString(FREQUENCY));
			}
			else {
				throw new AssertionError("Could not get notification frequency for " + user.getAsMention());
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static void setNotifyFrequency(User user, Duration frequency) {
		Duration min = Duration.ofMinutes(5);
		if(frequency.compareTo(min) > -1) {
			try {
				Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, FREQUENCY, frequency, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			} catch (SQLException e) {
				throw new IOError(e);
			}
		}
		else {
			throw new IllegalArgumentException("Frequency is less than 5 minutes!");
		}
	}
	
	public static boolean isNotifyingContinuously(User user) {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, NOTIFY_CONTINUOUSLY, DISCORD_USERS, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			if(result.next()) {
				return result.getBoolean(NOTIFY_CONTINUOUSLY);
			}
			throw new AssertionError("Could not get notification frequency for " + user.getIdLong());
		} catch (SQLException e) {
			throw new IOError(e);
		} 
	}
	
	public static void setNotifyContinuously(User user, boolean continuous) {
		int value = continuous ? 1 : 0;
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, NOTIFY_CONTINUOUSLY, value, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			if(continuous) {
				setDippedBelowThreshold(user, false);
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static Instant getLastNotification(User user) {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, LAST_NOTIFICATION, DISCORD_USERS, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			if(result.next()) {
				return TimeUtils.parseInstant(result.getString(LAST_NOTIFICATION));
			}
			throw new AssertionError("Could not get last notification of " + user.getAsMention());
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static void setLastNotification(User user) {
		setLastNotification(user, Instant.now());
	}
	
	public static void setLastNotification(User user, Instant instant) {
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, LAST_NOTIFICATION, instant.toString(), new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static boolean dippedBelowThreshold(User user) {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, BELOW_THRESHOLD, DISCORD_USERS, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			if(result.next()) {
				return result.getBoolean(BELOW_THRESHOLD);
			}
			throw new AssertionError("Could not get theshold state of " + user.getAsMention());
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static void setDippedBelowThreshold(User user, boolean dippedBelow) {
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, BELOW_THRESHOLD, dippedBelow, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@Nullable
	public static Wii[] getRegisteredWiis(User user) {
		try {
			HashSet<Wii> wiis = new HashSet<Wii>();
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, WII_ID, WIIS, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			while(result.next()) {
				wiis.add(Wii.getWii(result.getString(WII_ID)));
			}
			return (Wii[]) wiis.toArray(new Wii[] {});
		} catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static long getBalance(User user, CurrencyType currency) {
		return -999;
	}
	
	public static boolean sendDetailedPM(User user) {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, DETAILED_PM, DISCORD_USERS, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
			if(result.next()) {
				return result.getBoolean(DETAILED_PM);
			}
			throw new AssertionError("Could not get desired PM output of " + user.getAsMention());
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static void setSendDetailedPM(User user, boolean sendFullPM) {
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_USERS, DETAILED_PM, sendFullPM, new Comparison(DISCORD_ID, EQUALS, user.getIdLong()));
		} catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static String requestRegistration(User user, Player profile) {
		DesiredProfile desiredProfile = new DesiredProfile(user, profile);
		if(desiredProfiles.contains(desiredProfile)) {
			throw new IllegalStateException(profile.toString() + " is already being registered, please wait.");
		}
		else {
			desiredProfiles.add(desiredProfile);
			return desiredProfile.getRegistrationCode();
		}
	}
	
	public static boolean requestingRegistration(User user) {
		for(DesiredProfile profile : desiredProfiles) {
			if(profile.getRequester().equals(user)) {
				return true;
			}
		}
		return false;
	}
	
	public static void clearRegistration(User user) {
		for(DesiredProfile profile : desiredProfiles) {
			if(profile.getRequester().equals(user)) {
				desiredProfiles.remove(profile);
			}
		}
	}
	
	public static Message sendMessage(User user, MessageEmbed message) {
		if(canReceiveMessages(user)) {
			PrivateChannel privateChannel = user.openPrivateChannel().complete();
			try {
				return privateChannel.sendMessageEmbeds(message).complete();
			}
			catch(ErrorResponseException e) {
				System.out.println(user.getAsMention());
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void sendMessage(User user, String message) {
		if(canReceiveMessages(user)) {
			PrivateChannel privateChannel = user.openPrivateChannel().complete();
			try {
				privateChannel.sendMessage(message).complete();
			}
			catch(ErrorResponseException e) {
				System.out.println(user.getAsMention());
				e.printStackTrace();
			}
		}
	}

	
	public static String toDetailedString(User user) {
		return user.getName() + user.getDiscriminator() + " (" + user.getIdLong() + ")";
	}
	
	public static final User getUser(long id) {
		return Main.discordBot.jda.retrieveUserById(id).complete();
	}
	
	public static final User getUser(String name, String discriminator) {
		if(Main.discordBot != null) {
			User user = Main.discordBot.jda.getUserByTag(name, discriminator);
			return user;
		}
		return null;
	}
	
	public static final User getUser(String discriminator) {
		if(Main.discordBot != null) {
			return Main.discordBot.jda.getUserByTag(discriminator);
		}
		return null;
	}
	
	public static final User[] getUsers(String name) {
		if(Main.discordBot != null) {
			return Main.discordBot.jda.getUsersByName(name, true).toArray(new User[]{});
		}
		return null;
	}
	
	public static final User getUser(Result result) {
		return getUser(result.getLong(DISCORD_ID));
	}
	
	public static final Member getMember(String name, long id, Guild server) {
		return server.getMemberById(id);
	}
	
	public static final Member getMember(String name, String discriminator, Guild server) {
		return server.getMemberByTag(name, discriminator);
	}
	
	public static final Member getMember(String discriminator, Guild server) {
		return server.getMemberByTag(discriminator);
	}
	
	public static final void messageAllAdmins(String message) {
		HashSet<User> admins = new HashSet<User>();
		admins.addAll(Arrays.asList(getAllAdmins()));
		admins.addAll(Arrays.asList(getAllOperators()));
		for(User admin : admins) {
			sendMessage(admin, message);
		}
	}
	
	public static final void messageAllOperators(String message) {
		for(User operator : getAllOperators()) {
			sendMessage(operator, message);
		}
	}
	
	public static User[] getAllAdmins() {
		try {
			PreparedStatement st = ConsoleContext.INSTANCE.getConnection().prepareStatement("SELECT * FROM discord_users INNER JOIN admins ON(discord_users.discordID = admins.discordID);");
			Result results = st.query();
			int columns = results.getColumnCount();
			User[] operators = new User[columns];
			for(int i = 0; i < columns; i++) {
				results.next();
				operators[i] = Main.discordBot.jda.retrieveUserById(results.getLong(DISCORD_ID)).complete();
			}
			return operators;
		} catch (SQLException e) {
			throw new AssertionError(e);
		}
	}
	
	public static User[] getAllOperators() {
		try {
			PreparedStatement st = ConsoleContext.INSTANCE.getConnection().prepareStatement("SELECT * FROM discord_users INNER JOIN operators ON(discord_users.discordID = operators.discordID);");
			Result results = st.query();
			int columns = results.getColumnCount();
			User[] operators = new User[columns];
			for(int i = 0; i < columns; i++) {
				results.next();
				operators[i] = Main.discordBot.jda.retrieveUserById(results.getLong(DISCORD_ID)).complete();
			}
			return operators;
		} catch (SQLException e) {
			throw new AssertionError(e);
		}
	}
	
	public static void notifyDiscordUsers() throws SQLException {
		int count = Wiimmfi.getAcknowledgedPlayerCount();
		Result result = Table.selectAllFrom(ConsoleContext.INSTANCE, DISCORD_USERS);
		while(result.next()) {
			int threshold = result.getInt(THRESHOLD);
			if(count < threshold) {
				setDippedBelowThreshold(getUser(result), true);
				continue;
			}
			else {
				if(Instant.parse(result.getString(LAST_NOTIFICATION)).plus(Duration.parse(result.getString(FREQUENCY))).isBefore(Instant.now())) {
					if(result.getBoolean(BELOW_THRESHOLD)) {
						User user = getUser(result);
						if(!result.getBoolean(NOTIFY_CONTINUOUSLY)) {
							setDippedBelowThreshold(user, false);
						}
						if(threshold != -1) {
							for(Player player : Wiimmfi.getOnlinePlayers()) {
								if(player.getDiscord() == result.getLong(DISCORD_ID)) {
									return;
								}
							}
							sendMessage(user, "Players Online" + Wiimmfi.getOnlinePlayerList(sendDetailedPM(user)));
							setLastNotification(user);
						}
					}
				}
			}
		}
	}

	public static void attemptRegister() {
		DesiredProfile[] profiles = desiredProfiles.toArray(new DesiredProfile[] {});
		for(DesiredProfile profile : profiles) {
			if(profile.getRegistrationTimeout().isBefore(Instant.now())) {
				sendMessage(profile.getRequester(), "Registration for " + profile.getDesiredProfile().toFullString() + " timed out");
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
	
	public static boolean isFullDiscordTag(String userString) {
		return userString.length() > 4 && userString.charAt(userString.length() - 5) == '#';
	}
	
	public static String getDiscriminator(String fullDiscordTag) {
		System.out.println(fullDiscordTag.substring(fullDiscordTag.lastIndexOf('#') + 1));
		return fullDiscordTag.substring(fullDiscordTag.lastIndexOf('#') + 1);
	}
	
	public static boolean canReceiveMessages(User user) {
		return !user.isBot() && !user.isSystem();
	}
}
