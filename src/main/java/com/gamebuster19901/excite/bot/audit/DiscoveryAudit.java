package com.gamebuster19901.excite.bot.audit;

import java.io.IOError;
import java.sql.SQLException;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Comparator.*;

public class DiscoveryAudit extends Audit {
	
	Audit parentData;
	
	public DiscoveryAudit(Row row) {
		super(row, AuditType.DISCOVERY_AUDIT);
	}
	
	@SuppressWarnings("rawtypes")
	public static DiscoveryAudit addProfileDiscovery(CommandContext context, boolean automatic, Player player) {
		Audit parent = Audit.addAudit(context,  AuditType.DISCOVERY_AUDIT, getMessage(context, automatic, player));
		
		PreparedStatement st;
		
		try {
			st = Insertion.insertInto(Table.AUDIT_PROFILE_DISCOVERIES)
			.setColumns(AUDIT_ID, PLAYER_ID)
			.to(parent.getID(), player.getID())
			.prepare(context, true);
			
			st.execute();
			
			DiscoveryAudit ret = getProfileDiscoveryByDiscoveredID(ConsoleContext.INSTANCE, player.getID());
			ret.parentData = parent;
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static DiscoveryAudit getProfileDiscoveryByDiscoveredID(CommandContext context, long playerID) {
		return new DiscoveryAudit(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_PROFILE_DISCOVERIES, AUDIT_ID, new Comparison(PLAYER_ID, EQUALS, playerID)).getRow(true));
	}
	
	@SuppressWarnings("rawtypes")
	public static DiscoveryAudit getProfileDiscoveryByAuditID(CommandContext context, long auditID) {
		return new DiscoveryAudit(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_PROFILE_DISCOVERIES, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, auditID)).getRow(true));
	}
	
	@SuppressWarnings("rawtypes")
	private static String getMessage(CommandContext context, boolean automatic, Player player) {
		if(context.isConsoleMessage() && automatic) {
			return player.getIdentifierName();
		}
		return DiscordUser.toDetailedString(context) + " added " + player.getIdentifierName();
	}
	
}