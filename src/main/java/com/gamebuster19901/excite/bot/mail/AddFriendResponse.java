package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;

import com.gamebuster19901.excite.bot.user.Wii;

public class AddFriendResponse extends MailReplyResponse {

	Wii wiiToBefriend;
	
	public AddFriendResponse(Wii responder, Wii wiiToBefriend, MimeMessage message) throws MessagingException {
		super(responder, message);
		this.wiiToBefriend = wiiToBefriend;
	}
	
	@Override
	protected FormBodyPartBuilder getResponseTemplate(Wii responder, boolean overwrite) throws MessagingException {
		FormBodyPartBuilder replyBuilder = super.getResponseTemplate(responder, overwrite);
		replyBuilder.setField("Subject", "WC24 Cmd Message");
		replyBuilder.setField("X-Wii-AppId", "0-00000001-0001");
		InputStreamBody fileStream = new InputStreamBody(AddFriendResponse.class.getResourceAsStream("/com/gamebuster19901/mail/addFriend.dat"), ContentType.APPLICATION_OCTET_STREAM, "addFriend.dat");
		replyBuilder.setBody(fileStream);
		return replyBuilder;
	}
	
}
