package com.gamebuster19901.excite.bot.server.emote;

import java.util.HashMap;
import com.gamebuster19901.excite.bot.server.DiscordServer;

public class Emote {
	
	private HashMap<String, String> emotes = new HashMap<String, String>();
	
	public String getEmote(String name) {
		if(emotes.containsKey(name)) {
			return emotes.get(name);
		}
		
		String ret = "";
		for(DiscordServer server : DiscordServer.getLoadedDiscordServers()) {
			for(net.dv8tion.jda.api.entities.Emote emote : server.getGuild().getEmotesByName(name, false)) {
				ret += emote.getAsMention();
			}
		}
		if(ret.isEmpty()) {
			ret += "?" + name + "?";
		}
		else {
			emotes.put(name, ret);
		}
		return ret;
	}
	
}
