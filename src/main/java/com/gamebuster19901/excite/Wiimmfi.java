package com.gamebuster19901.excite;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gamebuster19901.excite.bot.command.MessageContext;

public class Wiimmfi {
	
	private static final Logger logger = Logger.getLogger(Wiimmfi.class.getName());
	
	private static final URL EXCITEBOTS;
	private static Document document;
	private static HashSet<Player> ONLINE_PLAYERS = new HashSet<Player>();
	private static HashSet<Player> HOSTING_PLAYERS = new HashSet<Player>();
	static {
		try {
			EXCITEBOTS = new URL("https://wiimmfi.de/stats/game/exciteracewii");
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
	}
	
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
		if(url != null) {
			try {
				if(url.getProtocol().equals("file")) {
					document = Jsoup.parse(new File(url.toURI()), null);
				}
				else {
					document = Jsoup.connect(url.toString()).get();
				}
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
	
	public static Player[] updateOnlinePlayers() {
		HashSet<Player> onlinePlayers = new HashSet<Player>();
		HashSet<Player> hostingPlayers = new HashSet<Player>();
		if(document != null) {
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
					boolean hosting = Integer.parseInt(parseLine(e.html(), 4)) != 0;
					String status = parseLine(e.html(), 7);
					
					hosting = !status.equals("o") && hosting;
					
					int playerId = Integer.parseInt(parseLine(e.html(), 1));
					
					Player player = Player.getPlayerByID(playerId);
					if(player instanceof UnknownPlayer) {
						String friendCode = parseLine(e.html(), 2);
						player = new Player(name, friendCode, playerId);
						Player.addPlayer(player);
					}
					else {
						player.setName(name);
						player.setStatus(status);
					}
					onlinePlayers.add(player);
				}
			}
		}
		ONLINE_PLAYERS = onlinePlayers;
		HOSTING_PLAYERS = hostingPlayers;
		return onlinePlayers.toArray(new Player[]{});
	}
	
	public static HashSet<Player> getOnlinePlayers() {
		return ONLINE_PLAYERS;
	}
	
	public static HashSet<Player> getIgnoredOnlinePlayers() {
		HashSet<Player> players = new HashSet<Player>();
		for(Player player : getOnlinePlayers()) {
			if(player.isBanned() || player.isBot()) {
				players.add(player);
			}
		}
		return players;
	}
	
	@SuppressWarnings("rawtypes")
	public static String getOnlinePlayerList(MessageContext messageContext, boolean full) {
		Player[] onlinePlayers = getOnlinePlayers().toArray(new Player[]{});
		Player[] ignoredPlayers = getIgnoredOnlinePlayers().toArray(new Player[]{});
		
		String response;
		
		if(ignoredPlayers.length == 0) {
			response = "Players Online: (" + onlinePlayers.length + ")\n\n";
		}
		else {
			response = "Players Online: " + (onlinePlayers.length - ignoredPlayers.length) + " (" + onlinePlayers.length + ")" + "\n\n";
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
	
	public static Player[] getKnownPlayers() {
		return Player.getEncounteredPlayers();
	}
	
	private static String parseLine(String s, int line) {
		String[] lines = s.split("\n");
		return lines[line].replace("<td>", "").replaceAll("</td>", "").replaceAll(" ", "");
	}
	
}
