package com.gamebuster19901.excite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import com.gamebuster19901.excite.bot.audit.LogInAudit;
import com.gamebuster19901.excite.bot.audit.LogOutAudit;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.exception.WiimmfiErrorResponse;
import com.gamebuster19901.excite.exception.WiimmfiResponseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Wiimmfi {
	
	private static final Logger logger = Logger.getLogger(Wiimmfi.class.getName());
	
	private static final URL EXCITEBOTS;
	private static JsonElement JSON;
	private static HashSet<Player> PREV_ONLINE_PLAYERS = new HashSet<>();
	private static HashSet<Player> PREV_ONLINE_PLAYERS_THAT_LOGGED_OUT = new HashSet<Player>();
	private static HashSet<Player> ONLINE_PLAYERS = new HashSet<Player>();
	private static final Duration WAIT_TIME = Duration.ofSeconds(20);
	static {
		try {
			EXCITEBOTS = new URL("https://wiimmfi.de/json/jacc/@/games/exciteracewii");
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	private Instant nextPing = Instant.EPOCH;
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
					String key = IOUtils.toString(new FileInputStream(new File("./wiimmfi.secret")), Charsets.UTF_8);
					HttpClient client = HttpClients.custom().build();
					HttpUriRequest request = RequestBuilder.get(EXCITEBOTS.toURI()).setHeader(new BasicHeader("X-Wiimmfi-Key", key)).setHeader(new BasicHeader(HttpHeaders.USER_AGENT, "Excitebot (+https://gamebuster19901.com/ExciteBot)")).build();
					HttpResponse response = client.execute(request);
					JSON = JsonParser.parseString(new BasicResponseHandler().handleResponse(response));
					updateOnlinePlayers();
					error = null;
				}
				catch(Exception e) {
					error = e;
					System.out.println(e);
				}
			}
			else {
				if(error == null) {
					error = new NullPointerException("No url or file provided!");
				}
			}
			nextPing = Instant.now().plus(WAIT_TIME);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Player[] updateOnlinePlayers() throws SQLException, WiimmfiResponseException {
		HashSet<Player> onlinePlayers = new HashSet<Player>();
		
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
							player = Player.addPlayer(new CommandContext(player), true, pid, fc, name);
						}
						else {
							System.out.println(status);
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
		
		/*
		//DEBUG
		Player debugPlayer = Player.getPlayerByID(ConsoleContext.INSTANCE, 999999996);
		if(debugPlayer instanceof UnknownPlayer) {
			debugPlayer = Player.addPlayer(ConsoleContext.INSTANCE, true, 999999996, "0000-0000-0003", "Invalid");
		}
		debugPlayer.setOnlineStatus(2);
		onlinePlayers.add(debugPlayer);
		*/
		
		for(Player player : onlinePlayers) {
			if(PREV_ONLINE_PLAYERS.contains(player)) {
				if(!(player.isPrivate() || player.isSearching() || player.isFriendsList())) {
					player.updateSecondsPlayed();
				}
				player.updateLastOnline();
			}
			else {
				LogInAudit.addLoginAudit(new CommandContext(player), player);
				player.updateLastOnline();
			}
			PREV_ONLINE_PLAYERS_THAT_LOGGED_OUT.remove(player);
		}
		for(Player player : PREV_ONLINE_PLAYERS_THAT_LOGGED_OUT) {
			LogOutAudit.addLogOutAudit(new CommandContext(player), player);
		}
		
		ONLINE_PLAYERS = onlinePlayers;
		PREV_ONLINE_PLAYERS = ONLINE_PLAYERS;
		PREV_ONLINE_PLAYERS_THAT_LOGGED_OUT = ONLINE_PLAYERS;
		
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
