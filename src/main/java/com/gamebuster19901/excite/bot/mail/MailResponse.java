package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;

public abstract class MailResponse {
	
	public abstract String getResponse() throws MessagingException;
	
}
