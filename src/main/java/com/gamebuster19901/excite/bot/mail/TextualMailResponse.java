package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.user.Wii;

public class TextualMailResponse extends MailReplyResponse implements EMessage {
	
	private static final String TEXTUAL_RESPONSE = null;
	
	public TextualMailResponse(Wii responder, Wii respondee, MimeMessage message) throws MessagingException {
		super(responder, respondee, message);
	}
	
	public TextualMailResponse(Wii responder, MimeMessage message) throws MessagingException {
		super(responder, message);
	}
	
	public void setText(String text) throws MessagingException {
		getResponseTemplates(responder).set(0, text);
	}

	@Override
	protected Object getDefaultResponseTemplate(Wii responder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFrom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSubject() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
