package com.gamebuster19901.excite.bot.mail;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;

import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.bot.user.Wii.InvalidWii;

public class AddFriendResponse extends MailReplyResponse {
	
	public AddFriendResponse(Wii responder, EmailAddress wiiToBefriend, MimeMessage message) throws MessagingException {
		super(responder, wiiToBefriend, message);
		System.out.println(wiiToBefriend);
		System.out.println(wiiToBefriend.getEmail());
		System.out.println(wiiToBefriend.getClass().getClassLoader());
		System.out.println(wiiToBefriend.getClass());
		if(wiiToBefriend instanceof InvalidWii) {
			throw new AssertionError();
		}
		if(wiiToBefriend instanceof Wii) {
			throw new AssertionError();
		}
	}

	@Override
	protected String getResponseTemplate() {
		try {
			return IOUtils.toString(TextualMailResponse.class.getResourceAsStream("/com/gamebuster19901/mail/templates/Friend Request.email"), Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(new MessagingException("Unable to retrieve response template", e));
		}
	}
	
}
