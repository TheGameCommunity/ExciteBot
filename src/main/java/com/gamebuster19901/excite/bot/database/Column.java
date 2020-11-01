package com.gamebuster19901.excite.bot.database;

import static com.gamebuster19901.excite.bot.database.Table.*;


import java.util.Arrays;
import java.util.HashSet;

public enum Column {

	//MULTIPLE TABLES:
	ALL_COLUMNS("*", Table.values()),
	DISCORD_ID("DiscordID", ADMINS, DISCORD_USERS, OPERATORS, PLAYERS),
	
	//ADMINS
	
	//DISCORD_SERVER
	SERVER_ID("server_id", DISCORD_SERVERS),
	SERVER_NAME("name", DISCORD_SERVERS),
	SERVER_PREFIX("prefix", DISCORD_SERVERS),
	
	//DISCORD_USER
	DISCORD_NAME("discord_name", DISCORD_USERS),
	THRESHOLD("threshold", DISCORD_USERS),
	FREQUENCY("frequency", DISCORD_USERS),
	LAST_NOTIFICATION("lastNotification", DISCORD_USERS),
	BELOW_THRESHOLD("dippedBelowThreshold", DISCORD_USERS),
	NOTIFY_CONTINUOUSLY("notifyContinuously", DISCORD_USERS),
	
	// OPERATOR
	
	//PLAYER
	PLAYER_ID("playerID", PLAYERS),
	FRIEND_CODE("friendCode", PLAYERS),
	PLAYER_NAME("name", PLAYERS),
	REDACTED("redacted", PLAYERS),
	LAST_ONLINE("lastOnline", PLAYERS);
	
	;
	
	private final String name;
	private final HashSet<Table> validTables = new HashSet<Table>();
	
	private Column(String name, Table...tables ) {
		if(tables == null || tables.length == 0) {
			throw new VerifyError("Column " + name + " has no tables!");
		}
		this.name = name;
		validTables.addAll(Arrays.asList(tables));
	}
	
	public String toString() {
		return name;
	}
	
	public boolean isInTable(Table table) {
		return validTables.contains(table);
	}
}
