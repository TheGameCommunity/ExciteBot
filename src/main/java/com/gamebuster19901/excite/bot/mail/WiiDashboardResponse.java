package com.gamebuster19901.excite.bot.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.gamebuster19901.excite.bot.user.Wii;

public class WiiDashboardResponse extends TextualMailResponse implements WiiMailResponse {

	public WiiDashboardResponse(Wii responder, ElectronicAddress respondee, Message message) throws MessagingException {
		super(responder, respondee, message);
		setFrom(responder);
		setTo(respondee);
		setAppID("2-48414541-0001");
		setWiiCMD("00042019");
	}
	
	@Override
	public WiiDashboardResponse setFrom(ElectronicAddress from) throws AddressException, MessagingException {
		super.setFrom(from);
		response.setHeader("MAIL_FROM", responder.getEmail());
		return this;
	}
	
	@Override
	public WiiDashboardResponse setTo(ElectronicAddress to) throws AddressException, MessagingException {
		super.setTo(to);
		response.setHeader("RCPT TO:", respondee.getEmail());
		return this;
	}

	@Override
	public WiiDashboardResponse setAppID(String id) throws MessagingException {
		response.setHeader("X-Wii-AppId", id);
		return this;
	}

	@Override
	public WiiDashboardResponse setWiiCMD(String cmd) throws MessagingException {
		response.setHeader("X-Wii-Cmd", cmd);
		return this;
	}
	
	@Override
	public WiiDashboardResponse setText(String text) throws MessagingException {
		return (WiiDashboardResponse) super.setText(text);
	}
	
}
