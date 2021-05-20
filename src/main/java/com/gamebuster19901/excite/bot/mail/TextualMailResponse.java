package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.StringBody;

import com.gamebuster19901.excite.bot.user.Wii;

public class TextualMailResponse extends MailReplyResponse {
	
	public TextualMailResponse(Wii responder, Wii respondee, MimeMessage message) throws MessagingException {
		super(responder, respondee, message);
	}
	
	public TextualMailResponse(Wii responder, MimeMessage message) throws MessagingException {
		super(responder, message);
	}
	
	public void setText(String text) throws MessagingException {
		setText(text, ContentType.DEFAULT_TEXT);
	}
	
	public void setText(String text, ContentType type) throws MessagingException {
		getResponseTemplate(responder, true).setBody(new StringBody(text, type)).setName("body");
	}
	
}
