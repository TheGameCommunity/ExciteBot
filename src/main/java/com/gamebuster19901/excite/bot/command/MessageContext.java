package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.server.DiscordServer;
import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.MessageUtil;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class MessageContext<E>{
	
	private E event;
	
	public MessageContext(E e) {
		if(e instanceof GuildMessageReceivedEvent || e instanceof PrivateMessageReceivedEvent || e instanceof DiscordUser) {
			this.event = e;
		}
		else {
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public MessageContext() {
		this.event = (E) ConsoleUser.INSTANCE;
	}
	
	public boolean isGuildMessage() {
		return event instanceof GuildMessageReceivedEvent;
	}
	
	public boolean isPrivateMessage() {
		return event instanceof PrivateMessageReceivedEvent || event instanceof DiscordUser;
	}
	
	public boolean isConsoleMessage() {
		return event instanceof ConsoleUser;
	}
	
	public E getEvent() {
		return event;
	}
	
	public DiscordUser getAuthor() {
		if(event instanceof GuildMessageReceivedEvent) {
			return DiscordUser.getDiscordUser(((GuildMessageReceivedEvent)event).getMessage().getAuthor().getIdLong());
		}
		else if (event instanceof PrivateMessageReceivedEvent) {
			return DiscordUser.getDiscordUser(((PrivateMessageReceivedEvent)event).getMessage().getAuthor().getIdLong());
		}
		else if (event instanceof DiscordUser) {
			return (DiscordUser) event;
		}
		return null;
	}
	
	public boolean isAdmin() {
		if (isOperator()){
			return true;
		}
		else if(event instanceof GuildMessageReceivedEvent) {
			GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;
			return getAuthor().isAdmin();
		}
		return false;
	}
	
	public boolean isOperator() {
		return isConsoleMessage() || getAuthor().isOperator();
	}
	
	public void sendMessage(String message) {
		if(!isConsoleMessage()) {
			if(event instanceof GuildMessageReceivedEvent) {
				for(String submessage : MessageUtil.toMessages(message)) {
					((GuildMessageReceivedEvent)event).getChannel().sendMessage(submessage).complete();
				}
			}
			else if (event instanceof PrivateMessageReceivedEvent) {
				for(String submessage : MessageUtil.toMessages(message)) {
					((PrivateMessageReceivedEvent)event).getChannel().sendMessage(submessage).complete();
				}
			}
			else if (event instanceof DiscordUser) {
				for(String submessage : MessageUtil.toMessages(message)) {
					((DiscordUser) event).sendMessage(submessage);
				}
			}
		}
		else {
			System.out.println(message);
		}
	}
	
	public String getMention() {
		if(isConsoleMessage()) {
			return "@CONSOLE";
		}
		return getAuthor().getJDAUser().getAsMention();
	}
	
	public String getTag() {
		if(isConsoleMessage()) {
			return "CONSOLE";
		}
		return getAuthor().getJDAUser().getAsTag();
	}
	
	public long getSenderId() {
		if(isConsoleMessage()) {
			return -1;
		}
		return getAuthor().getJDAUser().getIdLong();
	}
	
	public DiscordServer getServer() {
		if(event instanceof GuildMessageReceivedEvent) {
			return DiscordServer.getServer(((GuildMessageReceivedEvent)event).getMessage().getGuild().getIdLong());
		}
		return null;
	}
	
	public MessageChannel getChannel() {
		if (event instanceof GuildMessageReceivedEvent) {
			return ((GuildMessageReceivedEvent) event).getChannel();
		}
		if (event instanceof PrivateMessageReceivedEvent) {
			return ((PrivateMessageReceivedEvent) event).getChannel();
		}
		return null;
	}
}
