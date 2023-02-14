package com.gamebuster19901.excite.bot;

import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.StacktraceUtil;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class EventReceiver extends ListenerAdapter {

	private final Sub sub = new Sub();
	
	@Override
	public void onGenericEvent(GenericEvent ge) {
		if(ge instanceof GenericInteractionCreateEvent) {
			GenericInteractionCreateEvent e = (GenericInteractionCreateEvent) ge;
			User user = e.getUser();
			try {
				if(!DiscordUser.isKnown(e.getUser())) {
					DiscordUser.addUser(e.getUser());
				}
			}
			catch(Throwable t) {
				if(e.getInteraction() instanceof IReplyCallback) {
					new CommandContext(e.getInteraction()).replyMessage(StacktraceUtil.getStackTrace(t));
				}
			}
		}
		sub.onEvent(ge);
	}
	

	
	private static final class Sub extends ListenerAdapter {
		
		@Override
		public void onMessageReceived(MessageReceivedEvent e) {
			MessageChannel channel = e.getChannel();
			DiscordUser.addUser(e.getAuthor());
			if(!e.getAuthor().isBot() && !e.getAuthor().isSystem()) {
				Commands.DISPATCHER.handleCommand(e);
			}
		}
		
	}
	
}
