package com.gamebuster19901.excite.bot;

import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.server.DiscordServer;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventReceiver extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		MessageChannel channel = e.getChannel();
		if(e.getChannel() instanceof TextChannel) {
			DiscordServer.addServer(e.getGuild());
		}
		DiscordUser.addUser(e.getAuthor());
		if(!e.getAuthor().isBot() && !e.getAuthor().isSystem()) {
			Commands.DISPATCHER.handleCommand(e);
		}
	}
	
}
