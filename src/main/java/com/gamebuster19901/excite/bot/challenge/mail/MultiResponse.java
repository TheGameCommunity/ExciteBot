package com.gamebuster19901.excite.bot.challenge.mail;

import java.util.LinkedHashSet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MultiResponse extends MailResponse {

	private final LinkedHashSet<MailResponse> responses = new LinkedHashSet<MailResponse>();
	
	public MultiResponse(MimeMessage message) {
		super(message);
	}

	public void addResponse(MailResponse response) {
		if(response instanceof MultiResponse || response instanceof NoResponse) {
			throw new IllegalArgumentException("Cannot add a " + response.getClass().getSimpleName() + " into a MultiResponse!");
		}
		responses.add(response);
	}

	@Override
	public void respond() throws MessagingException {
		for(MailResponse response : responses) {
			response.respond();
		}
	}
	
}
