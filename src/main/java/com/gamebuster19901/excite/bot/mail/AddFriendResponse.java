package com.gamebuster19901.excite.bot.mail;

import java.io.File;
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.gamebuster19901.excite.bot.user.Wii;

public class AddFriendResponse extends MailReplyResponse implements WiiMailResponse {
	
	private static final File DATA_FILE = new File("./src/main/resources/com/gamebuster19901/mail/templates/a0000102.dat");
	
	public AddFriendResponse(Wii responder, Wii wiiToBefriend, Message message) throws MessagingException, IOException {
		super(responder, wiiToBefriend, message);
		setFrom(responder);
		setTo(respondee);
		setSubject("WC24 Cmd Message");
		setAppID("0-00000001-0001");
		setWiiCMD("80010001");
		
		Multipart multipart = new MimeMultipart();
		
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText("WC24 Cmd  Message", "us-ascii");
		
		MimeBodyPart attachmentPart = new MimeBodyPart();
		attachmentPart.attachFile(DATA_FILE, "application/octet-stream;", "base64");
		
		multipart.addBodyPart(textPart);
		multipart.addBodyPart(attachmentPart);
		
		response.setContent(multipart);
		response.saveChanges();
	}
	
	@Override
	public AddFriendResponse setFrom(ElectronicAddress from) throws AddressException, MessagingException {
		super.setFrom(from);
		response.setHeader("MAIL_FROM", responder.getEmail());
		return this;
	}
	
	@Override
	public AddFriendResponse setTo(ElectronicAddress to) throws AddressException, MessagingException {
		super.setTo(to);
		response.setHeader("RCPT TO", respondee.getEmail());
		return this;
	}


	@Override
	public AddFriendResponse setAppID(String id) throws MessagingException {
		response.setHeader("X-Wii-AppId", id);
		return this;
	}

	@Override
	public AddFriendResponse setWiiCMD(String cmd) throws MessagingException {
		response.setHeader("X-Wii-Cmd", cmd);
		return this;
	}
	
}
