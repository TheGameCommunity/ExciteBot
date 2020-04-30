package com.gamebuster19901.excite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;

import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.output.OutputCSV;

import net.dv8tion.jda.api.entities.User;

public class Player implements OutputCSV{
	private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
	
	protected static final String LEGACY = new String(Character.toChars(0x1F396));
	protected static final String VERIFIED = new String(Character.toChars(0x2705));
	protected static final String ZEROLOSS = new String(Character.toChars(0x2B50));
	protected static final String BANNED = new String(Character.toChars(0x274C));
	protected static final File KNOWN_PLAYERS = new File("./run/encounteredPlayers.csv");
	protected static final File OLD_KNOWN_PLAYERS = new File("./run/encounteredPlayers.csv.old");
	
	private static HashSet<Player> knownPlayers;
	static {
		try {
			if(!KNOWN_PLAYERS.exists()) {
				KNOWN_PLAYERS.getParentFile().mkdirs();
				KNOWN_PLAYERS.createNewFile();
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
		String prefix = "";
		if(isZeroLoss()) {
			prefix += ZEROLOSS;
		}
		if(isLegacy()) {
			prefix += LEGACY;
		}
		if(isVerified()) {
			DiscordUser user = DiscordUser.getDiscordUser(discord);
			if(!user.isBanned()) {
				prefix += VERIFIED;
			}
			else {
				prefix += BANNED;
			}
			return String.format(prefix + " " + getPrettyDiscord() + "(" + name + ") - FC:[" + friendCode +  "] - PID:["  + playerID + "]");
		}
		if(!prefix.isEmpty()) {
			prefix = " ";
		}
		return String.format(prefix + name + " - FC:[" + friendCode +  "] - PID:["  + playerID + "]");
	}
	
	public String toCSV() {
		return playerID + "," + friendCode + "," +  name + "," + discord + "," + zeroLoss;
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
	
	public boolean isLegacy() {
		return playerID < 600000000;
	}
	
	public boolean isVerified() {
		return getDiscord() != -1;
	}
	
	public boolean isZeroLoss() {
		return zeroLoss;
	}
	
	public long getDiscord() {
		return discord;
	}
	
	public String getPrettyDiscord() {
		if(Main.discordBot != null) {
			User user = DiscordUser.getJDAUser(discord);
			if(user != null) {
				return user.getAsTag();
			}
		}
		return "<" + getDiscord() + ">"; //if for some reason we cannot get the user's account
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
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(KNOWN_PLAYERS));
				
				while(reader.ready()) {
					String line = reader.readLine();
					if(line.startsWith("pid")) {
						continue;
					}
					
					int playerID = Integer.MIN_VALUE;
					String friendCode = null;
					String name = null;
					long discord = -1;
					boolean zeroLoss = false;
					
					String[] data = line.split(",");
					
					for(int i = 0; i < data.length; i++) {
						if (data[i] == null) {
							throw new IllegalArgumentException("argument " + i + " in \"" + line + "\"");
						}
					}
					
					playerID = Integer.parseInt(data[0]);
					friendCode = data[1];
					name = data[2];
					if(data[3].isEmpty()) {
						data[3] = "-1";
					}
					discord = Long.parseLong(data[3]);
					zeroLoss = Boolean.parseBoolean(data[4]);
					
					players.add(new Player(name, friendCode, playerID, discord, zeroLoss));
				}
			}
			finally {
				if(reader != null) {
					reader.close();
				}
			}
		}
		catch(IOException e) {
			throw new AssertionError(e);
		}
		return players.toArray(new Player[]{});
	}
	
}
