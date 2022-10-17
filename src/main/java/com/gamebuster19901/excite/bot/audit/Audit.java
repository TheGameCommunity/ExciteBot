package com.gamebuster19901.excite.bot.audit;

import java.io.IOError;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.Instant;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Result;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.database.sql.ResultSet;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.Identified;
import com.gamebuster19901.excite.util.TimeUtils;

import static com.gamebuster19901.excite.bot.database.Table.AUDITS;
import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Comparator.EQUALS;

public class Audit implements Identified<Long>{
	
	private final long auditID;
	private final AuditType type;
	protected Row row;
	
	protected Audit(Row result, AuditType type) {
		if(result == null) {
			throw new NullPointerException();
		}
		this.auditID = result.getLong(AUDIT_ID);
		this.type = type;
		this.row = result;
	}
	
	@SuppressWarnings("rawtypes")
	protected static Audit addAudit(MessageContext context, AuditType type, String description) {
		return addAudit(context, type, description, Instant.now());
	}
	
	@SuppressWarnings("rawtypes")
	protected static Audit addAudit(MessageContext executor, MessageContext context, AuditType type, String description) {
		return addAudit(executor, context, type, description, Instant.now());
	}
	
	@SuppressWarnings("rawtypes")
	protected static Audit addAudit(MessageContext context, AuditType type, String description, Instant dateIssued) {
		return addAudit(context, context, type, description, dateIssued);
	}
	
	@Deprecated
	@SuppressWarnings("rawtypes")
	protected static Audit addAudit(MessageContext executor, MessageContext context, AuditType type, String description, Instant dateIssued) {
		PreparedStatement ps;
		try {
			ps = Insertion.insertInto(AUDITS).setColumns(AUDIT_TYPE, ISSUER_ID, ISSUER_NAME, DESCRIPTION, DATE_ISSUED)
				.to(type, context.getAuthor().getID(), context.getAuthor().getName(), description, Instant.now())
				.prepare(executor, true);
			ps.execute();
			ResultSet results = ps.getGeneratedKeys();
			results.next();
			long auditID = results.getLong(GENERATED_KEY);
			Row row = Table.selectAllFromWhere(executor, AUDITS, new Comparison(AUDIT_ID, EQUALS, auditID)).getRow(true);
			return new Audit(row, type);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@Nullable
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Audit createAudit(MessageContext context, Row row) throws SQLException {
		AuditType type = AuditType.getType(row);
		long auditID = row.getLong(AUDIT_ID);
		Class<? extends Audit> auditTypeClazz = AuditType.getType(row).getType();
		try {
			Constructor<? extends Audit> constructor = auditTypeClazz.getDeclaredConstructor(Row.class);
			constructor.setAccessible(true);
			Result result = Table.selectAllFromJoinedUsingWhere(new MessageContext(context.getAuthor()), AUDITS, type.getTable(), AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, auditID));
			result.next();
			if(result.cursorValid()) {
				Row resultRow = result.getRow();
				return constructor.newInstance(resultRow);
			}
			return null;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}
	
	@Override
	public Long getID() {
		return auditID;
	}
	
	public final AuditType getType() {
		return type;
	}
	
	public long getIssuerID() {
		return row.getLong(ISSUER_ID);
	}
	
	public String getIssuerUsername() {
		return row.getString(ISSUER_NAME);
	}
	
	public String getDescription() {
		return row.getString(DESCRIPTION);
	}
	
	public Instant getDateIssued() {
		return TimeUtils.parseInstant(row.getString(DATE_ISSUED));
	}
	
	@Nullable
	@SuppressWarnings("rawtypes")
	public static Audit getAuditById(MessageContext context, long id) {
		try {
			return createAudit(context, Table.selectAllFromWhere(context, AUDITS, new Comparison(AUDIT_ID, EQUALS, id)).getRow(true));
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
