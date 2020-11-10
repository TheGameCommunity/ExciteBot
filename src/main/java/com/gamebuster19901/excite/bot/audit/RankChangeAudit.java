package com.gamebuster19901.excite.bot.audit;

import java.io.IOError;
import java.sql.SQLException;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Comparator.*;

public class RankChangeAudit extends Audit {
	
	Audit parentData;
	
	protected RankChangeAudit(Row row) {
		super(row, AuditType.RANK_CHANGE_AUDIT);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static RankChangeAudit addRankChange(MessageContext context, DiscordUser promotee, String rank, boolean added) {
		Audit parent = Audit.addAudit(ConsoleContext.INSTANCE, context, AuditType.RANK_CHANGE_AUDIT, getMessage(context, new MessageContext(promotee), rank, added));
		
		PreparedStatement st;
		try {
			st = Insertion.insertInto(AUDIT_RANK_CHANGES)
			.setColumns(AUDIT_ID, PROMOTEE, PROMOTEE_ID)
			.to(parent.getID(), promotee, promotee.getID())
			.prepare(ConsoleContext.INSTANCE, true);
			
			st.execute();
			
			RankChangeAudit ret = getRankChangeAuditByID(ConsoleContext.INSTANCE, parent.getID());
			ret.parentData = parent;
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static RankChangeAudit getRankChangeAuditByID(MessageContext context, long auditID) {
		try {
			return new RankChangeAudit(new Row(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_RANK_CHANGES, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, auditID))));
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static final String getMessage(MessageContext promoter, MessageContext<DiscordUser> promotee, String rank, boolean added) {
		if(added) {
			return promoter.getDiscordAuthor().toDetailedString() + " made " + promotee.getDiscordAuthor().toDetailedString() + " a bot " + rank + " for " + Main.discordBot.getSelfUser().getName();
		}
		return promoter.getDiscordAuthor().toDetailedString() + " removed the bot " + rank + " rights from " + promotee.getDiscordAuthor().toDetailedString() + " for " + Main.discordBot.getSelfUser().getName();
	}
	
}