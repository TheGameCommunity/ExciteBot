package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.server.DiscordServer;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.MessageUtil;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class MessageContext<E>{
	
	private E event;
	
	public MessageContext(E e) {
		if(e == null || e instanceof GuildMessageReceivedEvent || e instanceof PrivateMessageReceivedEvent || e instanceof DiscordUser) {
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
		return event instanceof PrivateMessageReceivedEvent || event instanceof DiscordUser;
	}
	
	public boolean isConsoleMessage() {
		return event == null;
	}
	
	public E getEvent() {
		return event;
	}
	
	public DiscordUser getAuthor() {
		if(isConsoleMessage()) {
			return null;
		}
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
			DiscordServer server = getServer();
			Role[] adminRoles = server.getAdminRoles();
			Member member = e.getMessage().getMember();
			for(Role role : adminRoles) {
				if(member.getRoles().contains(role)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isOperator() {
		return isConsoleMessage() || getAuthor().getJDAUser().getAsTag().equalsIgnoreCase(Main.botOwner);
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
			return "@ CONSOLE";
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
}
