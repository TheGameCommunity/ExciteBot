package com.gamebuster19901.excite;

import java.io.File;
import java.io.IOError;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashSet;
import java.util.logging.Logger;

import com.gamebuster19901.excite.bot.server.emote.Emote;
import com.gamebuster19901.excite.bot.audit.DiscoveryAudit;
import com.gamebuster19901.excite.bot.audit.NameChangeAudit;
import com.gamebuster19901.excite.bot.audit.ban.Ban;
import com.gamebuster19901.excite.bot.audit.ban.Banee;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.database.sql.ResultSet;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;

import static com.gamebuster19901.excite.bot.database.Table.PLAYERS;
import static com.gamebuster19901.excite.bot.database.Comparator.*;
import static com.gamebuster19901.excite.bot.database.Column.*;

import net.dv8tion.jda.api.entities.User;

public class Player implements Banee {
	private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
	
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
	private transient String onlineStatus = "";
	private transient int connectionStatus;
	
	private Player(ResultSet results) throws SQLException {
		this(results.getInt(PLAYER_ID));
	}

	protected Player(int playerID) {
		this.playerID = playerID;
		Player onlinePlayer = Wiimmfi.getOnlinePlayerByID(playerID);
		if(onlinePlayer != null) {
			this.host = onlinePlayer.host;
			this.onlineStatus = onlinePlayer.onlineStatus;
			this.connectionStatus = onlinePlayer.connectionStatus;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Player addPlayer(MessageContext context, int playerID, String friendCode, String name) throws SQLException {
		PreparedStatement ps = Insertion.insertInto(PLAYERS)
		.setColumns(PLAYER_ID, FRIEND_CODE, PLAYER_NAME)
		.to(playerID, friendCode, name)
		.prepare(ConsoleContext.INSTANCE);

		ps.execute();

		Player ret = getPlayerByID(context, playerID);
		DiscoveryAudit.addProfileDiscovery(new MessageContext(ret));
		
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
		if(discordID != -1) {
			MessageContext context = new MessageContext(DiscordUser.getDiscordUserIncludingUnknown(ConsoleContext.INSTANCE, discordID));
			if(context.isOperator()) {
				suffix = suffix + Emote.getEmote(BOT_OPERATOR);
			}
			else if(context.isAdmin()) {
				suffix = suffix + Emote.getEmote(BOT_ADMIN);
			}
		}
		if(isLegacy()) {
			suffix += Emote.getEmote(LEGACY);
		}
		if(isVerified()) {
			suffix += Emote.getEmote(VERIFIED);
			if(this.isBanned()) {
				if(!isOnline()) {
					suffix += Emote.getEmote(BANNED);
				}
			}
			return String.format(prefix + " " + name +  " - Discord❲" + getPrettyDiscord() + "❳" + suffix);
		}
		else if(this.isBanned()) {
			if(!isOnline()) {
				suffix += Emote.getEmote(BANNED);
			}
		}
		if(!suffix.isEmpty()) {
			suffix = suffix + " ";
		}
		return String.format(prefix + " " + name + " " + suffix);
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
		if(discordID != -1) {
			MessageContext context = new MessageContext(DiscordUser.getDiscordUserIncludingUnknown(ConsoleContext.INSTANCE, discordID));
			if(context.isOperator()) {
				suffix = suffix + Emote.getEmote(BOT_OPERATOR);
			}
			else if(context.isAdmin()) {
				suffix = suffix + Emote.getEmote(BOT_ADMIN);
			}
		}
		if(isLegacy()) {
			suffix += Emote.getEmote(LEGACY);
		}
		if(isVerified()) {
			suffix += Emote.getEmote(VERIFIED);
			if(this.isBanned()) {
				if(!isOnline()) {
					suffix += Emote.getEmote(BANNED);
				}
			}
			return String.format(prefix + " " + name +  " - FC❲" + friendCode +  "❳ - PID❲"  + playerID + "❳ - Discord❲" + getPrettyDiscord() + "❳" + suffix);
		}
		else if(this.isBanned()) {
			if(!isOnline()) {
				suffix += Emote.getEmote(BANNED);
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
				prefix = prefix + Emote.getEmote(BANNED);
			}
			if(isGlobal()) {
				if(isSearching()) {
					prefix = prefix + Emote.getEmote(SEARCHING);
				}
				else {
					if(isHosting()) {
						prefix = prefix + Emote.getEmote(HOSTING);
					}
					else if (!isBanned()){
						prefix = prefix + Emote.getEmote(ONLINE);
					}
				}
			}
			else if (isPrivate()){
				if(isHosting()) {
					prefix = prefix + Emote.getEmote(HOSTING_PRIVATE);
				}
				else if (!isBanned()){
					prefix = prefix + Emote.getEmote(ONLINE_PRIVATE);
				}
			}
			else {
				prefix = prefix + Emote.getEmote(FRIENDS_LIST);
			}
		}
		else {
			prefix = prefix + Emote.getEmote(OFFLINE);
		}
		return prefix;
	}
	
	@Override
	public String getName() {
		try {
			if(isRedacted()) {
				return "REDACTED_NAME";
			}
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, PLAYER_NAME, PLAYERS, new Comparison(PLAYER_ID, EQUALS, getID()));
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
			NameChangeAudit.addNameChange(new MessageContext(this), this, name);
			Table.updateWhere(ConsoleContext.INSTANCE, PLAYERS, PLAYER_NAME, name, new Comparison(PLAYER_NAME, EQUALS, getName()));
		}
	}
	
	public void setOnlineStatus(String status) {
		this.onlineStatus = status;
	}
	
	public void setConnectionStatus(int status) {
		this.connectionStatus = status;
	}
	
	public String getFriendCode() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, FRIEND_CODE, PLAYERS, new Comparison(PLAYER_ID, EQUALS, getID()));
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
		return getDiscord() != -1;
	}
	
	@SuppressWarnings("deprecation")
	public boolean isBot() {
		DiscordUser discordUser = DiscordUser.getDiscordUser(ConsoleContext.INSTANCE, getDiscord());
		if(discordUser != null) {
			User user = discordUser.getJDAUser();
			if(user != null) {
				return user.isBot();
			}
		}
		return false;
	}
	
	public boolean isRedacted() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, REDACTED, PLAYERS, new Comparison(PLAYER_ID, EQUALS, getID()));
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
		return onlineStatus.contains("o");
	}
	
	public boolean isHosting() {
		return host == 2;
	}
	
	public boolean isGlobal() {
		return onlineStatus.contains("G");
	}
	
	public boolean isPrivate() {
		return onlineStatus.contains("g");
	}
	
	public boolean isSearching() {
		return onlineStatus.contains("S") && !isHosting() ;
	}
	
	public void setHost(int host) {
		this.host = host;
	}
	
	public String getStatus() {
		return onlineStatus;
	}
	
	@SuppressWarnings("deprecation")
	public long getDiscord() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, DISCORD_ID, PLAYERS, new Comparison(PLAYER_ID, EQUALS, getID()));
			if(result.next()) {
				long ret = result.getLong(1);
				if(ret != 0) {
					return ret;
				}
				return -1;
			}
			throw new AssertionError("Could not find player with pid " + playerID);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public String getPrettyDiscord() {
		return DiscordUser.getDiscordUserIncludingUnknown(ConsoleContext.INSTANCE, getDiscord()).toString();
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
	public Ban ban(MessageContext context, Duration duration, String reason) {
		Ban ban = Ban.addBan(context, this, reason, duration);
		DiscordUser discord = DiscordUser.getDiscordUserIncludingUnknown(context, getDiscord());
		if(!(discord instanceof UnknownDiscordUser)) {
			discord.sendMessage(context, toString() + " " + reason);
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
	
	@SuppressWarnings("rawtypes")
	public static boolean isPlayerKnown(MessageContext context, int pid) {
		return Table.existsWhere(context, PLAYERS, new Comparison(PLAYER_ID, EQUALS, pid));
	}
	
	@SuppressWarnings("rawtypes")
	public static Player getPlayerByID(MessageContext context, int pid) {
		try {
			ResultSet rs = Table.selectAllFromWhere(context, PLAYERS, new Comparison(PLAYER_ID, EQUALS, pid));
			if(rs.next()) {
				return new Player(rs);
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
		return UnknownPlayer.INSTANCE;
	}
	
	@Deprecated
	@SuppressWarnings("rawtypes")
	public static Player[] getPlayersByName(MessageContext context, String name) {
		HashSet<Player> players = new HashSet<Player>();
		try {
			ResultSet rs = Table.selectAllFromWhere(context, PLAYERS, new Comparison(PLAYER_NAME, EQUALS, name));
			while(rs.next()) {
				players.add(new Player(rs));
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
		return players.toArray(new Player[]{});
	}
	
	public static Player[] getPlayersByAnyIdentifier(MessageContext context, String identifier) {
		HashSet<Player> players = new HashSet<Player>();
		try {
			ResultSet rs = Table.selectAllFromWhere(context, PLAYERS, 
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
	
	public static Player[] getUnclaimedPlayersByAnyIdentifier(MessageContext context, String identifier) {
		HashSet<Player> players = new HashSet<Player>();
		try {
			ResultSet rs = Table.selectAllFromWhere(context, PLAYERS, 
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
	public static Player[] getEncounteredPlayers(MessageContext context) {
		HashSet<Player> players = new HashSet<Player>();
		try {
			ResultSet rs = Table.selectAllFrom(context, PLAYERS);
			while(rs.next()) {
				players.add(new Player(rs));
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
		return players.toArray(new Player[]{});
	}
}