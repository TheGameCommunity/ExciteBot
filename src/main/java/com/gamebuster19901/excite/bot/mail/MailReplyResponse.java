package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.user.Wii;

public abstract class MailReplyResponse<T extends ElectronicAddress> extends MailResponse {
	
	protected Wii responder = Mailbox.ADDRESS;
	protected ElectronicAddress respondee;
	protected String response = getResponseTemplate();
	
	@SuppressWarnings("unchecked")
	public MailReplyResponse(MimeMessage messageToReplyTo) throws MessagingException {
		this((T) ElectronicAddress.getAddress(messageToReplyTo.getFrom()));
	}
	
	public MailReplyResponse(T recipient) {
		this.respondee = recipient;
	}
	
	public Wii getResponder() {
		return responder;
	}
	
	public ElectronicAddress getRespondee() {
		return respondee;
	}
	
	@Override
	public String getResponse() throws MessagingException {
		return response;
	}
	
	public void initVars() {
		setVar("from", responder.getEmail());
		setVar("to", respondee.getEmail());
		setVar("boundary", Mailbox.BOUNDARY);
	}
	
	public void setVar(String var, String value) {
		response = response.replace("%" + var + "%", value);
	}
	
	protected abstract String getResponseTemplate();
	
}