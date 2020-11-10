package com.gamebuster19901.excite.bot.audit.ban;

import java.io.IOError;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import com.gamebuster19901.excite.bot.audit.Audit;
import com.gamebuster19901.excite.bot.audit.AuditType;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.database.sql.ResultSet;
import com.gamebuster19901.excite.util.TimeUtils;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Comparator.*;

public class Ban extends Audit{
	
	Audit parentData;
	
	protected Ban(Row row) {
		super(row, AuditType.BAN);
	}

	protected static transient final int DB_VERSION = 1;
	
	@SuppressWarnings("rawtypes")
	public static Ban addBan(MessageContext context, Banee banee) {
		return addBan(context, banee, "");
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban addBan(MessageContext context, Banee banee, String reason) {
		return addBan(context, banee, reason, TimeUtils.FOREVER);
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban addBan(MessageContext context, Banee banee, Duration banDuration) {
		return addBan(context, banee, "", banDuration);
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban addBan(MessageContext context, Banee banee, String reason, Duration banDuration) {
		return addBan(context, banee, reason, banDuration, TimeUtils.fromNow(banDuration));
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban addBan(MessageContext context, Banee banee, String reason, Duration banDuration, Instant banExpire) {
		return addBan(context, banee, reason, banDuration, banExpire, 0l);
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static Ban addBan(MessageContext context, Banee banee, String reason, Duration banDuration, Instant banExpire, long pardon) {
		Audit parent = Audit.addAudit(context, AuditType.BAN, reason);
		PreparedStatement st;
		try {
			st = Insertion.insertInto(AUDIT_BANS)
			.setColumns(AUDIT_ID, BAN_DURATION, BAN_EXPIRE, BANNED_ID, BANNED_USERNAME, BAN_PARDON)
			.to(parent.getID(), banDuration, banExpire, banee.getID(), banee.getName(), pardon)
			.prepare(context, true);
			
			st.execute();
			
			Ban ret = getBanById(context, parent.getID());
			ret.parentData = parent;
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	public boolean isPardoned() {
		return row.getLong(BAN_PARDON) != 0;
	}
	
	public boolean isActive() {
		return TimeUtils.parseInstant(row.getString(BAN_EXPIRE)).isAfter(Instant.now()) && !isPardoned();
	}
	
	@Deprecated
	@SuppressWarnings("rawtypes")
	public String getBannedUsername(MessageContext context) {
		return row.getString(BANNED_USERNAME);
	}
	
	public long getBannedID() {
		return row.getLong(BANNED_ID);
	}
	
	public Table getTable() {
		if(getBannedID() < 10000000000000000l) {
			return PLAYERS;
		}
		return DISCORD_USERS;
	}
	
	public Instant getBanExpireTime() {
		return Instant.parse(row.getString(BAN_EXPIRE));
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban[] getBansOfID(MessageContext context, long id) {
		if(id == -1 || id == -2) {
			throw new AssertionError();
		}
		try {
			ResultSet results = Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_BANS, AUDIT_ID, new Comparison(BANNED_ID, EQUALS, id));
			ArrayList<Ban> bans = new ArrayList<Ban>();
			while(results.next()) {
				bans.add(new Ban(new Row(results, false)));
			}
			return bans.toArray(new Ban[]{});
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban[] getBansOf(MessageContext context, Banee banee) {
		return getBansOfID(context, banee.getID());
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban getBanById(MessageContext context, long id) throws IllegalArgumentException {
		try {
			return new Ban(new Row(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_BANS, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, id))));
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
}
