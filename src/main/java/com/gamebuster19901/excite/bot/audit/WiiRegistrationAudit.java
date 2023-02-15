package com.gamebuster19901.excite.bot.audit;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.CommandContext;
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
	
	public static WiiRegistrationAudit addWiiRegistrationAudit(CommandContext<?> registrant, Wii wii, boolean unregister) {
		Audit parent;
		String user = DiscordUser.toDetailedString(registrant);
		if(!unregister) {
			parent = Audit.addAudit(registrant, AuditType.WII_REGISTRATION_AUDIT, user + " registered " + wii.getWiiCode().hyphenate());
		}
		else {
			parent = Audit.addAudit(registrant, AuditType.WII_REGISTRATION_AUDIT, user + " unregistered " + wii.getWiiCode().hyphenate());
		}
		
		PreparedStatement st;
		
		try {
			st = Insertion.insertInto(Table.AUDIT_WII_REGISTER)
			.setColumns(AUDIT_ID, WII_ID, DISCORD_ID, UNREGISTER)
			.to(parent.getID(), wii.getWiiCode().toString(), registrant.getAuthor().getIdLong(), unregister)
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
