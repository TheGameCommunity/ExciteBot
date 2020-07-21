package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.gamebuster19901.excite.bot.audit.ban.Pardon;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class PardonCommand extends WiimmfiCommand{

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!pardon").then(Commands.argument("discordUser", StringArgumentType.string()).then(Commands.argument("discriminator", StringArgumentType.string()).executes((context) -> {
			return pardon(context.getSource(), context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class));
		}).then(Commands.argument("banId", LongArgumentType.longArg()).executes((context) -> {
			return pardon(context.getSource(), context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class), context.getArgument("banId", Long.class));
		}))))
		.then(Commands.argument("discordId", LongArgumentType.longArg()).executes((context) -> {
			return pardon(context.getSource(), context.getArgument("discordId", Long.class), 1);
		})).then(Commands.argument("banId", LongArgumentType.longArg()).executes((context) -> {
			return pardon(context.getSource(), context.getArgument("banId", Long.class), context.getArgument("banId", Long.class));
		})));
	}
	
	private static DiscordUser getDiscordUser(String username, String discriminator) {
		DiscordUser user = DiscordUser.getDiscordUser(username + "#" + discriminator);
		if(user == null) {
			user = new UnknownDiscordUser(username, discriminator);
		}
		return user;
	}
	
	private static DiscordUser getDiscordUser(long id) {
		DiscordUser user = DiscordUser.getDiscordUserIncludingUnknown(id);
		return user;
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(MessageContext context, String username, String discriminator) {
		if(context.isAdmin()) {
			DiscordUser user = getDiscordUser(username, discriminator);
			if(user instanceof UnknownDiscordUser) {
				context.sendMessage("Could not find user " + username + "#" + discriminator);
				return 1;
			}
			user.pardon(context);
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(MessageContext context, String username, String discriminator, long banId) {
		if(context.isAdmin()) {
			DiscordUser user = getDiscordUser(username, discriminator);
			if(user instanceof UnknownDiscordUser) {
				context.sendMessage("Could not find user " + username + "#" + discriminator);
				return 1;
			}
			try {
				user.pardon(context, new Pardon(context, banId));
			}
			catch(IllegalArgumentException e) {
				context.sendMessage(e.getMessage());
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(MessageContext context, long discordId, long banId) {
		if(context.isAdmin()) {
			DiscordUser user = getDiscordUser(discordId);
			if(user instanceof UnknownDiscordUser) {
				context.sendMessage("Could not find user by id (" + discordId + ")");
				return 1;
			}
			try {
				user.pardon(context, new Pardon(context, banId));
			}
			catch(IllegalArgumentException e) {
				context.sendMessage(e.getMessage());
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
