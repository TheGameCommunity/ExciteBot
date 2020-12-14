package com.gamebuster19901.excite.bot.audit;

import java.io.IOError;
import java.sql.SQLException;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Comparator.*;

public class DiscoveryAudit extends Audit {
	
	Audit parentData;
	
	public DiscoveryAudit(Row row) {
		super(row, AuditType.DISCOVERY_AUDIT);
	}
	
	@SuppressWarnings("rawtypes")
	public static DiscoveryAudit addProfileDiscovery(MessageContext context) {
		Audit parent = Audit.addAudit(context,  AuditType.DISCOVERY_AUDIT, getMessage(context));
		
		PreparedStatement st;
		
		try {
			st = Insertion.insertInto(Table.AUDIT_PROFILE_DISCOVERIES)
			.setColumns(AUDIT_ID, PLAYER_ID)
			.to(parent.getID(), context.getSenderId())
			.prepare(context, true);
			
			st.execute();
			
			DiscoveryAudit ret = getProfileDiscoveryByDiscoveredID(ConsoleContext.INSTANCE, context.getSenderId());
			ret.parentData = parent;
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static DiscoveryAudit getProfileDiscoveryByDiscoveredID(MessageContext context, long playerID) {
		return new DiscoveryAudit(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_PROFILE_DISCOVERIES, AUDIT_ID, new Comparison(PLAYER_ID, EQUALS, playerID)).getRow(true));
	}
	
	@SuppressWarnings("rawtypes")
	public static DiscoveryAudit getProfileDiscoveryByAuditID(MessageContext context, long auditID) {
		return new DiscoveryAudit(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_PROFILE_DISCOVERIES, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, auditID)).getRow(true));
	}
	
	@SuppressWarnings("rawtypes")
	private static String getMessage(MessageContext context) {
		return context.getAuthor().getIdentifierName();
	}
	
}