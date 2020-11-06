package com.gamebuster19901.excite.bot.audit;

import com.gamebuster19901.excite.bot.audit.ban.*;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.ResultSet;

import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Column.*;

import java.io.IOError;
import java.sql.SQLException;

public enum AuditType {

	BAN(Ban.class, AUDIT_BANS),
	PARDON(Pardon.class, AUDIT_PARDONS),
	
	COMMAND_AUDIT(CommandAudit.class, AUDIT_COMMANDS),
	NAME_CHANGE_AUDIT(NameChangeAudit.class, AUDIT_NAME_CHANGES),
	PROFILE_DISCOVERY_AUDIT(ProfileDiscoveryAudit.class, AUDIT_PROFILE_DISCOVERIES),
	RANK_CHANGE_AUDIT(RankChangeAudit.class, AUDIT_RANK_CHANGES)
	
	;

	private Class<? extends Audit> type;
	private Table table;
	
	AuditType(Class<? extends Audit> type, Table table) {
		this.type = type;
	}
	
	public Class<? extends Audit> getType() {
		return type;
	}
	
	public Table getTable;
	
	public static AuditType getType(Audit audit) {
		for(AuditType type : values()) {
			if(audit.getClass() == type.type) {
				return type;
			}
		}
		throw new AssertionError("Could not find audit type " + audit.getClass().getSimpleName());
	}
	
	public static AuditType getType(ResultSet results) {
		try {
			String type = results.getString(AUDIT_TYPE);
			for(AuditType audit : values()) {
				if(audit.getClass().getSimpleName().equals(type)) {
					return audit;
				}
			}
			throw new AssertionError("Could not find audit type " + type);
		} 
		catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
}
