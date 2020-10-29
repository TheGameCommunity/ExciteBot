package com.gamebuster19901.excite.bot.command;

import java.sql.Connection;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.database.DatabaseConnection;
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
		if(e instanceof GuildMessageReceivedEvent || e instanceof PrivateMessageReceivedEvent || e instanceof DiscordUser || e instanceof Player) {
			this.event = e;
		}
		else {
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public MessageContext() {
		this.event = (E) Main.CONSOLE;
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
	
	public boolean isIngameEvent() {
		return event instanceof Player;
	}
	
	public E getEvent() {
		return event;
	}
	
	public DiscordUser getDiscordAuthor() {
		if(event instanceof GuildMessageReceivedEvent) {
			return DiscordUser.getDiscordUser(ConsoleContext.INSTANCE, ((GuildMessageReceivedEvent)event).getMessage().getAuthor().getIdLong());
		}
		else if (event instanceof PrivateMessageReceivedEvent) {
			return DiscordUser.getDiscordUser(ConsoleContext.INSTANCE, ((PrivateMessageReceivedEvent)event).getMessage().getAuthor().getIdLong());
		}
		else if (event instanceof DiscordUser) {
			return (DiscordUser) event;
		}
		return null;
	}
	
	public Player getPlayerAuthor() {
		if(event instanceof Player) {
			return (Player) event;
		}
		return null;
	}
	
	public boolean isAdmin() {
		if (isOperator()){
			return true;
		}
		return getDiscordAuthor().isAdmin();
	}
	
	public boolean isOperator() {
		return isConsoleMessage() || getDiscordAuthor().isOperator();
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
		if(isIngameEvent()) {
			throw new UnsupportedOperationException();
		}
		else {
			System.out.println(message);
		}
	}
	
	public String getMention() {
		if(isConsoleMessage()) {
			return "@CONSOLE";
		}
		if(isIngameEvent()) {
			throw new UnsupportedOperationException();
		}
		return getDiscordAuthor().getJDAUser().getAsMention();
	}
	
	public String getTag() {
		if(isConsoleMessage()) {
			return "CONSOLE";
		}
		if(isIngameEvent()) {
			return getPlayerAuthor().toString();
		}
		return getDiscordAuthor().getJDAUser().getAsTag();
	}
	
	public long getSenderId() {
		if(isConsoleMessage()) {
			return -1;
		}
		if(event instanceof DiscordUser) {
			return getDiscordAuthor().getJDAUser().getIdLong();
		}
		if(event instanceof GuildMessageReceivedEvent) {
			return ((GuildMessageReceivedEvent) event).getAuthor().getIdLong();
		}
		if(event instanceof PrivateMessageReceivedEvent) {
			return ((PrivateMessageReceivedEvent)event).getAuthor().getIdLong();
		}
		if (event instanceof Player) {
			return ((Player) event).getPlayerID();
		}
		throw new IllegalStateException(event.getClass().getCanonicalName());
	}
	
	public DiscordServer getServer() {
		if(event instanceof GuildMessageReceivedEvent) {
			return DiscordServer.getServer(ConsoleContext.INSTANCE, ((GuildMessageReceivedEvent)event).getMessage().getGuild().getIdLong());
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
	
	public Connection getConnection() {
		return getDiscordAuthor().getConnection();
	}
	
	public DatabaseConnection getDatabaseConnection() {
		return getDiscordAuthor().getDatabaseConnection();
	}
}
