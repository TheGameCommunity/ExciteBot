package com.gamebuster19901.excite.bot.mail;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.http.entity.mime.content.StringBody;

import com.gamebuster19901.excite.bot.user.Wii;

public class TextualMailResponse extends MailReplyResponse {
	
	private StringBody body;
	
	public TextualMailResponse(Wii responder, Wii respondee, MimeMessage message) throws MessagingException {
		super(responder, respondee, message);
	}
	
	public TextualMailResponse(Wii responder, MimeMessage message) throws MessagingException {
		super(responder, message);
	}
	
	public void setText(String text) throws MessagingException {
		setText(text, Charset.forName(US_ASCII));
	}
	
	public void setText(String text, Charset charset) throws MessagingException {
		try {
			getResponseTemplates(responder).get(0).setBody(new StringBody(text, charset)).setName("body");
		} catch (UnsupportedEncodingException e) {
			throw new MessagingException("", e);
		}
	}
	
	public void setText(StringBody body) throws MessagingException {
		getResponseTemplates(responder).get(0).setBody(body);
	}
	
}
