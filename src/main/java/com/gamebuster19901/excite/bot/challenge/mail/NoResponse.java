package com.gamebuster19901.excite.bot.challenge.mail;

import java.util.logging.Level;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class NoResponse extends MailResponse {
	
	NoResponse(MimeMessage message) {
		super(message);
	}
	
	@Override
	public void respond() throws MessagingException {
		MailHandler.LOGGER.log(Level.INFO, "Refusing to respond to " + message.getSender());
	}

}
