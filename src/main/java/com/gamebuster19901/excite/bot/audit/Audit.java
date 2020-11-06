package com.gamebuster19901.excite.bot.audit;

import java.io.IOError;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.Instant;
import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.database.sql.ResultSet;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.Identified;
import com.gamebuster19901.excite.util.TimeUtils;

import static com.gamebuster19901.excite.bot.database.Table.AUDITS;
import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Comparator.EQUALS;

public class Audit implements Identified{
	
	private final long auditID;
	private final AuditType type;
	protected ResultSet results;
	
	protected Audit(ResultSet result) {
		try {
			this.auditID = result.getLong(AUDIT_ID);
			this.type = getAuditType();
			this.results = result;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected static Audit addAudit(MessageContext context, AuditType type, String description) {
		return addAudit(context, type, description, Instant.now());
	}
	
	@SuppressWarnings("rawtypes")
	protected static Audit addAudit(MessageContext context, AuditType type, String description, Instant dateIssued) {
		try {
			PreparedStatement ps = context.getConnection().prepareStatement("INSERT ? (?, ?, ?, ?, ?) VALUES (?, ?, ?, ?, ?)");
			Table.insertValue(ps, 1, AUDITS);
			Table.insertValue(ps, 2, AUDIT_TYPE);
			Table.insertValue(ps, 3, ISSUER_ID);
			Table.insertValue(ps, 4, ISSUER_NAME);
			Table.insertValue(ps, 5, DESCRIPTION);
			Table.insertValue(ps, 6, DATE_ISSUED);
			Table.insertValue(ps, 7, type);
			Table.insertValue(ps, 8, context.getDiscordAuthor().getID());
			Table.insertValue(ps, 9, context.getDiscordAuthor().getName());
			Table.insertValue(ps, 10, description);
			Table.insertValue(ps, 11, Instant.now());
			return new Audit(ps.executeQuery());
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	protected static Audit createAudit(ResultSet result) {
		Class<? extends Audit> auditType = AuditType.getType(result).getType();
		try {
			Constructor<? extends Audit> constructor = auditType.getDeclaredConstructor(ResultSet.class);
			return constructor.newInstance(result);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}
	
	@Override
	public long getID() {
		return auditID;
	}
	
	public final AuditType getType() {
		return type;
	}
	
	public long getIssuerID() {
		try {
			return results.getLong(ISSUER_ID);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public String getIssuerUsername() {
		try {
			return results.getString(ISSUER_NAME);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public String getDescription() {
		try {
			return results.getString(DESCRIPTION);
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	public Instant getDateIssued() {
		try {
			return TimeUtils.parseInstant(results.getString(DATE_ISSUED));
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Audit getAuditById(MessageContext context, long id) {
		try {
			return createAudit(Table.selectAllFromWhere(context, AUDITS, AUDIT_ID, EQUALS, id));
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public AuditType getAuditType() {
		return AuditType.getType(this);
	}
	
	@SuppressWarnings("rawtypes")
	public DiscordUser getIssuerDiscord(MessageContext context) {
		if(getIssuerID() == -1) {
			return Main.CONSOLE;
		}
		return DiscordUser.getDiscordUserIncludingUnknown(context, getIssuerID());
	}
	
}
