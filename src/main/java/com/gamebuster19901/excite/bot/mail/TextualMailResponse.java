package com.gamebuster19901.excite.bot.mail;

import java.io.IOException;
import java.util.Base64;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;

import com.gamebuster19901.excite.bot.user.Wii;
import static java.nio.charset.StandardCharsets.UTF_16BE;

public class TextualMailResponse extends MailReplyResponse {
	
	public static final Base64.Encoder ENCODER = Base64.getEncoder();
	
	private String text = "";
	
	public TextualMailResponse(Wii responder, EmailAddress respondee, MimeMessage message) throws MessagingException {
		super(responder, respondee, message);
	}
	
	public TextualMailResponse(Wii responder, MimeMessage message) throws MessagingException {
		super(responder, message);
	}

	@Override
	protected String getResponseTemplate() {
		try {
			return IOUtils.toString(TextualMailResponse.class.getResourceAsStream("/com/gamebuster19901/mail/templates/Email.email"), Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(new MessagingException("Unable to retrieve response template", e));
		}
	}
	
	public TextualMailResponse setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public void initVars() {
		super.initVars();
		StringBuilder builder = new StringBuilder();
		String txt = ENCODER.encodeToString(UTF_16BE.encode(text).array());
		for(int i = 0; i < txt.length(); i++) {
			if(i > 0 && (i % 76 == 0)) {
				builder.append('\n');
			}
			builder.append(txt.charAt(i));
		}
		setVar("text", builder.toString());
	}
	
}
