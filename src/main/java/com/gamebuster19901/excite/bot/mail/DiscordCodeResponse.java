package com.gamebuster19901.excite.bot.mail;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.user.Wii;

public class DiscordCodeResponse extends TextualMailResponse {

	private static final Set<Wii> desiredWiis = Collections.newSetFromMap(new ConcurrentHashMap<Wii, Boolean>());
	
	final String registrationCode;
	final boolean hadOldCode;
	
	public DiscordCodeResponse(Wii responder, Wii wiiToRegister, MimeMessage message) throws MessagingException {
		super(responder, wiiToRegister, message);
		hadOldCode = (wiiToRegister.getRegistrationCode() != null) ? true : false;
		registrationCode = wiiToRegister.generateRegistrationCode();
		desiredWiis.add(wiiToRegister);
		setText(registrationCode);
	}
	
	
	
}
