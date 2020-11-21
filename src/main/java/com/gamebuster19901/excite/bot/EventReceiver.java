package com.gamebuster19901.excite.bot;

import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.server.DiscordServer;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventReceiver extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		DiscordServer.addServer(e.getGuild());
		DiscordUser.addUser(e.getAuthor());
		if(!e.getAuthor().isBot()) {
			Commands.DISPATCHER.handleCommand(e);
		}
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		DiscordUser.addUser(e.getAuthor());
		if(!e.getAuthor().isBot()) {
			Commands.DISPATCHER.handleCommand(e);
		}
	}
	
}
