package com.gamebuster19901.excite.bot.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.user.Wii;

public class TextualMailResponse extends MailReplyResponse implements EmailResponse {
	
	public TextualMailResponse(Wii responder, ElectronicAddress respondee, Message message) throws MessagingException {
		super(responder, respondee, message);
		setFrom(responder);
		setTo(respondee);
	}
	
	public TextualMailResponse(Wii responder, MimeMessage message) throws MessagingException {
		super(responder, message);
	}
	
	public TextualMailResponse setText(String text) throws MessagingException {
		response.setContent(text, "text/plain; charset=utf-16be");
		response.addHeader("Content-Transfer-Encoding", "base64");
		response.saveChanges();
		return this;
	}
	
}
