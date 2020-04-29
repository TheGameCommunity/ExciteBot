package com.gamebuster19901.excite.bot.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class MessageContext<E>{
	
	private E event;
	
	public MessageContext(E e) {
		if(e == null || e instanceof GuildMessageReceivedEvent || e instanceof PrivateMessageReceivedEvent) {
			this.event = e;
		}
		else {
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	public MessageContext() {
		this.event = null;
	}
	
	public boolean isGuildMessage() {
		return event instanceof GuildMessageReceivedEvent;
	}
	
	public boolean isPrivateMessage() {
		return event instanceof PrivateMessageReceivedEvent;
	}
	
	public boolean isConsoleMessage() {
		return event == null;
	}
	
	public E getEvent() {
		return event;
	}
}
