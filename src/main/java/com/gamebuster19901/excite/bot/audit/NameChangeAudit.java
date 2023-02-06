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

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Comparator.*;

public class NameChangeAudit extends Audit {

	Audit parentData;
			
	protected NameChangeAudit(Row row) {
		super(row, AuditType.NAME_CHANGE_AUDIT);
	}
	
	@SuppressWarnings("rawtypes")
	public static NameChangeAudit addNameChange(CommandContext context, Player player, String newName) {
		Audit parent = Audit.addAudit(ConsoleContext.INSTANCE, context,  AuditType.NAME_CHANGE_AUDIT, getMessage(context, newName));
		String name = player.getName();
		PreparedStatement st;
		try {
			st = Insertion.insertInto(Table.AUDIT_NAME_CHANGES)
			.setColumns(AUDIT_ID, OLD_PLAYER_NAME, NEW_PLAYER_NAME, PLAYER_ID, FRIEND_CODE)
			.to(parent.getID(), name, newName, player.getID(), player.getFriendCode())
			.prepare(ConsoleContext.INSTANCE, true);
			
			st.execute();
			
			NameChangeAudit ret = getNameChangeByAuditID(ConsoleContext.INSTANCE, parent.getID());
			ret.parentData = parent;
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static NameChangeAudit getNameChangeByAuditID(CommandContext context, long auditID) {
		return new NameChangeAudit(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_NAME_CHANGES, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, auditID)).getRow(true));
	}
	
	@SuppressWarnings("rawtypes")
	private static String getMessage(CommandContext context, String newName) {
		return context.getPlayerAuthor().getName() + "(" + context.getPlayerAuthor().getID() + ") changed their name to " + newName; 
	}
	
}
