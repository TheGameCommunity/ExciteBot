package com.gamebuster19901.excite.bot.command;

import java.sql.SQLException;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.database.sql.DatabaseConnection;
import com.gamebuster19901.excite.bot.server.DiscordServer;
import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.MessageUtil;
import com.gamebuster19901.excite.util.Named;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class MessageContext<E>{
	
	private E event;
	private AbnormalMessage message;
	
	public MessageContext(E e) {
		if(e instanceof GuildMessageReceivedEvent || e instanceof PrivateMessageReceivedEvent || e instanceof DiscordUser || e instanceof Player) {
			this.event = e;
		}
		else {
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	public MessageContext(E e, String message) {
		this(e);
		long id = 0;
		if(e instanceof GuildMessageReceivedEvent) {
			id = ((GuildMessageReceivedEvent) e).getMessageIdLong();
		}
		if(e instanceof PrivateMessageReceivedEvent) {
			id = ((PrivateMessageReceivedEvent) e).getMessageIdLong();
		}
		this.message = new AbnormalMessage(message, id);
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
	
	public Named getAuthor() {
		Named author;
		author = getDiscordAuthor();
		if(author == null) {
			author = getPlayerAuthor();
		}
		return author;
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
	
	public void sendMessage(MessageEmbed message) {
		if(!isConsoleMessage()) {
			if(event instanceof GuildMessageReceivedEvent) {
				((GuildMessageReceivedEvent)event).getChannel().sendMessage(message).complete();
			}
			else if (event instanceof PrivateMessageReceivedEvent) {
				((PrivateMessageReceivedEvent)event).getChannel().sendMessage(message).complete();
			}
			else if (event instanceof DiscordUser) {
				((DiscordUser) event).sendMessage(message);
			}
		}
		else if (isConsoleMessage()) {
			throw new UnsupportedOperationException();
		}
		if(isIngameEvent()) {
			throw new UnsupportedOperationException();
		}
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
		else if (isConsoleMessage()) {
			System.out.println(message);
		}
		if(isIngameEvent()) {
			throw new UnsupportedOperationException();
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
		if(event instanceof DiscordUser) {
			return getDiscordAuthor().getID();
		}
		if(event instanceof GuildMessageReceivedEvent) {
			return ((GuildMessageReceivedEvent) event).getAuthor().getIdLong();
		}
		if(event instanceof PrivateMessageReceivedEvent) {
			return ((PrivateMessageReceivedEvent)event).getAuthor().getIdLong();
		}
		if (event instanceof Player) {
			return ((Player) event).getID();
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
	
	public DatabaseConnection getConnection() throws SQLException {
		return DatabaseConnection.INSTANCE;
	}
	
	@Nullable
	public Message getMessage() {
		if(event instanceof GuildMessageReceivedEvent) {
			return ((GuildMessageReceivedEvent) event).getMessage();
		}
		if(event instanceof PrivateMessageReceivedEvent) {
			return ((PrivateMessageReceivedEvent) event).getMessage();
		}
		
		return message;
		
	}
}
