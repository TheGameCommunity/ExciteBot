package com.gamebuster19901.excite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.server.emote.Emote;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.FileUtils;

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
	protected static final File KNOWN_PLAYERS = new File("./run/encounteredPlayers.csv");
	protected static final File OLD_KNOWN_PLAYERS = new File("./run/encounteredPlayers.csv.old");
	
	private static HashSet<Player> knownPlayers;
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
			knownPlayers = new HashSet<Player>(Arrays.asList(getEncounteredPlayersFromFile()));
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
	
	private transient Status status;
	
	public Player(String name, String friendCode, int playerID) {
		this(name, friendCode, playerID, -1, false);
	}
	
	public Player(String name, String friendCode, int playerID, long discord, boolean zeroLoss) {
		this.name = name;
		this.friendCode = friendCode;
		this.playerID = playerID;
		this.discord = discord;
		this.zeroLoss = zeroLoss;
		
	}
	
	public String toString() {
		String suffix = "";
		if(isZeroLoss()) {
			suffix += Emote.getEmote(ZEROLOSS);
		}
		if(isBot()) {
			suffix += BOT;
		}
		if(isLegacy()) {
			suffix += Emote.getEmote(LEGACY);
		}
		if(isVerified()) {
			DiscordUser user = DiscordUser.getDiscordUserIncludingUnknown(discord);
			if(!user.isBanned()) {
				suffix += Emote.getEmote(VERIFIED);
			}
			else {
				suffix += Emote.getEmote(BANNED);
			}
			return String.format(name +  " - FC:[" + friendCode +  "] - PID:["  + playerID + "] - Discord:[" + getPrettyDiscord() + "]" + suffix);
		}
		if(!suffix.isEmpty()) {
			suffix = suffix + " ";
		}
		return String.format(name + " - FC:[" + friendCode +  "] - PID:["  + playerID + "]" + suffix);
	}
	
	public String toCSV() {
		try (
			StringWriter writer = new StringWriter();
			CSVPrinter printer = new CSVPrinter(writer, CSVFormat.EXCEL);
		)
		{
			printer.printRecord(playerID, friendCode, name, "`" + discord, zeroLoss);
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
		this.name = name;
	}
	
	public String getFriendCode() {
		return friendCode;
	}
	
	public int getPlayerID() {
		return playerID;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public boolean isOnline() {
		return getStatus() != OFFLINE;
	}
	
	public boolean isHosting() {
		return status == HOSTING;
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
	
	public boolean isZeroLoss() {
		return zeroLoss;
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
		return null;
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
					
					players.add(new Player(name, friendCode, playerID, discord, zeroLoss));
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
