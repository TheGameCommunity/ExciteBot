package com.gamebuster19901.excite.bot.mail;

import java.io.OutputStream;

import javax.mail.Message;

public class NoResponse extends MailResponse {
	
	NoResponse(Message prompt) {
		super(prompt);
	}

	@Override
	public void writeTo(OutputStream o) {
		//NO-OP
	}

}
