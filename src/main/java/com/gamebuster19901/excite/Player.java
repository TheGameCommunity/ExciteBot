package com.gamebuster19901.excite;

import java.io.File;
import java.io.IOError;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.logging.Logger;

import com.gamebuster19901.excite.bot.server.emote.Emote;
import com.gamebuster19901.excite.bot.audit.DiscoveryAudit;
import com.gamebuster19901.excite.bot.audit.NameChangeAudit;
import com.gamebuster19901.excite.bot.audit.ban.Ban;
import com.gamebuster19901.excite.bot.audit.ban.Banee;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Function;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Result;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.Nobody;
import com.gamebuster19901.excite.bot.user.UnknownUser;
import com.gamebuster19901.excite.util.Owned;
import com.gamebuster19901.excite.util.TimeUtils;

import static com.gamebuster19901.excite.bot.database.Table.PLAYERS;
import static com.gamebuster19901.excite.bot.database.Comparator.*;
import static com.gamebuster19901.excite.bot.database.Column.*;

import net.dv8tion.jda.api.entities.User;

public class Player implements Banee<Player>, Owned<User, Player> {
	private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
	
	public static final String validFCChars = "1234567890";
	
	protected static final String LEGACY = new String("legacy");
	protected static final String VERIFIED = new String("verified");
	protected static final String BANNED = new String("banned");
	protected static final String ONLINE = new String("online");
	protected static final String ONLINE_PRIVATE = new String("private_room");
	protected static final String OFFLINE = new String("offline");
	protected static final String HOSTING = new String("hosting");
	protected static final String HOSTING_PRIVATE = new String("hosting_private_room");
	protected static final String SEARCHING = new String("searching");
	protected static final String SPECTATING = new String("spectating");
	protected static final String FRIENDS_LIST = new String("friend_list");
	protected static final String BOT = new String(Character.toChars(0x1F916));
	protected static final String BOT_ADMIN = new String("bot_admin");
	protected static final String BOT_OPERATOR = new String("bot_operator");
	
	protected static final File KNOWN_PLAYERS = new File("./run/encounteredPlayers.csv");
	protected static final File OLD_KNOWN_PLAYERS = new File("./run/encounteredPlayers.csv.old");
	
	private final int playerID;
	
	private transient int host;
	private transient int connectionStatus = -1;
	
	private Player(Result results) throws SQLException {
		this(results.getInt(PLAYER_ID));
	}

	protected Player(int playerID) {
		this.playerID = playerID;
		Player onlinePlayer = Wiimmfi.getOnlinePlayerByID(playerID);
		if(onlinePlayer != null) {
			this.host = onlinePlayer.host;
			this.connectionStatus = onlinePlayer.connectionStatus;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Player addPlayer(CommandContext<?> context, boolean automatic, int playerID, String friendCode, String name) throws SQLException {
		
		if(context.getEvent(Player.class) instanceof UnknownPlayer) {
			UnknownPlayer player = context.getEvent(UnknownPlayer.class);
			player.name = name;
			player.friendCode = friendCode;
		}
		
		PreparedStatement ps = Insertion.insertInto(PLAYERS)
		.setColumns(PLAYER_ID, FRIEND_CODE, PLAYER_NAME)
		.to(playerID, friendCode, name)
		.prepare(ConsoleContext.INSTANCE);

		ps.execute();

		Player ret = getPlayerByID(context, playerID);
		DiscoveryAudit.addProfileDiscovery(context, automatic, ret);
		
		return ret;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String toString() {
		String name = getName();
		long discordID = getDiscord();
		
		String prefix = calculatePrefix();
		String suffix = "";

		if(isBot()) {
			suffix += BOT;
		}
		if(discordID != 0) {
			CommandContext context = new CommandContext(DiscordUser.getUser(discordID));
			if(context.isOperator()) {
				suffix = suffix + Emote.getEmoji(BOT_OPERATOR);
			}
			else if(context.isAdmin()) {
				suffix = suffix + Emote.getEmoji(BOT_ADMIN);
			}
		}
		if(isLegacy()) {
			suffix += Emote.getEmoji(LEGACY);
		}
		if(isVerified()) {
			suffix += Emote.getEmoji(VERIFIED);
			if(this.isBanned()) {
				if(!isOnline()) {
					suffix += Emote.getEmoji(BANNED);
				}
			}
			return String.format(prefix + " " + name +  " - Discord❲" + getPrettyDiscord() + "❳" + suffix);
		}
		else if(this.isBanned()) {
			if(!isOnline()) {
				suffix += Emote.getEmoji(BANNED);
			}
		}
		if(!suffix.isEmpty()) {
			suffix = suffix + " ";
		}
		return String.format(prefix + " " + name + " " + suffix);
	}
	
	public String toEmbedstring() {
		String name = getName();
		
		String prefix = calculatePrefix();
		String suffix = "";

		if(isBot()) {
			suffix += BOT;
		}
		if(isLegacy()) {
			suffix += Emote.getEmoji(LEGACY);
		}
		if(this.isBanned()) {
			if(!isOnline()) {
				suffix += Emote.getEmoji(BANNED);
			}
		}
		if(!suffix.isEmpty()) {
			suffix = suffix + " ";
		}
		return String.format(prefix + " " + name + "(" + getID() + ") " + suffix);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String toFullString() {
		long discordID = getDiscord();
		String name = getName();
		String friendCode = getFriendCode();
		
		String prefix = calculatePrefix();
		String suffix = "";

		if(isBot()) {
			suffix += BOT;
		}
		if(discordID != 0) {
			CommandContext context = new CommandContext(DiscordUser.getUser(discordID));
			if(context.isOperator()) {
				suffix = suffix + Emote.getEmoji(BOT_OPERATOR);
			}
			else if(context.isAdmin()) {
				suffix = suffix + Emote.getEmoji(BOT_ADMIN);
			}
		}
		if(isLegacy()) {
			suffix += Emote.getEmoji(LEGACY);
		}
		if(isVerified()) {
			suffix += Emote.getEmoji(VERIFIED);
			if(this.isBanned()) {
				if(!isOnline()) {
					suffix += Emote.getEmoji(BANNED);
				}
			}
			return String.format(prefix + " " + name +  " - FC❲" + friendCode +  "❳ - PID❲"  + playerID + "❳ - Discord❲" + getPrettyDiscord() + "❳" + suffix);
		}
		else if(this.isBanned()) {
			if(!isOnline()) {
				suffix += Emote.getEmoji(BANNED);
			}
		}
		if(!suffix.isEmpty()) {
			suffix = suffix + " ";
		}
		return String.format(prefix + " " + name + " - FC❲" + friendCode +  "❳ - PID❲"  + playerID + "❳" + suffix);
	}
	
	private String calculatePrefix() {
		String prefix = "";
		if(isOnline()) {
			if(isBanned()) {
				prefix = prefix + Emote.getEmoji(BANNED);
			}
			if(isGlobal()) {
				if(isSearching()) {
					prefix = prefix + Emote.getEmoji(SEARCHING);
				}
				else {
					if(isHosting()) {
						prefix = prefix + Emote.getEmoji(HOSTING);
					}
					else if (!isBanned()){
						prefix = prefix + Emote.getEmoji(ONLINE);
					}
				}
			}
			else if (isPrivate()){
				if(isFriendsList()) {
					prefix = prefix + Emote.getEmoji(FRIENDS_LIST);
				}
				if(isHosting()) {
					prefix = prefix + Emote.getEmoji(HOSTING_PRIVATE);
				}
				else{
					prefix = prefix + Emote.getEmoji(ONLINE_PRIVATE);
				}
			}
			else {
				prefix = prefix + "?";
			}
		}
		else {
			prefix = prefix + Emote.getEmoji(OFFLINE);
		}
		return prefix;
	}
	
	@Override
	public String getName() {
		try {
			if(isRedacted()) {
				return "REDACTED_NAME";
			}
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, PLAYER_NAME, PLAYERS, new Comparison(PLAYER_ID, EQUALS, getID()));
			if(result.next()) {
				return result.getString(PLAYER_NAME);
			}
			else {
				throw new AssertionError("Could not find name of player with PID " + playerID);
			}
		}
		catch(SQLException | AssertionError e) {
			throw new IOError(e);
		}
	}
	
	public void setName(String name) throws SQLException {
		String oldName = getName();
		if(oldName != null && !oldName.equals(name)) {
			NameChangeAudit.addNameChange(new CommandContext(this), this, name);
			Table.updateWhere(ConsoleContext.INSTANCE, PLAYERS, PLAYER_NAME, name, new Comparison(PLAYER_ID, EQUALS, getID()));
		}
	}
	
	public void setOnlineStatus(int status) {
		this.connectionStatus = status;
	}
	
	public String getFriendCode() {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, FRIEND_CODE, PLAYERS, new Comparison(PLAYER_ID, EQUALS, getID()));
			if(result.next()) {
				return result.getString(FRIEND_CODE);
			}
			else {
				throw new AssertionError("Could not find friend code of player with PID " + playerID);
			}
		}
		catch(SQLException | AssertionError e) {
			throw new IOError(e);
		}
	}
	
	@Override
	public long getID() {
		return playerID;
	}
	
	public boolean isLegacy() {
		return playerID < 600000000;
	}
	
	public boolean isVerified() {
		return getDiscord() != 0;
	}
	
	@SuppressWarnings("deprecation")
	public boolean isBot() {
		return DiscordUser.getUser(getDiscord()).isBot();
	}
	
	public boolean isRedacted() {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, REDACTED, PLAYERS, new Comparison(PLAYER_ID, EQUALS, getID()));
			if(result.next()) {
				return result.getBoolean(REDACTED);
			}
			throw new AssertionError("Could not find player with pid " + playerID);
		}
		catch (SQLException e){
			throw new IOError(e);
		}
	}

	public boolean isBanned() {
		for(Ban ban : Ban.getBansOf(ConsoleContext.INSTANCE, this)) {
			if(ban.isActive()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isOnline() {
		return connectionStatus > -1;
	}
	
	public boolean isHosting() {
		return host == 2;
	}
	
	public boolean isGlobal() {
		return !isPrivate();
	}
	
	public boolean isPrivate() {
		return (host == 2 && connectionStatus == 6);
	}
	
	public boolean isSearching() {
		return connectionStatus == 3 && !isHosting() ;
	}
	
	public boolean isFriendsList() {
		return connectionStatus == 1 && host == 0;
	}
	
	public void setHost(int host) {
		this.host = host;
	}
	
	public void updateSecondsPlayed() {
		try {
			if(!isOnline() || !Wiimmfi.getOnlinePlayers().contains(this)) {
				throw new IllegalStateException();
			}
			Table.updateWhere(ConsoleContext.INSTANCE, PLAYERS, SECONDS_PLAYED, Duration.between(getLastOnline(), Instant.now()).plus(getOnlineDuration()), new Comparison(PLAYER_ID, EQUALS, this.getID()));
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public Duration getOnlineDuration() {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, SECONDS_PLAYED, PLAYERS, new Comparison(PLAYER_ID, EQUALS, this.getID()));
			if(result.next()) {
				return Duration.parse(result.getString(SECONDS_PLAYED));
			}
			else {
				throw new IllegalStateException("No result for player " + this.getIdentifierName());
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public Instant getLastOnline() {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, LAST_ONLINE, PLAYERS, new Comparison(PLAYER_ID, EQUALS, this.getID()));
			if(result.next()) {
				return Instant.parse(result.getString(LAST_ONLINE));
			}
			else {
				throw new IllegalStateException("No result for player " + this.getIdentifierName());
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public void updateLastOnline() {
		setLastOnline(Instant.now());
	}
	
	public void setLastOnline(Instant instant) {
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, PLAYERS, LAST_ONLINE, instant, new Comparison(PLAYER_ID, EQUALS, this.getID()));
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public Instant getFirstSeen() {
		try {
			DiscoveryAudit discoveryAudit = DiscoveryAudit.getProfileDiscoveryByDiscoveredID(ConsoleContext.INSTANCE, playerID);
			return discoveryAudit.getDateIssued();
		}
		catch(IndexOutOfBoundsException e) {
			return TimeUtils.PLAYER_EPOCH; //profile may have been created before this was tracked
		}
	}
	
	@Override
	public User getOwner() {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, DISCORD_ID, PLAYERS, new Comparison(PLAYER_ID, EQUALS, getID()));
			if(result.next()) {
				return DiscordUser.getUser(getDiscord());
			}
			return Nobody.INSTANCE;
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public long getDiscord() {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, DISCORD_ID, PLAYERS, new Comparison(PLAYER_ID, EQUALS, getID()));
			if(result.next()) {
				long ret = result.getLong(DISCORD_ID);
				if(ret != 0) {
					return ret;
				}
				return 0;
			}
			throw new AssertionError("Could not find player with pid " + playerID);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public String getPrettyDiscord() {
		return DiscordUser.getUser(getDiscord()).toString();
	}
	
	public void setDiscord(long discordID) {
		try {
			if(getDiscord() != discordID) {
				Table.updateWhere(ConsoleContext.INSTANCE, PLAYERS, DISCORD_ID, discordID, new Comparison(PLAYER_ID, EQUALS, getID()));
			}
		}
		catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Ban ban(CommandContext context, Duration duration, String reason) {
		Ban ban = Ban.addBan(context, this, reason, duration);
		User discord = DiscordUser.getUser(getDiscord());
		if(!(discord instanceof UnknownUser)) {
			DiscordUser.sendMessage(discord, toString() + " " + reason);
		}
		return ban;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Player) {
			return ((Player) o).getID() == getID();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) getID();
	}
	

	@Override
	public Player asObj() {
		return this;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isPlayerKnown(CommandContext context, int pid) {
		return Table.existsWhere(context, PLAYERS, new Comparison(PLAYER_ID, EQUALS, pid));
	}
	
	@SuppressWarnings("rawtypes")
	public static Player getPlayerByID(CommandContext context, int pid) {
		try {
			Result rs = Table.selectAllFromWhere(context, PLAYERS, new Comparison(PLAYER_ID, EQUALS, pid));
			if(rs.next()) {
				return new Player(rs);
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
		return new UnknownPlayer(pid);
	}
	
	@Deprecated
	@SuppressWarnings("rawtypes")
	public static Player[] getPlayersByName(CommandContext context, String name) {
		HashSet<Player> players = new HashSet<Player>();
		try {
			Result rs = Table.selectAllFromWhere(context, PLAYERS, new Comparison(PLAYER_NAME, EQUALS, name));
			while(rs.next()) {
				players.add(new Player(rs));
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
		return players.toArray(new Player[]{});
	}
	
	public static Player[] searchPlayers(CommandContext context, String query) {
		HashSet<Player> players = new HashSet<Player>();
		try {
			Result rs = Table.selectAllFromWhere(context, PLAYERS, new Comparison(Function.Lower.of(PLAYER_NAME), LIKE, query + "%"));
			while(rs.next()) {
				players.add(new Player(rs));
			}
		} catch(SQLException e) {
			throw new IOError(e);
		}
		return players.toArray(new Player[] {});
	}
	
	public static Player[] getPlayersByAnyIdentifier(CommandContext context, String identifier) {
		HashSet<Player> players = new HashSet<Player>();
		try {
			if(identifier.endsWith(")") && identifier.indexOf('(') != -1) {
				identifier = identifier.substring(identifier.indexOf('(') + 1, identifier.length() - 1);
			}
			Result rs = Table.selectAllFromWhere(context, PLAYERS, 
				new Comparison(PLAYER_NAME, EQUALS, identifier)
				.or(
				new Comparison(PLAYER_ID, EQUALS, identifier))
				.or(
				new Comparison(FRIEND_CODE, EQUALS, identifier)		
				));
			while(rs.next()) {
				players.add(new Player(rs));
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
		return players.toArray(new Player[]{});
	}
	
	public static Player[] getUnclaimedPlayersByAnyIdentifier(CommandContext context, String identifier) {
		HashSet<Player> players = new HashSet<Player>();
		try {
			Result rs = Table.selectAllFromWhere(context, PLAYERS, 
				new Comparison(PLAYER_NAME, EQUALS, identifier)
				.openBeginning().or(
				new Comparison(PLAYER_ID, EQUALS, identifier))
				.or(
				new Comparison(FRIEND_CODE, EQUALS, identifier))
				.close().and(
				new Comparison(DISCORD_ID, IS_NULL))
				);
			while(rs.next()) {
				players.add(new Player(rs));
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
		return players.toArray(new Player[]{});
	}
	
	@SuppressWarnings("rawtypes")
	public static Player[] getEncounteredPlayers(CommandContext context) {
		HashSet<Player> players = new HashSet<Player>();
		try {
			Result rs = Table.selectAllFrom(context, PLAYERS);
			while(rs.next()) {
				players.add(new Player(rs));
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
		return players.toArray(new Player[]{});
	}
	
	public static boolean isValidFriendCode(String fc) {
		if(fc.length() == 14) {
			for(int i = 0; i < 14; i++) {
				if(i == 4 || i == 9) {
					if(fc.charAt(i) != '-') {
						return false;
					}
				}
				else {
					if(validFCChars.indexOf(fc.charAt(i)) == -1) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
}