package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;

public interface EmailResponse {
	
	public abstract EmailResponse setSubject(String subject) throws MessagingException;
	
	public abstract EmailResponse setFrom(ElectronicAddress address) throws MessagingException;
	
	public abstract EmailResponse setTo(ElectronicAddress to) throws MessagingException;
	
}
