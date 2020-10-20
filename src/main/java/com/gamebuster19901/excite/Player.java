package com.gamebuster19901.excite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.server.emote.Emote;
import com.gamebuster19901.excite.bot.audit.Audit;
import com.gamebuster19901.excite.bot.audit.NameChangeAudit;
import com.gamebuster19901.excite.bot.audit.ProfileDiscoveryAudit;
import com.gamebuster19901.excite.bot.audit.ban.ProfileBan;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.file.FileUtils;

import net.dv8tion.jda.api.entities.User;

public class Player implements OutputCSV{
	private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
	
	protected static final String LEGACY = new String("legacy");
	protected static final String VERIFIED = new String("verified");
	protected static final String ZEROLOSS = new String(Character.toChars(0x2B50));
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
	
	private static Set<Player> knownPlayers;
	static {
		try {
			if(!KNOWN_PLAYERS.exists()) {
				KNOWN_PLAYERS.getParentFile().mkdirs();
				KNOWN_PLAYERS.createNewFile();
			}
			else {
				if(OLD_KNOWN_PLAYERS.exists()) {
					if(!FileUtils.contentEquals(KNOWN_PLAYERS, OLD_KNOWN_PLAYERS)) {
						throw new IOException("File content differs!");
					}
				}
			}
			knownPlayers = Collections.synchronizedSet(new HashSet<Player>(Arrays.asList(getEncounteredPlayersFromFile())));
		}
		catch(IOException e) {
			throw new IOError(e);
		}
	}
	
	private String name;
	private final String friendCode;
	private final int playerID;
	
	private boolean zeroLoss = false;
	private long discord = -1;
	private ProfileDiscoveryAudit discoveryAudit;
	
	private transient boolean hosting;
	private transient String status;
	
	@Deprecated
	public Player(String name, String friendCode, int playerID, long discord, boolean zeroLoss) {
		this.name = name;
		this.friendCode = friendCode;
		this.playerID = playerID;
		this.discord = discord;
		this.zeroLoss = zeroLoss;
		if(!(this instanceof UnknownPlayer)) {
			this.discoveryAudit = Audit.addAudit(new ProfileDiscoveryAudit(this));
		}
	}
	
	public Player(String name, String friendCode, int playerID) {
		this(name, friendCode, playerID, -1, false);
	}
	
	public Player(String name, String friendCode, int playerID, long discord, boolean zeroLoss, long discoveryAudit) {
		this.name = name;
		this.friendCode = friendCode;
		this.playerID = playerID;
		this.discord = discord;
		this.zeroLoss = zeroLoss;
		if(!(this instanceof UnknownPlayer)) {
			this.discoveryAudit = (ProfileDiscoveryAudit) Audit.getAuditById(discoveryAudit);
		}
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String toString() {
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
		if(isZeroLoss()) {
			suffix += Emote.getEmote(ZEROLOSS);
		}
		if(isBot()) {
			suffix += BOT;
		}
		if(discord != -1) {
			MessageContext context = new MessageContext(DiscordUser.getDiscordUserIncludingUnknown(discord));
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
			DiscordUser user = DiscordUser.getDiscordUserIncludingUnknown(discord);
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
		if(isZeroLoss()) {
			suffix += Emote.getEmote(ZEROLOSS);
		}
		if(isBot()) {
			suffix += BOT;
		}
		if(discord != -1) {
			MessageContext context = new MessageContext(DiscordUser.getDiscordUserIncludingUnknown(discord));
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
			DiscordUser user = DiscordUser.getDiscordUserIncludingUnknown(discord);
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
	
	public String toCSV() {
		try (
			StringWriter writer = new StringWriter();
			CSVPrinter printer = new CSVPrinter(writer, CSVFormat.EXCEL);
		)
		{
			printer.printRecord(playerID, friendCode, name, "`" + discord, zeroLoss, "`" + discoveryAudit.getAuditId());
			printer.flush();
			return writer.toString();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if(this.name != null && !this.name.equals(name)) {
			Audit.addAudit(new NameChangeAudit(this, name));
		}
		this.name = name;
	}
	
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getFriendCode() {
		return friendCode;
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
	
	public boolean isBot() {
		DiscordUser discordUser = DiscordUser.getDiscordUser(getDiscord());
		if(discordUser != null) {
			User user = discordUser.getJDAUser();
			if(user != null) {
				return user.isBot();
			}
		}
		return false;
	}

	public boolean isBanned() {
		return ProfileBan.isProfileBanned(this);
	}
	
	public boolean isZeroLoss() {
		return zeroLoss;
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
	
	public long getDiscord() {
		return discord;
	}
	
	public String getPrettyDiscord() {
		return DiscordUser.getDiscordUserIncludingUnknown(discord).toString();
	}
	
	public void setDiscord(long discordId) {
		this.discord = discordId;
	}
	
	@SuppressWarnings("rawtypes")
	public ProfileBan ban(MessageContext context, Duration duration, String reason) {
		ProfileBan profileBan = new ProfileBan(context, reason, duration, this);
		profileBan = Audit.addAudit(profileBan); //future proofing in case we ever need to return a different audit
		DiscordUser discord = DiscordUser.getDiscordUserIncludingUnknown(getDiscord());
		if(!(discord instanceof UnknownDiscordUser)) {
			discord.sendMessage(context, toString() + " " + reason);
		}
		return profileBan;
	}
	
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
	
	public static boolean isPlayerKnown(int pid) {
		for(Player player : knownPlayers) {
			if(player.playerID == pid) {
				return true;
			}
		}
		return false;
	}
	
	public static Player getPlayerByID(int pid) {
		for(Player player : knownPlayers) {
			if (player.playerID == pid) {
				return player;
			}
		}
		return UnknownPlayer.INSTANCE;
	}
	
	public static Player[] getPlayersByName(String name) {
		HashSet<Player> players = new HashSet<Player>();
		for(Player player : knownPlayers) {
			if(player.getName().equalsIgnoreCase(name)) {
				players.add(player);
			}
		}
		return players.toArray(new Player[]{});
	}
	
	/**
	 * @deprecated use DiscordUser.getProfiles()
	 */
	@Deprecated
	public static Player[] getPlayersByDiscord(String name, String discriminator) {
		return getPlayersByDiscord(DiscordUser.getJDAUser(name, discriminator));
	}
	
	/**
	 * @deprecated use DiscordUser.getProfiles()
	 */
	@Deprecated
	public static Player[] getPlayersByDiscord(User user) {
		HashSet<Player> players = new HashSet<Player>();
		if(Main.discordBot != null) {
			if(user != null) {
				for(Player player : knownPlayers) {
					if(player.getPrettyDiscord() == user.getAsTag()) {
						players.add(player);
					}
				}
			}
		}
		return players.toArray(new Player[]{});
	}
	
	/**
	 * @deprecated use DiscordUser.getProfiles()
	 */
	@Deprecated
	public static Player[] getPlayersByDiscord(DiscordUser user) {
		return getPlayersByDiscord(user.getJDAUser());
	}
	
	/**
	 * @deprecated use DiscordUser.getProfiles()
	 */
	public static Player[] getPlayersByDiscord(long id) {
		return getPlayersByDiscord(DiscordUser.getJDAUser(id));
	}
	
	public static void addPlayer(Player player) {
		if(knownPlayers.contains(player)) {
			throw new IllegalArgumentException("Player already known!: " + player.toString());
		}
		LOGGER.info("New Player found!: " + player.toString());
		knownPlayers.add(player);
	}
	
	public static void updatePlayerListFile() {
		BufferedWriter writer = null;
		try {
			if(OLD_KNOWN_PLAYERS.exists()) {
				OLD_KNOWN_PLAYERS.delete();
			}
			if (!KNOWN_PLAYERS.renameTo(OLD_KNOWN_PLAYERS)) {
				throw new IOException();
			}
			KNOWN_PLAYERS.createNewFile();
			writer = new BufferedWriter(new FileWriter(KNOWN_PLAYERS));
			for(Player p : knownPlayers) {
				writer.write(p.toCSV());
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
	
	public static Player[] getEncounteredPlayers() {
		return knownPlayers.toArray(new Player[] {});
	}
	
	public static Player[] getPlayersFromIds(int[] ids) {
		HashSet<Player> players = new HashSet<Player>();
		for(int i = 0; i < ids.length; i++) {
			Player player = getPlayerByID(ids[i]);
			if(player != null) {
				players.add(player);
			}
		}
		return players.toArray(new Player[]{});
	}
	
	private static Player[] getEncounteredPlayersFromFile() {
		HashSet<Player> players = new HashSet<Player>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(KNOWN_PLAYERS));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withTrim(false));
			try {
				reader = new BufferedReader(new FileReader(KNOWN_PLAYERS));
				
				int playerID = Integer.MIN_VALUE;
				String friendCode = null;
				String name = null;
				long discord = -1;
				boolean zeroLoss = false;
				long discoveryAudit = -1;
				
				for(CSVRecord csvRecord : csvParser ) {
					playerID = Integer.parseInt(csvRecord.get(0));
					friendCode = csvRecord.get(1);
					name = csvRecord.get(2);
					String discordId = csvRecord.get(3);
					if(discordId.isEmpty()) {
						discord = -1;
					}
					discord = Long.parseLong(discordId.substring(1));
					zeroLoss = Boolean.parseBoolean(csvRecord.get(4));
					if(csvRecord.size() > 5) {
						discoveryAudit = Long.parseLong(csvRecord.get(5).substring(1));
						players.add(new Player(name, friendCode, playerID, discord, zeroLoss, discoveryAudit));
					}
					else {
						players.add(new Player(name, friendCode, playerID, discord, zeroLoss));
					}
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
		return players.toArray(new Player[]{});
	}
	
}