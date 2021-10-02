package com.gamebuster19901.excite;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.gamebuster19901.excite.bot.audit.LogInAudit;
import com.gamebuster19901.excite.bot.audit.LogOutAudit;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.exception.WiimmfiErrorResponse;
import com.gamebuster19901.excite.exception.WiimmfiResponseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Wiimmfi {
	
	private static final Logger logger = Logger.getLogger(Wiimmfi.class.getName());
	
	
	private static final URL EXCITEBOTS;
	private static JsonElement JSON;
	private static HashSet<Player> PREV_ONLINE_PLAYERS = new HashSet<Player>();
	private static HashSet<Player> ONLINE_PLAYERS = new HashSet<Player>();
	private static HashSet<Player> HOSTING_PLAYERS = new HashSet<Player>();
	static {
		try {
			String key = IOUtils.toString(new FileInputStream(new File("./wiimmfi.secret")), Charsets.UTF_8);
			EXCITEBOTS = new URL("https://wiimmfi.de/json/jacc/" + key + "/games/exciteracewii");
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	private Instant nextPing = Instant.now().plus(Duration.ofSeconds(20));
	private URL url;
	private Throwable error = null;
	
	public Wiimmfi() {
		this(EXCITEBOTS);
	}
	
	public Wiimmfi(URL url) {
		update(url);
	};
	
	public Wiimmfi(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			error = e;
			logger.log(Level.SEVERE, e, () -> e.getMessage());
		}
		update();
	}

	public void update(URL url) {
		this.url = url;
		update();
	}
	
	public void update() {
		if(Instant.now().isAfter(nextPing)) {
			if(url != null) {
				try {
					URLConnection connection = EXCITEBOTS.openConnection();
					connection.setRequestProperty("User-Agent", "Excitebot (+https://github.com/Gamebuster19901/ExciteBot)");
					InputStream is = connection.getInputStream();
					JSON = JsonParser.parseString(IOUtils.toString(is, StandardCharsets.UTF_8));
					is.close();
					error = null;
				}
				catch(Exception e) {
					error = e;
				}
			}
			else {
				if(error == null) {
					error = new NullPointerException("No url or file provided!");
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Player[] updateOnlinePlayers() throws SQLException, WiimmfiResponseException {
		HashSet<Player> onlinePlayers = new HashSet<Player>();
		HashSet<Player> hostingPlayers = new HashSet<Player>();
		
		if (JSON != null) {
			JsonArray objects = null;
			JsonElement object1;
			HashMap<String, JsonElement> object1Entries = new HashMap<String, JsonElement>();
			if(JSON.isJsonArray()) {
				objects = JSON.getAsJsonArray();
				object1 = objects.get(0);
			}
			else {
				object1 = JSON;
			}
			
			for(Entry<String, JsonElement> e : object1.getAsJsonObject().entrySet()) {
				if(object1Entries.put(e.getKey(), e.getValue()) != null) {
					throw new WiimmfiResponseException("Duplicate key in json response:" + e.getKey());
				}
			}
			
			JsonElement typeJson = object1Entries.get("type");
			JsonElement identifyJson = object1Entries.get("identify");
			JsonElement gameJson = object1Entries.get("game_list");
			
			String type = null;
			String identify = null;
			String game = null;
			if(typeJson != null) {
				type = typeJson.getAsString();
			}
			
			if(type == null) {
				throw new WiimmfiResponseException("Unexpected response from wiimmfi api");
			}
			if(type.equals("error")) {
				JsonElement errorJson = object1Entries.get("error");
				JsonElement msgJson = object1Entries.get("msg");
				String error = null;
				String msg = null;
				if(errorJson != null) {
					error = errorJson.getAsString();
				}
				else {
					error = "No error type received from wiimmfi";
				}
				if(msgJson != null) {
					msg = msgJson.getAsString();
				}
				throw new WiimmfiErrorResponse(error + ": " + msg);
			}
			
			if(identifyJson != null) {
				identify = identifyJson.getAsString();
			}
			if(gameJson != null) {
				if(gameJson.getAsJsonArray().size() > 0) {
					game = gameJson.getAsJsonArray().get(0).getAsString();
				}
				else {
					throw new WiimmfiResponseException("No game data response");
				}
			}
			
			if(!"games".equals(identify)) {
				throw new WiimmfiResponseException("Unexpected response of type: " + identify);
			}
			if(!"exciteracewii".equals(game)) {
				throw new WiimmfiResponseException("Wiimmfi sent player list from incorrect game: " + game);
			}
			
			elementFinder:
			for(int i = 1; i < objects.size(); i++) { //should be safe to access w/o null check as it should error before now
				JsonElement obj = objects.get(i);
				HashMap<String, JsonElement> entries = new HashMap<String, JsonElement>();
				for(Entry<String, JsonElement> e : obj.getAsJsonObject().entrySet()) {
					if(entries.put(e.getKey(), e.getValue()) != null) {
						throw new WiimmfiResponseException("Duplicate key in json response:" + e.getKey());
					}
				}
				
				if("game-stats".equals(entries.get("type").getAsString())) {
					JsonArray playerList = entries.get("list").getAsJsonArray();
					for(JsonElement e : playerList) {
						HashMap<String, JsonElement> playerDataEntries = new HashMap<String, JsonElement>();
						for(Entry<String, JsonElement> e2 : e.getAsJsonObject().entrySet()) {
							playerDataEntries.put(e2.getKey(), e2.getValue());
						}
						
						int pid = playerDataEntries.get("pid").getAsInt();
						String fc = playerDataEntries.get("fc").getAsString();
						int status = playerDataEntries.get("online_status").getAsInt();
						int host = playerDataEntries.get("hoststate").getAsInt();
						String name = playerDataEntries.get("name").getAsJsonArray().get(0).getAsString();
						
						Player player = Player.getPlayerByID(ConsoleContext.INSTANCE, pid);
						if(player instanceof UnknownPlayer) {
							player = Player.addPlayer(new MessageContext(player), true, pid, fc, name);
						}
						else {
							player.setName(name);
							player.setOnlineStatus(status);
							player.setHost(host);
						}
						onlinePlayers.add(player);
					};
					break elementFinder;
				}
			}
		}
		
		/*if(document != null) {
			document.getElementsByAttributeValueContaining("id", "game").remove();
			Elements elements = document.getElementsByClass("tr0");
			elements.addAll(document.getElementsByClass("tr1"));
			for(Element e : elements) {
				if(!e.hasClass("tr0") && !e.hasClass("tr1")) {
					e.remove();
				}
			}
			if(elements.size() > 0) {
				Elements playerEntries = elements;
				for(Element e : playerEntries) {
					
					String name = parseLine(e.html(), 10);
					int hosting = 0;
					String hostingString = parseLine(e.html(), 4);
					if(!hostingString.equals("<tdclass=\"dbnull\">—")) {
						hosting = Integer.parseInt(parseLine(e.html(), 3));
					}
					String status = parseLine(e.html(), 6);
					
					int playerId = Integer.parseInt(parseLine(e.html(), 1));
					
					Player player = Player.getPlayerByID(ConsoleContext.INSTANCE, playerId);
					if(player instanceof UnknownPlayer) {
						String friendCode = parseLine(e.html(), 2);
						player = Player.addPlayer(new MessageContext(player), true, playerId, friendCode, name);
					}
					else {
						player.setName(name);
						player.setOnlineStatus(status);
						player.setHost(hosting);
					}
					onlinePlayers.add(player);
				}
			}
		}*/
		
		for(Player player : onlinePlayers) {
			if(PREV_ONLINE_PLAYERS.contains(player)) {
				if(!(player.isPrivate() || player.isSearching() || player.isFriendsList())) {
					player.updateSecondsPlayed();
				}
				player.updateLastOnline();
			}
			else {
				LogInAudit.addLoginAudit(new MessageContext(player), player);
				player.updateLastOnline();
			}
			PREV_ONLINE_PLAYERS.remove(player);
		}
		for(Player player : PREV_ONLINE_PLAYERS) {
			LogOutAudit.addLogOutAudit(new MessageContext(player), player);
		}
		
		ONLINE_PLAYERS = onlinePlayers;
		HOSTING_PLAYERS = hostingPlayers;
		PREV_ONLINE_PLAYERS = ONLINE_PLAYERS;
		
		return onlinePlayers.toArray(new Player[]{});
	}
	
	public static HashSet<Player> getOnlinePlayers() {
		return ONLINE_PLAYERS;
	}
	
	public static Player getOnlinePlayerByID(long id) {
		for(Player player : getOnlinePlayers()) {
			if(player.getID() == id) {
				return player;
			}
		}
		return null;
	}
	
	public static HashSet<Player> getIgnoredOnlinePlayers() {
		HashSet<Player> players = new HashSet<Player>();
		for(Player player : getOnlinePlayers()) {
			if(player.isBanned() || player.isBot() || !player.isGlobal()) {
				players.add(player);
			}
		}
		return players;
	}
	
	public static String getOnlinePlayerList(boolean full) {
		Player[] onlinePlayers = getOnlinePlayers().toArray(new Player[]{});
		Player[] ignoredPlayers = getIgnoredOnlinePlayers().toArray(new Player[]{});
		
		String response;
		
		if(ignoredPlayers.length == 0) {
			response = ": (" + onlinePlayers.length + ")\n\n";
		}
		else {
			response = ": " + (getAcknowledgedPlayerCount()) + " (" + getIgnoredOnlinePlayerCount() + " ignored)" + "\n\n";
		}
		
		if(full) {
			for(int i = 0; i < onlinePlayers.length ; i++) {
				response += onlinePlayers[i].toFullString() + '\n';
			}
		}
		else {
			for(int i = 0; i < onlinePlayers.length ; i++) {
				response += onlinePlayers[i].toString() + '\n';
			}
		}

		return response;
	}
	
	public static int getIgnoredOnlinePlayerCount() {
		return getIgnoredOnlinePlayers().size();
	}
	
	public static int getOnlinePlayerCount() {
		return getOnlinePlayers().size();
	}
	
	public static int getAcknowledgedPlayerCount() {
		return getOnlinePlayerCount() - getIgnoredOnlinePlayerCount();
	}
	
	public Throwable getError() {
		return error;
	}
	
	private static String parseLine(String s, int line) {
		String[] lines = s.split("\n");
		return lines[line].replace("<td>", "").replaceAll("</td>", "").replaceAll(" ", "");
	}
	
}
