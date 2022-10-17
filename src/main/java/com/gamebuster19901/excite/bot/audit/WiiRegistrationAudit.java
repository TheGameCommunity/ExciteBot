package com.gamebuster19901.excite.bot.audit;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.Wii;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Comparator.*;
import static com.gamebuster19901.excite.bot.database.Table.*;

import java.io.IOError;
import java.sql.SQLException;

public class WiiRegistrationAudit extends Audit {

	Audit parentData;
	
	protected WiiRegistrationAudit(Row result) {
		super(result, AuditType.WII_REGISTRATION_AUDIT);
	}
	
	public static WiiRegistrationAudit addWiiRegistrationAudit(MessageContext registrant, DiscordUser registree,  Wii wii, boolean unregister) {
		Audit parent;
		if(!unregister) {
			parent = Audit.addAudit(registrant, AuditType.WII_REGISTRATION_AUDIT, registrant.getAuthor().getIdentifierName() + " registered " + wii.getWiiCode().hyphenate() + " to " + registree);
		}
		else {
			parent = Audit.addAudit(registrant, AuditType.WII_REGISTRATION_AUDIT, registrant.getAuthor().getIdentifierName() + " unregistered " + wii.getWiiCode().hyphenate() + " from " + registree);
		}
		
		PreparedStatement st;
		
		try {
			st = Insertion.insertInto(Table.AUDIT_WII_REGISTER)
			.setColumns(AUDIT_ID, WII_ID, DISCORD_ID, UNREGISTER)
			.to(parent.getID(), wii.getWiiCode().toString(), registree.getID(), unregister)
			.prepare(true);
			
			st.execute();
			
			WiiRegistrationAudit ret = getWiiRegistrationAuditByID(ConsoleContext.INSTANCE, parent.getID());
			ret.parentData = parent;
			
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	private static WiiRegistrationAudit getWiiRegistrationAuditByID(ConsoleContext context, long auditID) {
		return new WiiRegistrationAudit(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_WII_REGISTER, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, auditID)).getRow(true));
	}

}
