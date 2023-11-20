package com.gamebuster19901.excite.bot.mail;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.Message;
import javax.mail.MessagingException;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.user.Wii;

public class DiscordCodeResponse extends TextualMailResponse {

	private static final Set<Wii> desiredWiis = Collections.newSetFromMap(new ConcurrentHashMap<Wii, Boolean>());
	
	final String registrationCode;
	final boolean hadOldCode;
	
	public DiscordCodeResponse(Wii responder, Wii wiiToRegister, Message message) throws MessagingException {
		super(responder, wiiToRegister, message);
		hadOldCode = (wiiToRegister.getRegistrationCode() != null) ? true : false;
		registrationCode = wiiToRegister.getRegistrationCode();
		desiredWiis.add(wiiToRegister);
		String text = "Welcome to The Game Community!\n"
				+ "\n"
				+ "In order to use the mail features\n"
				+ "of Excitebot, you must first link\n"
				+ "your discord account to your wii.\n"
				+ "\n"
				+ "Please execute the following\n"
				+ "command in a private message\n"
				+ "with %bot%:\n"
				+ "\n"
				+ "register wii %code%\n"
				+ "\n"
				+ "You should receive a message on\n"
				+ "discord once you are successfully\n"
				+ "registered. If you have any\n"
				+ "issues, please don't hesitate to\n"
				+ "ask for assistance in our discord:\n"
				+ "http://discord.gg/PGJCvTj\n"
				+ "or file a bug report at\n"
				+ "https://github.com/Gamebuster19901/ExciteBot/issues.\n"
				+ "\n"
				+ "Cheers,\n"
				+ "Gamebuster";
		text = text.replace("%bot%", Main.discordBot.getSelfUser().getAsTag());
		text = text.replace("%code%", registrationCode);
		setText(text);
	}
	
}
