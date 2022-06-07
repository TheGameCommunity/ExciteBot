package com.gamebuster19901.excite.bot.audit;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.*;

import java.io.IOError;
import java.sql.SQLException;

import static com.gamebuster19901.excite.bot.database.Comparator.*;

public class CommandAudit extends Audit {

	Audit parentData;
	
	protected CommandAudit(Row row) {
		super(row, AuditType.COMMAND_AUDIT);
	}
	
	@SuppressWarnings("rawtypes")
	public static CommandAudit addCommandAudit(MessageContext context, String command) {
		Audit parent = Audit.addAudit(ConsoleContext.INSTANCE, context, AuditType.COMMAND_AUDIT, command);
		
		PreparedStatement st;
		try {
			
			String serverName = null;
			long serverID = 0;
			String channelName = null;
			long channelID = 0;
			long messageID = 0;
			boolean isGuildMessage = context.isGuildMessage();
			boolean isPrivateMessage = context.isPrivateMessage();
			boolean isConsoleMessage = context.isConsoleMessage();
			boolean isAdmin = context.isAdmin();
			boolean isOperator = context.isOperator();
			
			if(isPrivateMessage || isConsoleMessage) {
				channelName = context.getDiscordAuthor().getName();
				channelID = context.getSenderId();
			}
			else {
				serverName = context.getServer().getName();
				serverID = context.getServer().getID();
				channelName = context.getChannel().getName();
				channelID = context.getChannel().getIdLong();
				messageID = context.getMessage().getIdLong();
			}
			
			st = Insertion.insertInto(AUDIT_COMMANDS)
			.setColumns(AUDIT_ID, SERVER_NAME, SERVER_ID, CHANNEL_NAME, CHANNEL_ID, MESSAGE_ID, IS_GUILD_MESSAGE, IS_PRIVATE_MESSAGE, IS_CONSOLE_MESSAGE, IS_ADMIN, IS_OPERATOR)
			.to(parent.getID(), serverName, serverID, channelName, channelID, messageID, isGuildMessage, isPrivateMessage, isConsoleMessage, isAdmin, isOperator)
			.prepare(ConsoleContext.INSTANCE, true);
			
			st.execute();
			
			CommandAudit ret = getCommandAuditByID(ConsoleContext.INSTANCE, parent.getID());
			ret.parentData = parent;
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static CommandAudit getCommandAuditByID(MessageContext context, long auditID) {
		return new CommandAudit(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_COMMANDS, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, auditID)).getRow(true));
	}
	
}
