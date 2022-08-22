package com.gamebuster19901.excite.bot.audit.ban;

import java.io.IOError;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.bot.audit.Audit;
import com.gamebuster19901.excite.bot.audit.AuditType;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Result;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.util.TimeUtils;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Comparator.*;

public class Ban extends Audit{
	
	Audit parentData;
	
	protected Ban(Row row) {
		super(row, AuditType.BAN);
	}
	
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
			.setColumns(AUDIT_ID, BAN_DURATION, BAN_EXPIRE, BANNED_ID, BANNED_USERNAME)
			.to(parent.getID(), banDuration, banExpire, banee.getID(), banee.getName())
			.prepare(context, true);
			
			st.execute();
			
			Ban ret = getBanByAuditId(context, parent.getID());
			ret.parentData = parent;
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	public boolean isPardoned() {
		return getPardonID() != 0;
	}
	
	public long getPardonID() {
		try {
			Result result = Table.selectAllFromWhere(ConsoleContext.INSTANCE, AUDIT_PARDONS, new Comparison(PARDONED_AUDIT_ID, EQUALS, getID()));
			if(result.next()) {
				return result.getLong(PARDONED_AUDIT_ID);
			}
			return 0;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	public boolean isActive() {
		return TimeUtils.parseInstant(row.getString(BAN_EXPIRE)).isAfter(Instant.now()) && !isPardoned();
	}
	
	@Deprecated
	@SuppressWarnings("rawtypes")
	public String getBannedUsername() {
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
			throw new AssertionError("Attempted to get bans of a fake banee");
		}
		Result results = Table.selectAllFromJoinedUsingWhere(new MessageContext(context.getAuthor()), AUDITS, AUDIT_BANS, AUDIT_ID, new Comparison(BANNED_ID, EQUALS, id));
		ArrayList<Ban> bans = new ArrayList<Ban>();
		while(results.next()) {
			bans.add(new Ban(results.getRow()));
		}
		return bans.toArray(new Ban[]{});
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban[] getBansOf(MessageContext context, Banee banee) {
		return getBansOfID(context, banee.getID());
	}
	
	@Nullable
	@SuppressWarnings("rawtypes")
	public static Ban getBanByAuditId(MessageContext context, long id) throws IllegalArgumentException {
		Result results = Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_BANS, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, id));
		if(results.hasNext()) {
			results.next();
			return new Ban(results.getRow());
		}
		return null;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User: ").append(getBannedUsername()).append('\n')
		.append("User ID: " ).append(getBannedID()).append('\n')
		.append("Ban ID: " ).append(this.getID()).append('\n')
		.append("Reason: ").append(this.getDescription()).append('\n');
		return builder.toString();
	}
	
}
