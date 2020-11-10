package com.gamebuster19901.excite.bot.database;

import static com.gamebuster19901.excite.bot.database.Table.*;


import java.util.Arrays;
import java.util.HashSet;

public enum Column {

	//MULTIPLE TABLES:
	ALL_COLUMNS("*", Table.values()),
	GENERATED_KEY("GENERATED_KEY", Table.values()), //Used for getting generated key
	DISCORD_ID("DiscordID", ADMINS, DISCORD_USERS, OPERATORS, PLAYERS),
	PLAYER_ID("playerID", PLAYERS, AUDIT_NAME_CHANGES),
	FRIEND_CODE("friendCode", PLAYERS, AUDIT_NAME_CHANGES),
	
	//AUDITS
	AUDIT_ID("auditID", AUDITS, AUDIT_BANS, AUDIT_COMMANDS, AUDIT_NAME_CHANGES, AUDIT_PARDONS, AUDIT_PROFILE_DISCOVERIES, AUDIT_RANK_CHANGES),
	AUDIT_TYPE("type", AUDITS),
	ISSUER_ID("issuer", AUDITS),
	ISSUER_NAME("issuerName", AUDITS),
	DESCRIPTION("description", AUDITS),
	DATE_ISSUED("issued", AUDITS),
	
	//BANS
	BAN_DURATION("duration", AUDIT_BANS),
	BAN_EXPIRE("expireTime", AUDIT_BANS),
	BANNED_ID("bannedID", AUDIT_BANS),
	@Deprecated BANNED_USERNAME("bannedUsername", AUDIT_BANS),
	BAN_PARDON("pardon", AUDIT_BANS),
	
	//NAME CHANGES
	OLD_PLAYER_NAME("oldName", AUDIT_NAME_CHANGES),
	NEW_PLAYER_NAME("newName", AUDIT_NAME_CHANGES),
	
	//RANK CHANGES
	PROMOTEE("promotee", AUDIT_RANK_CHANGES),
	PROMOTEE_ID("promoteeDiscordID", AUDIT_RANK_CHANGES),
	
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
	
	public static Column getColumn(String dbName) {
		for(Column column : values()) {
			if(column.name.equals(dbName)) {
				return column;
			}
		}
		throw new IllegalArgumentException("No column with db name " + dbName);
	}
}
