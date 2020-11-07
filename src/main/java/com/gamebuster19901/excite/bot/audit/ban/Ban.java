package com.gamebuster19901.excite.bot.audit.ban;

import java.io.IOError;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import com.gamebuster19901.excite.bot.audit.Audit;
import com.gamebuster19901.excite.bot.audit.AuditType;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.database.sql.ResultSet;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.TimeUtils;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Comparator.*;

public class Ban extends Audit{
	
	Audit parentData;
	
	protected Ban(ResultSet result) {
		super(result);
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
		return addBan(context, banee, reason, banDuration, Instant.now().plus(banDuration));
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban addBan(MessageContext context, Banee banee, String reason, Duration banDuration, Instant banExpire) {
		return addBan(context, banee, reason, banDuration, banExpire, -1l);
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static Ban addBan(MessageContext context, Banee banee, String reason, Duration banDuration, Instant banExpire, long pardon) {
		Audit parent = Audit.addAudit(context, AuditType.BAN, reason);
		PreparedStatement st;
		try {
			st = Insertion.insertInto(AUDIT_BANS)
			.setColumns(AUDIT_ID, BAN_DURATION, BAN_EXPIRE, BANNED_ID, BANNED_USERNAME, BAN_PARDON)
			.to(parent.getID(), banDuration, banExpire, banee.getID(), banee.getName(), pardon)
			.prepare(context);
			
			Ban ret = new Ban(st.executeQuery());
			ret.parentData = parent;
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	public boolean isPardoned() {
		try {
			return results.getLong(BAN_PARDON) != 0;
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public boolean isActive() {
		try {
			return TimeUtils.parseInstant(results.getString(BAN_EXPIRE)).isAfter(Instant.now()) && !isPardoned();
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@Deprecated
	@SuppressWarnings("rawtypes")
	public String getBannedUsername(MessageContext context) {
		try {
			return results.getString(BANNED_USERNAME);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public long getBannedID() {
		try {
			return results.getLong(BANNED_ID);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public Table getTable() {
		if(getBannedID() < 10000000000000000l) {
			return PLAYERS;
		}
		return DISCORD_USERS;
	}
	
	public Instant getBanExpireTime() {
		try {
			return Instant.parse(results.getString(BAN_EXPIRE));
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban[] getBansOfUser(MessageContext context, DiscordUser user) {
		try {
			ResultSet results = Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_BANS, AUDIT_ID, BANNED_ID, EQUALS, user.getID());
			Ban[] bans = new Ban[results.getMetaData().getColumnCount()];
			for(int i = 0; i < bans.length; i++) {
				results.next();
				bans[i] = new Ban(results);
			}
			return bans;
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban[] getBansOfUser(MessageContext context, long id) {
		if(id == -1 || id == -2) {
			throw new AssertionError();
		}
		return getBansOfUser(context, DiscordUser.getDiscordUserIncludingUnknown(context, id));
	}
	
	@SuppressWarnings("rawtypes")
	public static Ban getBanById(MessageContext context, long id) throws IllegalArgumentException {
		return new Ban(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_BANS, AUDIT_ID, AUDIT_ID, EQUALS, id));
	}
	
}
