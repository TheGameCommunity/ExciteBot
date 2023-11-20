package com.gamebuster19901.excite.bot.mail;

import java.io.IOException;
import java.io.OutputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

public abstract class MailResponse {

	protected Message prompt;
	
	public MailResponse(Message prompt) {
		this.prompt = prompt;
	}
	
	public final Message getPrompt() {
		return prompt;
	}
	
	public static Session getSession() {
		return Mailbox.SESSION;
	}
	
	public abstract void writeTo(OutputStream o) throws IOException, MessagingException;
	
}
