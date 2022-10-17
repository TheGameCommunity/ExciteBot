package com.gamebuster19901.excite.bot.server.emote;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.server.DiscordServer;

import net.dv8tion.jda.api.entities.Guild;

public class Emote {
	
	private static final Logger LOGGER = Logger.getLogger(Emote.class.getName());
	
	private static final ConcurrentHashMap<String, Emote> EMOTES = new ConcurrentHashMap<String, Emote>();
	
	private long discordServer = -1;
	private String name;
	private final net.dv8tion.jda.api.entities.Emote emote;
	
	public Emote(String name) {
		this.emote = getEmote(name).asDiscordEmote();
		EMOTES.put(name, this);
	}
	
	protected Emote(String name, net.dv8tion.jda.api.entities.Emote emote) {
		this.name = name;
		this.emote = emote;
	}
	
	public Emote(String name, long discordServer, net.dv8tion.jda.api.entities.Emote emote) {
		this(name, emote);
		this.discordServer = discordServer;
	}
	
	public static Emote getEmote(String name) {
		Emote emote = EMOTES.get(name);
		if(emote != null && 
				emote.discordServer != -1 
				&& DiscordServer.getServer(ConsoleContext.INSTANCE, emote.discordServer).isLoaded()) {
			return EMOTES.get(name);
		}
		
		String ret = "";
		for(DiscordServer server : DiscordServer.getKnownDiscordServers()) {
			Guild guild = server.getGuild();
			if(guild != null) {
				List<net.dv8tion.jda.api.entities.Emote> emotes = server.getGuild().getEmotesByName(name, false);
				if(emotes.size() > 0) {
					LOGGER.info("Found emote :" + name + ": " + " in " + server);
					return new Emote(name, server.getID(), emotes.get(0));
				}
			}
		}
		LOGGER.log(Level.WARNING, "Unable to find emote :" + name + ":");
		return new Emote(name);
	}
	
	public net.dv8tion.jda.api.entities.Emote asDiscordEmote() {
		return emote;
	}
	
	@Override
	public String toString() {
		try {
			return emote.getAsMention();
		}
		catch(Throwable t) {
			return "?" + name + "?"; 
		}
	}
	
}
