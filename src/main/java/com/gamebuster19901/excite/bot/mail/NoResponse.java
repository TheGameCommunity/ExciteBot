package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class NoResponse extends MailResponse {
	
	NoResponse(MimeMessage message) {
		super(message);
	}
	
	@Override
	public String getResponse() throws MessagingException {
		return null;
	}

}
