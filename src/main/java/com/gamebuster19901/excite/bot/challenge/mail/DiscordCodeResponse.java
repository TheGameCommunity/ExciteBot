package com.gamebuster19901.excite.bot.challenge.mail;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.user.Wii;

public class DiscordCodeResponse extends MailReplyResponse {

	private static final Set<Wii> desiredWiis = Collections.newSetFromMap(new ConcurrentHashMap<Wii, Boolean>());
	
	final String registrationCode;
	final boolean hadOldCode;
	
	public DiscordCodeResponse(Wii wii, MimeMessage message) {
		super(message);
		hadOldCode = (wii.getRegistrationCode() != null) ? true : false;
		registrationCode = wii.generateRegistrationCode();
		desiredWiis.add(wii);
	}
	
}
