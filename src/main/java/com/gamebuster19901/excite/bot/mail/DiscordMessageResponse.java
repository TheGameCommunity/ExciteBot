package com.gamebuster19901.excite.bot.mail;

import com.gamebuster19901.excite.bot.command.MessageContext;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class DiscordMessageResponse extends NonWiiResponse {
	
	private final MessageContext recipient;
	private final String message;
	private final MessageEmbed messageEmbed;
	
	public DiscordMessageResponse(MessageContext recipient, String message) {
		this.recipient = recipient;
		this.message = message;
		this.messageEmbed = null;
	}
	
	public DiscordMessageResponse(MessageContext recipient, MessageEmbed message) {
		this.recipient = recipient;
		this.message = null;
		this.messageEmbed = message;
	}

	@Override
	public void send() {
		if(message != null) {
			recipient.sendMessage(message);
		}
		if(messageEmbed != null) {
			recipient.sendMessage(message);
		}
	}

}
