package com.gamebuster19901.excite.bot.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.gamebuster19901.excite.bot.user.Wii;

public class AddFriendResponse extends MailReplyResponse {

	Wii wiiToBefriend;
	
	public AddFriendResponse(Wii responder, Wii wiiToBefriend, MimeMessage message) {
		super(responder, message);
		this.wiiToBefriend = wiiToBefriend;
	}

	@Override
	protected MimeMessage getResponseTemplate(Wii responder) throws MessagingException {
		MimeMessage message = super.getResponseTemplate(responder);
		message.setSubject("WC24 Cmd Message");
		message.addHeader("X-Wii-AppId", "0-00000001-0001");
		message.addHeader("X-Wii-Cmd", "80010001");
		MimeMultipart multiPart = new MimeMultipart();
		MimeBodyPart textPart = genTextPart("WC24 CMD Message");
		MimeBodyPart attachmentPart = genEmptyPart();
		attachmentPart.setDataHandler(new DataHandler(new FriendAttachment()));
		multiPart.addBodyPart(textPart);
		multiPart.addBodyPart(attachmentPart);
		message.setContent(multiPart);
		return message;
	}
	
	private static final class FriendAttachment implements DataSource {

		@Override
		public InputStream getInputStream() throws IOException {
			return FriendAttachment.class.getResourceAsStream("/com/gamebuster19901/mail/addFriend.dat");
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getContentType() {
			return "application/octet-stream";
		}

		@Override
		public String getName() {
			return "addFriend.dat";
		}
		
	}
	
}
