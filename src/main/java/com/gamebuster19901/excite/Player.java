package com.gamebuster19901.excite;

import java.io.File;
import java.io.IOError;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.logging.Logger;

import com.gamebuster19901.excite.bot.server.emote.Emote;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.database.sql.ResultSet;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import static com.gamebuster19901.excite.bot.database.Table.PLAYERS;
import static com.gamebuster19901.excite.bot.database.Comparator.EQUALS;
import static com.gamebuster19901.excite.bot.database.Column.*;

import net.dv8tion.jda.api.entities.User;

public class Player {
	private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
	
	protected static final String LEGACY = new String("legacy");
	protected static final String VERIFIED = new String("verified");
	protected static final String BANNED = new String("banned");
	protected static final String ONLINE = new String("online");
	protected static final String OFFLINE = new String("offline");
	protected static final String HOSTING = new String("hosting");
	protected static final String RACING = new String("playing");
	protected static final String SEARCHING = new String("searching");
	protected static final String SPECTATING = new String("spectating");
	protected static final String FRIENDS_LIST = new String("friend_list");
	protected static final String BOT = new String(Character.toChars(0x1F916));
	protected static final String BOT_ADMIN = new String("bot_admin");
	protected static final String BOT_OPERATOR = new String("bot_operator");
	
	protected static final File KNOWN_PLAYERS = new File("./run/encounteredPlayers.csv");
	protected static final File OLD_KNOWN_PLAYERS = new File("./run/encounteredPlayers.csv.old");
	
	private final int playerID;
	
	private transient boolean hosting;
	private transient String status;
	
	private Player(ResultSet results) throws SQLException {
		this.playerID = results.getInt(PLAYER_ID);
	}

	protected Player(int playerID) {
		this.playerID = playerID;
	}

	@SuppressWarnings({ "rawtypes", "resource" })
	public static Player addPlayer(MessageContext context, int playerID, String friendCode, String name) throws SQLException {
		//INSERT INTO `excitebot`.`players` (`playerID`, `friendCode`, `name`) VALUES ('01234', '5678-9012-3456', 'Fake');
		PreparedStatement ps = context.getConnection().prepareStatement("INSERT INTO ? (?, ?, ?) VALUES (?, ?, ?);");
		ps.setString(1, Table.PLAYERS.toString());
		ps.setString(2, PLAYER_ID);
		ps.setString(3, FRIEND_CODE);
		ps.setString(4, PLAYER_NAME);
		ps.setInt(5, playerID);
		ps.setString(6, friendCode);
		ps.setString(7, name);
		ps.execute();
		return getPlayerByID(context, playerID);
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String toString() {
		String name = getName();
		long discordID = getDiscord();
		
		String prefix = "";
		String suffix = "";
		if(isOnline()) {
			if(isBanned()) {
				prefix = prefix + Emote.getEmote(BANNED);
			}
			if(isHosting()) {
				prefix = prefix + Emote.getEmote(HOSTING);
			}
			else if (!isBanned()){
				prefix = prefix + Emote.getEmote(ONLINE);
			}
		}
		else {
			prefix = prefix + Emote.getEmote(OFFLINE);
		}
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
		
		String prefix = "";
		String suffix = "";
		if(isOnline()) {
			if(isBanned()) {
				prefix = prefix + Emote.getEmote(BANNED);
			}
			if(isHosting()) {
				prefix = prefix + Emote.getEmote(HOSTING);
			}
			else if (!isBanned()){
				prefix = prefix + Emote.getEmote(ONLINE);
			}
		}
		else {
			prefix = prefix + Emote.getEmote(OFFLINE);
		}
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
	
	public String getName() {
		try {
			if(isRedacted()) {
				return "REDACTED_NAME";
			}
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, PLAYER_NAME, PLAYERS, PLAYER_ID, EQUALS, getPlayerID());
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
			//Audit.addAudit(new NameChangeAudit(this, name));
			Table.updateWhere(ConsoleContext.INSTANCE, PLAYERS, PLAYER_NAME, name, PLAYER_NAME, EQUALS, getName());
		}
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getFriendCode() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, FRIEND_CODE, PLAYERS, PLAYER_ID, EQUALS, getPlayerID());
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
	
	public int getPlayerID() {
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
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, REDACTED, PLAYERS, PLAYER_ID, EQUALS, getPlayerID());
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
		return false;
		//return ProfileBan.isProfileBanned(this);
	}
	
	public boolean isOnline() {
		return Wiimmfi.getOnlinePlayers().contains(this);
	}
	
	public boolean isHosting() {
		return hosting;
	}
	
	public String getStatus() {
		return status;
	}
	
	@SuppressWarnings("deprecation")
	public long getDiscord() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, DISCORD_ID, PLAYERS, PLAYER_ID, EQUALS, getPlayerID());
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
				Table.updateWhere(ConsoleContext.INSTANCE, PLAYERS, DISCORD_ID, discordID, PLAYER_ID, EQUALS, getPlayerID());
			}
		}
		catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
/*	@SuppressWarnings("rawtypes")
	public ProfileBan ban(MessageContext context, Duration duration, String reason) {
		ProfileBan profileBan = new ProfileBan(context, reason, duration, this);
		profileBan = Audit.addAudit(profileBan); //future proofing in case we ever need to return a different audit
		DiscordUser discord = DiscordUser.getDiscordUserIncludingUnknown(getDiscord());
		if(!(discord instanceof UnknownDiscordUser)) {
			discord.sendMessage(context, toString() + " " + reason);
		}
		return profileBan;
	}*/
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Player) {
			return ((Player) o).getPlayerID() == getPlayerID();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getPlayerID();
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isPlayerKnown(MessageContext context, int pid) {
		return Table.existsWhere(context, PLAYERS, PLAYER_ID, EQUALS, pid);
	}
	
	@SuppressWarnings("rawtypes")
	public static Player getPlayerByID(MessageContext context, int pid) {
		try {
			ResultSet rs = Table.selectAllFromWhere(context, PLAYERS, PLAYER_ID, EQUALS, pid);
			if(rs.next()) {
				return new Player(rs);
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
		return UnknownPlayer.INSTANCE;
	}
	
	@SuppressWarnings("rawtypes")
	public static Player[] getPlayersByName(MessageContext context, String name) {
		HashSet<Player> players = new HashSet<Player>();
		try {
			ResultSet rs = Table.selectAllFromWhere(context, PLAYERS, PLAYER_NAME, EQUALS, name);
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