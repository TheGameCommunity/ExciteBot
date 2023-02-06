package com.gamebuster19901.excite.bot.audit;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Comparator.EQUALS;
import static com.gamebuster19901.excite.bot.database.Table.AUDITS;

import java.io.File;
import java.io.IOError;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.mail.AddFriendResponse;
import com.gamebuster19901.excite.bot.mail.ElectronicAddress;
import com.gamebuster19901.excite.bot.mail.EmailAddress;
import com.gamebuster19901.excite.bot.mail.MailReplyResponse;
import com.gamebuster19901.excite.bot.mail.Mailbox;
import com.gamebuster19901.excite.bot.mail.RefundResponse;

public class MailAudit extends Audit {

	Audit parentData;
	
	protected MailAudit(Row result) {
		super(result, AuditType.MAIL_AUDIT);
	}

	@SuppressWarnings("rawtypes")
	public static MailAudit addMailAudit(CommandContext context, MimeMessage message, boolean incoming, File file) throws AddressException, MessagingException {
		ElectronicAddress from = new EmailAddress(message.getFrom()[0]);
		ElectronicAddress to = new EmailAddress(new InternetAddress(message.getHeader("To")[0]));
		String description = from.getEmail() + " sent mail to " + to.getEmail();
		String[] appIDHeader = message.getHeader(Mailbox.APP_ID_HEADER);
		String mailType = "GENERIC";
		if(appIDHeader != null) {
			switch(appIDHeader[0]) {
				case Mailbox.FRIEND_REQUEST:
					description = from + " sent a friend request to " + to;
					mailType = "FRIEND_REQUEST";
					break;
				case Mailbox.EXCITEBOTS:
					description = from + " sent a challenge to " + to;
					mailType = "CHALLENGE";
					break;
			}
		}
		
		Audit parent = Audit.addAudit(context, AuditType.MAIL_AUDIT, description);
		
		PreparedStatement st;
		
		try {
			st = Insertion.insertInto(Table.MAIL)
			.setColumns(AUDIT_ID, SENDER, RECIPIENT, INCOMING, MAIL_TYPE, FILE)
			.to(parent.getID(), from.getEmail(), to.getEmail(), incoming, mailType, file.getAbsolutePath())
			.prepare(context, true);
			
			st.execute();
			
			MailAudit ret = getMailAuditByAuditID(ConsoleContext.INSTANCE, parent.getID());
			ret.parentData = parent;
			
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public static MailAudit addMailAudit(CommandContext context, MailReplyResponse message, boolean incoming, File file) {
		ElectronicAddress from = message.getResponder();
		ElectronicAddress to = message.getRespondee();
		String description = from.getEmail() + " sent mail to " + to.getEmail();
		String mailType = "GENERIC";
		if(message instanceof AddFriendResponse) {
			description = from.getEmail() + " sent a friend request to " + to.getEmail();
			mailType = "FRIEND_REQUEST";
		}
		else if (message instanceof RefundResponse) {
			description = "refunded " + ((RefundResponse)message).getReward() + " to " + to.getEmail();
			mailType = "REFUND";
		}
		
		Audit parent = Audit.addAudit(context, AuditType.MAIL_AUDIT, description);
		
		PreparedStatement st;
		
		try {
			st = Insertion.insertInto(Table.MAIL)
			.setColumns(AUDIT_ID, SENDER, RECIPIENT, INCOMING, MAIL_TYPE, FILE)
			.to(parent.getID(), from.getEmail(), to.getEmail(), incoming, mailType, file.getAbsolutePath())
			.prepare(context, true);
			
			st.execute();
			
			MailAudit ret = getMailAuditByAuditID(ConsoleContext.INSTANCE, parent.getID());
			ret.parentData = parent;
			
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
	}
	
	private static MailAudit getMailAuditByAuditID(ConsoleContext context, long auditID) {
		return new MailAudit(Table.selectAllFromJoinedUsingWhere(context, AUDITS, Table.MAIL, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, auditID)).getRow(true));
	}
	
}
