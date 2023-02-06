package com.gamebuster19901.excite.bot.audit;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;


import java.io.IOError;
import java.sql.SQLException;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Comparator.*;

public class LogInAudit extends Audit {

	Audit parentData;

	protected LogInAudit(Row result) {
		super(result, AuditType.LOG_IN_AUDIT);
	}

	public static LogInAudit addLoginAudit(CommandContext context, Player player) {
		Audit parent = Audit.addAudit(context, AuditType.LOG_IN_AUDIT, getMessage(player));
		
		PreparedStatement st;
		
		try {
			st = Insertion.insertInto(Table.AUDIT_PROFILE_LOGINS)
			.setColumns(AUDIT_ID)
			.to(parent.getID())
			.prepare(context, true);
			
			st.execute();
			
			LogInAudit ret = getLoginAuditByAuditID(ConsoleContext.INSTANCE, parent.getID());
			ret.parentData = parent;
			
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
		
	}

	private static LogInAudit getLoginAuditByAuditID(ConsoleContext context, long auditID) {
		return new LogInAudit(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_PROFILE_LOGINS, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, auditID)).getRow(true));
	}

	private static String getMessage(Player player) {
		return player.getIdentifierName() + " logged in";
	}
	
}
