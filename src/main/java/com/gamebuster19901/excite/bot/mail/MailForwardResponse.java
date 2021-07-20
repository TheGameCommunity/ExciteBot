package com.gamebuster19901.excite.bot.mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.user.Wii;

public class MailForwardResponse extends MailReplyResponse {

	private final EmailAddress originalFrom;
	private final EmailAddress originalTo;
	private final MimeMessage originalMessage;
	private final MimeMessage forwardMessage;
	
	public MailForwardResponse(MimeMessage message, EmailAddress forwardTo) throws MessagingException {
		super(Wii.getWii(((EmailAddress)message.getAllRecipients()[0]).getNamePart()), forwardTo, message);
		originalMessage = message;
		forwardMessage = new MimeMessage(originalMessage);
		originalFrom = (EmailAddress)message.getFrom()[0];
		originalTo = (EmailAddress)message.getAllRecipients()[0];
	}
	
	@Override
	public String getResponse() throws MessagingException{
		try {
			forwardMessage.setFrom(this.respondee.toAddress());
			forwardMessage.setRecipient(RecipientType.TO, originalTo.toAddress());
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			forwardMessage.writeTo(outStream);
			return new String(outStream.toByteArray());
		} catch (IOException e) {
			throw new MessagingException("IOException occured", e); //really shouldn't happen
		}
	}
	
	public EmailAddress getOriginalFrom() {
		return originalFrom;
	}
	
	public EmailAddress getOriginalTo() {
		return originalTo;
	}
	
	public MimeMessage getOriginalMessage() {
		return originalMessage;
	}
	
	public MimeMessage getForwardMessage() {
		return forwardMessage;
	}
	
	@Override
	public void initVars() {
		//not implemented
	}

	@Override
	protected String getResponseTemplate() {
		return null; //not implemented
	}

}
