package com.gamebuster19901.excite.bot.mail;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import com.gamebuster19901.excite.bot.user.Wii;

public abstract class MailReplyResponse extends MailResponse {
	
	public static final String US_ASCII = "us-ascii";
	
	protected Wii responder;
	
	public MailReplyResponse(Wii responder, MimeMessage message) {
		super(message);
		this.responder = responder;
	}
	
	@Override
	public void respond() throws MessagingException {
		send();
	}

	protected final void send() throws MessagingException {
		
	}
	
	protected MimeMessage getResponseTemplate(Wii responder) throws MessagingException {
		Session session = message.getSession();
		MimeMessage response = new MimeMessage(session);
		response.setFrom(responder.getEmail());
		return response;
	}
	
	protected MimeBodyPart genEmptyPart() {
		return new MimeBodyPart();
	}
	
	protected MimeBodyPart genTextPart(String text) throws MessagingException {
		return genTextPart(text, US_ASCII);
	}
	
	protected MimeBodyPart genTextPart(String text, String encoding) throws MessagingException {
		MimeBodyPart textPart = genEmptyPart();
		textPart.setText(text, encoding);
		return textPart;
	}
	
	protected MimeBodyPart genContentPart(DataHandler dataHandler) throws MessagingException {
		MimeBodyPart contentPart = genEmptyPart();
		contentPart.setDataHandler(dataHandler);
		return contentPart;
	}
	
}
