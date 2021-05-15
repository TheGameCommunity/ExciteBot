package com.gamebuster19901.excite.bot.database;

import static com.gamebuster19901.excite.bot.database.Table.*;


import java.util.Arrays;
import java.util.HashSet;

public enum Column {

	//MULTIPLE TABLES:
	ALL_COLUMNS("*", Table.values()),
	GENERATED_KEY("GENERATED_KEY", Table.values()), //Used for getting generated key
	
	//AUDITS
	AUDIT_ID("auditID", AUDITS, AUDIT_BANS, AUDIT_COMMANDS, AUDIT_NAME_CHANGES, AUDIT_PARDONS, AUDIT_PROFILE_DISCOVERIES, AUDIT_RANK_CHANGES, AUDIT_PROFILE_LOGINS, AUDIT_PROFILE_LOGOUTS),
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
	
	//PARDONS
	PARDONED_AUDIT_ID("pardonedAuditID", AUDIT_PARDONS),
	
	//NAME CHANGES
	OLD_PLAYER_NAME("oldName", AUDIT_NAME_CHANGES),
	NEW_PLAYER_NAME("newName", AUDIT_NAME_CHANGES),
	
	//RANK CHANGES
	PROMOTEE("promotee", AUDIT_RANK_CHANGES),
	PROMOTEE_ID("promoteeDiscordID", AUDIT_RANK_CHANGES),
	
	//COMMANDS
	CHANNEL_NAME("channelName", AUDIT_COMMANDS),
	CHANNEL_ID("channelID", AUDIT_COMMANDS),
	MESSAGE_ID("messageID", AUDIT_COMMANDS),
	IS_GUILD_MESSAGE("isGuildMessage", AUDIT_COMMANDS),
	IS_PRIVATE_MESSAGE("isPrivateMessage", AUDIT_COMMANDS),
	IS_CONSOLE_MESSAGE("isConsoleMessage", AUDIT_COMMANDS),
	IS_ADMIN("isAdmin", AUDIT_COMMANDS),
	IS_OPERATOR("isOperator", AUDIT_COMMANDS),
	
	//DISCOVERY
	DISCOVERED_ID("discoveredID", AUDIT_PROFILE_DISCOVERIES),
	
	//ADMINS
	
	//DISCORD_SERVER
	SERVER_ID("serverID", DISCORD_SERVERS, AUDIT_COMMANDS),
	SERVER_NAME("serverName", DISCORD_SERVERS, AUDIT_COMMANDS),
	SERVER_PREFIX("prefix", DISCORD_SERVERS),
	
	//DISCORD_USER
	DISCORD_ID("discordID", ADMINS, DISCORD_USERS, OPERATORS, PLAYERS, WIIS),
	DISCORD_NAME("discord_name", DISCORD_USERS),
	THRESHOLD("threshold", DISCORD_USERS),
	FREQUENCY("frequency", DISCORD_USERS),
	LAST_NOTIFICATION("lastNotification", DISCORD_USERS),
	BELOW_THRESHOLD("dippedBelowThreshold", DISCORD_USERS),
	NOTIFY_CONTINUOUSLY("notifyContinuously", DISCORD_USERS),
	DETAILED_PM("detailedPM", DISCORD_USERS),
	STARS("stars", DISCORD_USERS),
	
	// OPERATOR
	
	//PLAYER
	PLAYER_ID("playerID", PLAYERS, AUDIT_NAME_CHANGES, AUDIT_PROFILE_DISCOVERIES),
	FRIEND_CODE("friendCode", PLAYERS, AUDIT_NAME_CHANGES),
	PLAYER_NAME("name", PLAYERS),
	REDACTED("redacted", PLAYERS),
	LAST_ONLINE("lastOnline", PLAYERS),
	SECONDS_PLAYED("secondsPlayed", PLAYERS),
	
	//WIIS
	WII_ID("wiiID", WIIS);
	
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
		throw new IllegalArgumentException("No column with name " + dbName);
	}
}
