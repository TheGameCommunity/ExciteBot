package com.gamebuster19901.excite.game;

import org.apache.commons.text.WordUtils;

public enum Bot {
	FROG,
	BEETLE,
	LADYBUG,
	BAT,
	TURTLE,
	GRASSHOPPER,
	MOUSE,
	MANTIS,
	HUMMINGBIRD,
	CENTIPEDE,
	SPIDER,
	CRAB,
	SCORPION,
	HORNET,
	LOBSTER,
	ANT,
	ROACH,
	LIZARD,
	SQUID,
	BOULDER,
	FLEA,
	CRICKET;
	
	public String toString() {
		return WordUtils.capitalizeFully(name());
	}
	
	public static Bot fromString(String botName) {
		for(Bot bot : values()) {
			if(botName.equalsIgnoreCase(bot.name())) {
				return bot;
			}
		}
		throw new IllegalArgumentException("Unknown bot " + botName);
	}
	
}
