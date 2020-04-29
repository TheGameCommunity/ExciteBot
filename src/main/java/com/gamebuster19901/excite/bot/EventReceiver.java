package com.gamebuster19901.excite.bot;

import com.gamebuster19901.excite.bot.command.Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventReceiver extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if(!e.getAuthor().isBot()) {
			Commands.DISPATCHER.handleCommand(e);
		}
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		if(!e.getAuthor().isBot()) {
			Commands.DISPATCHER.handleCommand(e);
		}
	}
	
}
