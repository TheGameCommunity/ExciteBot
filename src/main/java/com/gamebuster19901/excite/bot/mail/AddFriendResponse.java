package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.user.Wii;

public class AddFriendResponse extends MailReplyResponse {

	Wii wiiToBefriend;
	
	public AddFriendResponse(Wii responder, Wii wiiToBefriend, MimeMessage message) throws MessagingException {
		super(responder, message);
		this.wiiToBefriend = wiiToBefriend;
	}

	@Override
	protected String getResponseTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initVars() {
		super.initVars();
	}
	
}
