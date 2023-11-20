package com.gamebuster19901.excite.bot.mail;

import java.io.IOException;
import java.io.OutputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.user.Wii;

public abstract class MailReplyResponse extends MailResponse implements EmailResponse {
	
	protected Wii responder;
	protected ElectronicAddress respondee;
	protected Message response = new MimeMessage(Mailbox.SESSION);
	
	public MailReplyResponse(Wii responder, ElectronicAddress respondee, Message message) throws MessagingException {
		super(message);
		this.responder = responder;
		this.respondee = respondee;
	}
	
	public MailReplyResponse(Wii responder, MimeMessage message) throws MessagingException {
		this(responder, Wii.getWii(message.getFrom()[0].toString()), message);
	}
	
	public Wii getResponder() {
		return responder;
	}
	
	public ElectronicAddress getRespondee() {
		return respondee;
	}
	
	public Message getResponse() throws MessagingException {
		return response;
	}
	
	@Override
	public MailReplyResponse setSubject(String subject) throws MessagingException {
		response.setSubject(subject);
		return this;
	}

	@Override
	public MailReplyResponse setFrom(ElectronicAddress address) throws AddressException, MessagingException {
		response.setFrom(new InternetAddress(address.getEmail()));
		return this;
	}

	@Override
	public MailReplyResponse setTo(ElectronicAddress to) throws AddressException, MessagingException {
		response.setRecipient(RecipientType.TO, new InternetAddress(to.getEmail()));
		return this;
	}
	
	@Override
	public final void writeTo(OutputStream o) throws IOException, MessagingException {
		response.writeTo(o);
	}
	
}
