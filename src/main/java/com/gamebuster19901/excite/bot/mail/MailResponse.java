package com.gamebuster19901.excite.bot.mail;

import java.util.logging.Level;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public abstract class MailResponse {

	protected MimeMessage message;
	
	public MailResponse(MimeMessage message) {
		this.message = message;
	}
	
	public abstract void respond() throws MessagingException;
	
	public MailResponse handleResponseFailure(Throwable t) throws Throwable {
		Throwable original = t;
		for(int i = 1; i < 4; i++) {
			try {
				MailHandler.LOGGER.log(Level.WARNING, "Failed to respond, retrying " + i + "/3", t);
				respond();
				return new NoResponse(message);
			}
			catch(Throwable t2) {
				t = t2;
			}
		}
		throw original;
	}
	
}
