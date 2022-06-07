package com.gamebuster19901.excite.bot.command;

import java.sql.SQLException;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.audit.BotDeleteMessageAudit;
import com.gamebuster19901.excite.bot.database.sql.DatabaseConnection;
import com.gamebuster19901.excite.bot.server.DiscordServer;
import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.Nobody;
import com.gamebuster19901.excite.util.MessageUtil;
import com.gamebuster19901.excite.util.Named;

import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.NewsChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageContext<E>{
	
	private E event;
	private AbnormalMessage message;
	
	public MessageContext(E e) {
		if(e instanceof MessageReceivedEvent || e instanceof DiscordUser || e instanceof Player) {
			this.event = e;
		}
		else {
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	public MessageContext(E e, String message) {
		this(e);
		long id = 0;
		if(e instanceof MessageReceivedEvent) {
			id = ((MessageReceivedEvent) e).getMessageIdLong();
		}
		this.message = new AbnormalMessage(message, id);
	}
	
	@SuppressWarnings("unchecked")
	public MessageContext() {
		this.event = (E) Main.CONSOLE;
	}
	
	public boolean isDiscordContext() {
		return event instanceof MessageReceivedEvent;
	}
	
	public boolean isGuildMessage() {
		return getChannel() instanceof BaseGuildMessageChannel;
	}
	
	public boolean isStandardGuildMessage() {
		return getChannel() instanceof TextChannel;
	}
	
	public boolean isNewsMessage() {
		return getChannel() instanceof NewsChannel;
	}
	
	public boolean isThreadMessage() {
		return getChannel() instanceof ThreadChannel;
	}
	
	public boolean isPrivateMessage() {
		return getChannel() instanceof PrivateChannel || event instanceof DiscordUser;
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
		if(event instanceof MessageReceivedEvent) {
			return DiscordUser.getDiscordUser(ConsoleContext.INSTANCE, ((MessageReceivedEvent)event).getMessage().getAuthor().getIdLong());
		}
		else if (event instanceof DiscordUser) {
			return (DiscordUser) event;
		}
		return Nobody.INSTANCE;
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
	
	public Message sendMessage(MessageEmbed message) {
		if(isDiscordContext()) {
			return ((MessageReceivedEvent)event).getChannel().sendMessageEmbeds(message).complete();
		}
		else if (isConsoleMessage()) {
			throw new UnsupportedOperationException("Cannot send an embed to the console");
		}
		else if (event instanceof DiscordUser) {
			return ((DiscordUser) event).sendMessage(message);
		}
		else if(isIngameEvent()) {
			throw new UnsupportedOperationException();
		}
		return null;
	}
	
	public void sendMessage(MessageChannel channel, String message) {
		if(isIngameEvent()) {
			throw new UnsupportedOperationException();
		}
		channel.sendMessage(message).complete();
	}
	
	public void sendMessage(String message) {
		if(isDiscordContext()) {
			if(event instanceof MessageReceivedEvent) {
				for(String submessage : MessageUtil.toMessages(message)) {
					((MessageReceivedEvent)event).getChannel().sendMessage(submessage).complete();
				}
			}
		}
		else if (event instanceof DiscordUser) {
			for(String submessage : MessageUtil.toMessages(message)) {
				((DiscordUser) event).sendMessage(submessage);
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
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getAuthor().getIdLong();
		}
		if (event instanceof Player) {
			return ((Player) event).getID();
		}
		throw new IllegalStateException(event.getClass().getCanonicalName());
	}
	
	public DiscordServer getServer() {
		if(event instanceof MessageReceivedEvent) {
			return DiscordServer.getServer(ConsoleContext.INSTANCE, ((MessageReceivedEvent)event).getMessage().getGuild().getIdLong());
		}
		return null;
	}
	
	public MessageChannel getChannel() {
		if (event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getChannel();
		}
		return null;
	}
	
	public DatabaseConnection getConnection() throws SQLException {
		return DatabaseConnection.INSTANCE;
	}
	
	@Nullable
	public Message getMessage() {
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getMessage();
		}
		
		return message;
		
	}
	
	public void deletePromptingMessage(MessageContext deleter, String response) {
		Message message = getMessage();
		if(message != null) {
			message.delete().complete();
			BotDeleteMessageAudit.addBotDeleteMessageAudit(deleter, this, "User published their registration code.");
			if(response != null && !response.isEmpty()) {
				sendMessage(response);
			}
		}
	}
}
