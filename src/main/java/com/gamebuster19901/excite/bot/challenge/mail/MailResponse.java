package com.gamebuster19901.excite.bot.challenge.mail;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public abstract class MailResponse {

	protected MimeMessage message;
	
	public MailResponse(MimeMessage message) {
		this.message = message;
	}
	
	public abstract void respond() throws MessagingException;
	
	protected MimeMessage getResponseTemplate() throws MessagingException {
		Session session = message.getSession();
		MimeMessage response = new MimeMessage(session);
		return response;
	}
	
}
