package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public abstract class MailResponse {

	protected MimeMessage prompt;
	
	public MailResponse(MimeMessage prompt) {
		this.prompt = prompt;
	}
	
	public abstract String getResponse() throws MessagingException;
	
}
