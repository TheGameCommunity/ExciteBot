package com.gamebuster19901.excite.bot.mail;

import java.time.Instant;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

public class AddFriendResponse extends MailReplyResponse {

	public AddFriendResponse(MimeMessage message) {
		super(message);
	}

	@Override
	protected MimeMessage getResponseTemplate() throws MessagingException {
		MimeMessage message = super.getResponseTemplate();
		message.setSubject("WC24 Cmd Message");
		message.addHeader("X-Wii-AppId", "0-00000001-0001");
		message.addHeader("X-Wii-Cmd", "80010001");
		MimeBodyPart textPart = genEmptyTextPart();
		textPart.setText("WC24 CMD Message");
		
	}
	
}
