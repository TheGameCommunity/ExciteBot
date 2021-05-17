package com.gamebuster19901.excite.bot.mail;

import java.io.InputStream;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public abstract class MailReplyResponse extends MailResponse {
	
	public MailReplyResponse(MimeMessage message) {
		super(message);
	}
	
	@Override
	public void respond() throws MessagingException {
		send();
	}

	protected final void send() throws MessagingException {
		
	}
	
	protected MimeMessage getResponseTemplate() throws MessagingException {
		Session session = message.getSession();
		MimeMessage response = new MimeMessage(session);
		response.addFrom(session.get);
		return response;
	}
	
	protected MimeBodyPart genEmptyPart() {
		return new MimeBodyPart();
	}
	
	protected MimeBodyPart genEmptyTextPart() throws MessagingException {
		return genEmptyTextPart("us-ascii");
	}
	
	protected MimeBodyPart genEmptyTextPart(String encoding) throws MessagingException {
		MimeBodyPart textPart = genEmptyPart();
		textPart.setText("", encoding);
		return textPart;
	}
	
	protected MimeBodyPart genContentPart(DataHandler dataHandler) throws MessagingException {
		MimeBodyPart contentPart = genEmptyPart();
		contentPart.setDataHandler(dataHandler);
	}
	
}
