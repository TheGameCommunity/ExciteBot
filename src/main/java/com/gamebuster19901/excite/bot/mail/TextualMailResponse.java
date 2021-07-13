package com.gamebuster19901.excite.bot.mail;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;

import com.gamebuster19901.excite.bot.user.Wii;

public class TextualMailResponse extends MailReplyResponse {
	
	private String text = "";
	
	public TextualMailResponse(Wii responder, Wii respondee, MimeMessage message) throws MessagingException {
		super(responder, respondee, message);
	}
	
	public TextualMailResponse(Wii responder, MimeMessage message) throws MessagingException {
		super(responder, message);
	}

	@Override
	protected String getResponseTemplate() {
		try {
			return IOUtils.toString(TextualMailResponse.class.getResourceAsStream("/mail/templates/Email.email"), Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(new MessagingException("Unable to retrieve response template", e));
		}
	}
	
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void initVars() {
		super.initVars();
		setVar("text", text);
	}

	
}
