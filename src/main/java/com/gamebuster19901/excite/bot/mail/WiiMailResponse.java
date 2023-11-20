package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;

public interface WiiMailResponse extends EmailResponse {
	
	public abstract WiiMailResponse setAppID(String id) throws MessagingException;
	
	public abstract WiiMailResponse setWiiCMD(String cmd) throws MessagingException;
	
	@Override
	public WiiMailResponse setFrom(ElectronicAddress from) throws MessagingException;
	
	@Override
	public WiiMailResponse setTo(ElectronicAddress to) throws MessagingException;
	
}
