package com.gamebuster19901.excite.bot.challenge.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
	
}
