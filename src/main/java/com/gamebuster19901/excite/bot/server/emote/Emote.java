package com.gamebuster19901.excite.bot.server.emote;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gamebuster19901.excite.bot.server.DiscordServer;
import com.gamebuster19901.excite.bot.server.UnloadedDiscordServer;

public class Emote {
	
	private static final Logger LOGGER = Logger.getLogger(Emote.class.getName());
	
	private static final HashMap<String, Emote> EMOTES = new HashMap<String, Emote>();
	
	private long discordServer = -1;
	private String name;
	private String format;
	
	public Emote(String name) {
		this(name, "?" + name + "?");
		EMOTES.put(name, this);
	}
	
	protected Emote(String name, String format) {
		this.name = name;
		this.format = format;
	}
	
	public Emote(String name, long discordServer, String format) {
		this(name, format);
		this.discordServer = discordServer;
	}
	
	public static Emote getEmote(String name) {
		Emote emote = EMOTES.get(name);
		if(emote != null && 
				emote.discordServer != -1 
				&& ! (DiscordServer.getServer(emote.discordServer) instanceof UnloadedDiscordServer)) {
			return EMOTES.get(name);
		}
		
		String ret = "";
		for(DiscordServer server : DiscordServer.getLoadedDiscordServers()) {
			List<net.dv8tion.jda.api.entities.Emote> emotes = server.getGuild().getEmotesByName(name, false);
			if(emotes.size() > 0) {
				LOGGER.info("Found emote :" + name + ": " + " in " + server);
				return new Emote(name, server.getId(), emotes.get(0).getAsMention());
			}
		}
		LOGGER.log(Level.WARNING, "Unable to find emote :" + name + ":");
		return new Emote(name);
	}
	
	@Override
	public String toString() {
		return format;
	}
	
}
