package com.gamebuster19901.excite.bot;

import com.gamebuster19901.excite.Main;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public abstract class WiimmfiCommand {

	protected final static boolean checkNotErrored(Object o) {
		sendTyping(o);
		if(o instanceof GuildMessageReceivedEvent) {
			return checkNotErrored((GuildMessageReceivedEvent) o);
		}
		else if (o instanceof PrivateMessageReceivedEvent) {
			return checkNotErrored((PrivateMessageReceivedEvent) o);
		}
		return checkNotErrored();
	}
	
	private static final boolean checkNotErrored(GuildMessageReceivedEvent e) {
		e.getChannel().sendTyping().complete();
		Throwable error = Main.discordBot.getWiimmfi().getError();
		if(error != null) {
			e.getChannel().sendMessage("Could not retrieve data: " + error.getClass().getCanonicalName() + ":" + error.getMessage()).complete();
			return false;
		}
		return true;
	}
	
	private static final boolean checkNotErrored(PrivateMessageReceivedEvent e) {
		e.getChannel().sendTyping().complete();
		Throwable error = Main.discordBot.getWiimmfi().getError();
		if(error != null) {
			e.getChannel().sendMessage("Could not retrieve data: " + error.getClass().getCanonicalName() + ":" + error.getMessage()).complete();
			return false;
		}
		return true;
	}
	
	private static final boolean checkNotErrored() {
		return Main.discordBot.getWiimmfi().getError() == null;
	}
	
	protected static void sendTyping(Object o) {
		if(o instanceof GuildMessageReceivedEvent) {
			((GuildMessageReceivedEvent) o).getChannel().sendTyping().complete();
		}
		else if (o instanceof PrivateMessageReceivedEvent) {
			((PrivateMessageReceivedEvent) o).getChannel().sendTyping().complete();
		}
	}
	
	public static final int send(String message, Object messageEvent) {
		if(messageEvent instanceof GuildMessageReceivedEvent) {
			GuildMessageReceivedEvent guildEvent = (GuildMessageReceivedEvent) messageEvent;
			if(guildEvent.getGuild().getSelfMember().hasPermission(guildEvent.getChannel(), Permission.MESSAGE_WRITE)) {
				guildEvent.getChannel().sendMessage(message).complete();
			}
		}
		else if (messageEvent instanceof PrivateMessageReceivedEvent) {
			((PrivateMessageReceivedEvent) messageEvent).getChannel().sendMessage(message).complete();
		}
		else {
			throw new IllegalArgumentException("messageEvent must be a messageEvent or null, but it was " + messageEvent.getClass());
		}
		return 0;
	}
	
}
