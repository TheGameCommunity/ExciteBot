package com.gamebuster19901.excite.bot.command.argument;

import java.util.List;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.command.exception.ParseExceptions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageChannel;

public class MessageChannelObtainer {
	
	String channel = "";
	
	MessageChannelObtainer(StringReader reader) throws CommandSyntaxException {
		channel = Commands.readString(reader);
	}
	
	public MessageChannel obtain(MessageContext context) throws CommandSyntaxException {
		GuildChannel ret;
		String channelName;
		if(!channel.isBlank()) {
			if (channel.charAt(0) == '#') {
				channel = channel.substring(1);
				channelName = channel;
				List<GuildChannel> channels = context.getServer().getGuild().getChannels(true);
				for(GuildChannel channel : channels) {
					if(channel.getName().equals(channelName)) {
						ret = channel;
						break;
					}
				}
				throw ParseExceptions.CHANNEL_NOT_FOUND_IN_SERVER.create(context.getServer(), channelName);
			}
			else {
				long channelID;
				try {
					if(channel.startsWith("<#")) {
						channelID = Long.parseLong(channel.substring(2, channel.length() - 1));
					}
					else {
						channelID = Long.parseLong(channel);
					}
				}
				catch(NumberFormatException e) {
					throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong().create(channel);
				}
				GuildChannel channel = Main.discordBot.jda.getGuildChannelById(channelID);
				if(channel == null) {
					throw ParseExceptions.CHANNEL_NOT_FOUND.create(channelID);
				}
				ret = channel;
			}
			if(ret instanceof GuildChannel) {
				if(ret instanceof MessageChannel) {
					return (MessageChannel) ret;
				}
				else {
					throw ParseExceptions.TEXT_CHANNEL_REQUIRED.create(ret.getGuild(), ret.getAsMention());
				}
			}
			else {
				throw ParseExceptions.PUBLIC_CHANNEL_REQUIRED.create(ret.getAsMention());
			}
		}
		else {
			return context.getChannel();
		}
	}
	
}
