package com.gamebuster19901.excite.bot.server.emote;

import com.gamebuster19901.excite.Main;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public class Emote {

	
	public static String getEmoji(String name) {
		String ret = "";
		for(Emoji emoji : Main.discordBot.jda.getEmojisByName(name, false)) {
			String s = emoji.getFormatted();
			return s;
		}
		return ret;
	}
	
}