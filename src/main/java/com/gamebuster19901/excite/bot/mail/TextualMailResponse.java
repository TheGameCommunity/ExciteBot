package com.gamebuster19901.excite.bot.mail;

import java.io.IOException;
import java.util.Base64;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;

import com.gamebuster19901.excite.bot.user.Wii;

import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_8;

public class TextualMailResponse<T extends ElectronicAddress> extends MailReplyResponse<T> {
	
	public static final Base64.Encoder ENCODER = Base64.getEncoder();
	
	private String text = "";
	
	public TextualMailResponse(MimeMessage messageToRespondTo) throws MessagingException {
		super(messageToRespondTo);
	}
	
	public TextualMailResponse(T recipient) throws MessagingException {
		super(recipient);
	}

	@Override
	protected String getResponseTemplate() {
		try {
			return IOUtils.toString(TextualMailResponse.class.getResourceAsStream("/com/gamebuster19901/mail/templates/Email.email"), Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(new MessagingException("Unable to retrieve response template", e));
		}
	}
	
	public TextualMailResponse<T> setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public void initVars() {
		super.initVars();
		StringBuilder builder = new StringBuilder();
		String txt;
		if(this.respondee instanceof Wii) {
			txt = ENCODER.encodeToString(UTF_16BE.encode(text).array());
		}
		else {
			txt = ENCODER.encodeToString(UTF_8.encode(text).array());
		}
		System.out.println(txt);
		for(int i = 0; i < txt.length(); i++) {
			if(i > 0 && (i % 76 == 0)) {
				builder.append('\n');
			}
			builder.append(txt.charAt(i));
		}
		setVar("text", builder.toString());
		System.out.println(builder.toString());
	}
	
}
