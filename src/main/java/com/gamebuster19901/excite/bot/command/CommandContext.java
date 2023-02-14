package com.gamebuster19901.excite.bot.command;

import java.sql.SQLException;

import org.jetbrains.annotations.Nullable;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.audit.BotDeleteMessageAudit;
import com.gamebuster19901.excite.bot.database.sql.DatabaseConnection;
import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.Nobody;
import com.gamebuster19901.excite.util.Named;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class CommandContext<E>{
	
	private E event;
	private AbnormalMessage message;
	
	public CommandContext(E e) {
		if(e instanceof MessageReceivedEvent || e instanceof User || e instanceof Player) {
			this.event = e;
		}
		else {
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	public CommandContext(E e, String message) {
		this(e);
		long id = 0;
		if(e instanceof MessageReceivedEvent) {
			id = ((MessageReceivedEvent) e).getMessageIdLong();
		}
		this.message = new AbnormalMessage(message, id);
	}
	
	@SuppressWarnings("unchecked")
	public CommandContext() {
		this.event = (E) Main.CONSOLE;
	}
	
	public boolean isDiscordContext() {
		return event instanceof MessageReceivedEvent;
	}
	
	public boolean isGuildMessage() {
		return getChannel() instanceof GuildMessageChannel;
	}
	
	public boolean isStandardGuildMessage() {
		return getChannel() instanceof StandardGuildMessageChannel;
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
	
	public Named<?> getAuthor() {
		Named<?> author = null;
		if(isIngameEvent()) {
			author = getPlayerAuthor();
		}
		else if (isDiscordContext() || isConsoleMessage()) {
			author = Named.of(getDiscordAuthor());
		}
		if(author == null) {
			throw new AssertionError(author + " is not from ingame, discord, or the console?!");
		}
		return author;
	}
	
	public User getDiscordAuthor() {
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getAuthor();
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
		return DiscordUser.isAdmin(getDiscordAuthor());
	}
	
	public boolean isOperator() {
		return isConsoleMessage() || DiscordUser.isOperator(getDiscordAuthor());
	}
	
	public InteractionHook replyMessage(MessageCreateData messageData) {
		return replyMessage(messageData, false);
	}
	
	public InteractionHook replyMessage(MessageCreateData messageData, boolean ephemeral) {
		if(event instanceof IReplyCallback) {
			return ((IReplyCallback) event).reply(messageData).setEphemeral(ephemeral).complete();
		}
		else if(event instanceof MessageReceivedEvent) {
			((MessageReceivedEvent) event).getChannel().sendMessage(messageData);
			return null;
		}
		else if(event instanceof User) {
			if(event instanceof ConsoleUser) {
				System.out.println(messageData.getContent());
			}
			else {
				PrivateChannel channel = ((User)event).openPrivateChannel().complete();
				channel.sendMessage(messageData).queue();
			}
			return null;
		}
		else {
			throw new UnsupportedOperationException("Cannot reply to a " + event.getClass().getCanonicalName());
		}
	}
	
	public void replyMessage(String message) {
		if(message.length() > 2000) {
			message = message.substring(0, 2000);
		}
		replyMessage(new MessageCreateBuilder().setContent(message).build(), false);
	}
	
	public void editMessage(MessageEditData messageEdit) {
		if(event instanceof IMessageEditCallback) {
			((IMessageEditCallback) event).editMessage(messageEdit);
		}
		else {
			replyMessage(MessageCreateData.fromEditData(messageEdit));
		}
	}
	
	public void editMessage(String message) {
		editMessage(new MessageEditBuilder().closeFiles().clear().setContent(message).build());
	}
	
	public void editMessage(String message, boolean clear) {
		if(!clear) {
			editMessage(new MessageEditBuilder().setContent(message).build());
		}
		else {
			editMessage(message);
		}
	}
	
	@Deprecated
	public void sendMessage(String message) {
		replyMessage(new MessageCreateBuilder().setContent(message).build());
	}
	
	public void sendMessage(EmbedBuilder embed) {
		replyMessage(new MessageCreateBuilder().setEmbeds(embed.build()).build());
	}
	
	public String getMention() {
		if(isConsoleMessage()) {
			return "@CONSOLE";
		}
		if(isIngameEvent()) {
			throw new UnsupportedOperationException();
		}
		return getDiscordAuthor().getAsMention();
	}
	
	public String getTag() {
		if(isConsoleMessage()) {
			return "CONSOLE";
		}
		if(isIngameEvent()) {
			return getPlayerAuthor().toString();
		}
		return getDiscordAuthor().getAsTag();
	}
	
	public long getSenderId() {
		if(event instanceof DiscordUser) {
			return getDiscordAuthor().getIdLong();
		}
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getAuthor().getIdLong();
		}
		if (event instanceof Player) {
			return ((Player) event).getID();
		}
		throw new IllegalStateException(event.getClass().getCanonicalName());
	}
	
	public Guild getServer() {
		if(event instanceof MessageReceivedEvent) {
			if(((MessageReceivedEvent) event).isFromGuild()) {
				return ((MessageReceivedEvent) event).getGuild();
			}
		}
		return null;
	}
	
	public MessageChannelUnion getChannel() {
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
	
	public void deletePromptingMessage(CommandContext deleter, String response) {
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
