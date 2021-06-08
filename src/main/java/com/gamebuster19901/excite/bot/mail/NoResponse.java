package com.gamebuster19901.excite.bot.mail;

import java.util.logging.Level;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class NoResponse extends MailResponse {
	
	NoResponse(MimeMessage message) {
		super(message);
	}
	
	@Override
	public void respond() throws MessagingException {
		Address from = null;
		if(message.getFrom() != null) {
			from = message.getFrom()[0];
		}
		MailHandler.LOGGER.log(Level.INFO, "Refusing to respond to " + from);
	}

}
