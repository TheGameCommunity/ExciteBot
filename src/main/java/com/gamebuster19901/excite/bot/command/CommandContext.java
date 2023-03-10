package com.gamebuster19901.excite.bot.command;

import java.time.Instant;

import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class CommandContext<E> {
	
	private E event;
	private EmbedBuilder embedBuilder;
	
	public CommandContext(E e) {
		if(e instanceof MessageReceivedEvent || e instanceof Interaction || e instanceof GuildReadyEvent || e instanceof User) {
			this.event = e;
		}
		else {
			throw new IllegalArgumentException(e.getClass().getCanonicalName());
		}
	}

	public User getAuthor() {
		if(event instanceof Interaction) {
			return ((Interaction) event).getUser();
		}
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getAuthor();
		}
		if(event instanceof User) {
			return (User) event;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEvent(Class<T> type) throws ClassCastException {	
		if(type.isAssignableFrom(event.getClass())) {
			return (T)event;
		}
		throw new ClassCastException(event + " cannot be cast to " + type.getCanonicalName());
	}
	
	public boolean isDiscordContext() {
		return event instanceof ISnowflake;
	}
	
	public boolean isGuildContext() {
		return getChannel() instanceof GuildChannel;
	}
	
	public boolean isPrivateContext() {
		return getChannel() instanceof PrivateChannel;
	}
	
	public String getMention() {
		if (event instanceof Interaction) {
			return ((Interaction) event).getUser().getAsMention();
		}
		else if (event instanceof User) {
			return ((User) event).getAsMention();
		}
		else {
			throw new UnsupportedOperationException(event.getClass().getCanonicalName() + " cannot be mentioned!");
		}
	}
	
	public InteractionHook replyMessage(MessageCreateData messageData) {
		return replyMessage(messageData, false, true);
	}
	
	public InteractionHook replyMessage(MessageCreateData messageData, boolean ephemeral, boolean silent) {
		if(event instanceof IReplyCallback) {
			return ((IReplyCallback) event).reply(messageData).setEphemeral(ephemeral).setSuppressedNotifications(silent).complete();
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
		replyMessage(new MessageCreateBuilder().setContent(message).build(), false, true);
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
	public InteractionHook sendMessage(String message) {
		return replyMessage(new MessageCreateBuilder().setContent(message).build());
	}
	
	public InteractionHook  sendMessage(EmbedBuilder embed) {
		return replyMessage(new MessageCreateBuilder().setEmbeds(embed.build()).build());
	}
	
	public InteractionHook sendMessage(EmbedBuilder embed, boolean ephemeral) {
		return replyMessage(new MessageCreateBuilder().setEmbeds(embed.build()).build(), ephemeral, true);
	}
	
	public InteractionHook sendMessage(EmbedBuilder embed, boolean ephemeral, boolean silent) {
		return replyMessage(new MessageCreateBuilder().setEmbeds(embed.build()).build(), ephemeral, silent);
	}
	
	public EmbedBuilder constructEmbedResponse(String command) {
		return constructEmbedResponse(command, null);
	}
	
	public EmbedBuilder constructEmbedResponse(String command, String title) {
		User user = getAuthor();
		embedBuilder = new EmbedBuilder();
		embedBuilder.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
		embedBuilder.setTimestamp(Instant.now());
		return embedBuilder;
	}
	
	public MessageChannel getChannel() {
		if(event instanceof Interaction) {
			return ((Interaction) event).getMessageChannel();
		}
		else if (event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getChannel();
		}
		return null;
	}
	
	public EmbedBuilder getEmbed() {
		return embedBuilder;
	}
	
	public Guild getServer() {
		if(event instanceof Interaction) {
			return ((Interaction) event).getGuild();
		}
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getGuild();
		}
		return null;
	}
	
	public boolean isConsoleMessage() {
		return event instanceof ConsoleUser;
	}
	
	public boolean isAdmin() {
		return DiscordUser.isOperator(getAuthor());
	}

	public boolean isOperator() {
		return DiscordUser.isOperator(getAuthor());
	}

	public void deletePromptingMessage(ConsoleContext instance, String string) {
		// TODO Auto-generated method stub
		
	}

}