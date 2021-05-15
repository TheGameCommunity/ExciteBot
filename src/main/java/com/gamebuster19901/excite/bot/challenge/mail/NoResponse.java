package com.gamebuster19901.excite.bot.challenge.mail;

import java.util.logging.Level;

public class NoResponse implements MailResponse {
	
	@Override
	public void respond(Mail mail) {
		MailHandler.LOGGER.log(Level.INFO, "Refusing to respond to " + mail);
	}

}
