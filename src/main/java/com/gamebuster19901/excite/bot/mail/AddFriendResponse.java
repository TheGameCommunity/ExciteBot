package com.gamebuster19901.excite.bot.mail;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import com.gamebuster19901.excite.bot.user.Wii;

public class AddFriendResponse extends MailReplyResponse {

	Wii wiiToBefriend;
	
	public AddFriendResponse(Wii responder, Wii wiiToBefriend, MimeMessage message) throws MessagingException {
		super(responder, message);
		this.wiiToBefriend = wiiToBefriend;
	}
	
	@Override
	public void respond() throws MessagingException {
		try {
			FormBodyPartBuilder commandBuilder = FormBodyPartBuilder.create();
			commandBuilder.setBody(new StringBody("WC24 Cmd Message", Charset.forName(US_ASCII)));
		}
		catch (UnsupportedEncodingException e) {
			throw new MessagingException("", e);
		}
	}
	
	@Override
	protected List<FormBodyPartBuilder> getResponseTemplates(Wii responder) throws MessagingException {
		try {
			List<FormBodyPartBuilder> builders = super.getResponseTemplates(responder);
			
			//Default reply builder
			
			FormBodyPartBuilder defaultReplyBuilder = builders.get(0);
			defaultReplyBuilder.setField("Subject", "WC24 Cmd Message");
			defaultReplyBuilder.setField("X-Wii-AppId", "0-00000001-0001");
			defaultReplyBuilder.setField("MIME-Version", "1.0");
			
			//Text reply builder
			FormBodyPartBuilder stringCommandBuilder = FormBodyPartBuilder.create("WC24 Cmd Message", new StringBody("WC24 Cmd Message", Charset.forName(US_ASCII)));
			builders.add(stringCommandBuilder);
			
			//File reply builder
			
			InputStreamBody fileStream = new InputStreamBody(AddFriendResponse.class.getResourceAsStream("/com/gamebuster19901/mail/addFriend.dat"), ContentType.APPLICATION_OCTET_STREAM, "addFriend.dat");
			defaultReplyBuilder.setBody(fileStream);
			FormBodyPartBuilder fileBuilder = FormBodyPartBuilder.create("addFreind.dat", fileStream);
			builders.add(fileBuilder);
			
			return builders;
		}
		catch(UnsupportedEncodingException e) {
			throw new MessagingException("US-ASCII not supported?!", e);
		}
	}
	
}
