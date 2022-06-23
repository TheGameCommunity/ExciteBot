package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.user.Wii;

public abstract class MailReplyResponse extends MailResponse {
	
	protected Wii responder;
	protected ElectronicAddress respondee;
	protected String response = getResponseTemplate();
	
	public MailReplyResponse(Wii responder, ElectronicAddress respondee, MimeMessage message) {
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
	
	@Override
	public String getResponse() {
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
