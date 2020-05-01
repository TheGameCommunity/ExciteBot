package com.gamebuster19901.excite.bot.command;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class BanCommand extends WiimmfiCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!ban").then(Commands.argument("discordUser", StringArgumentType.word()).then((Commands.argument("discriminator", StringArgumentType.string()).executes((context) -> {
			return banUserForever(context.getSource(), getDiscordUser(context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class)));
		}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
			return banUserForever(context.getSource(), getDiscordUser(context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class)), context.getArgument("reason", String.class));
		}))
		.then(Commands.argument("amount", IntegerArgumentType.integer(1)).then(Commands.argument("timeUnit", StringArgumentType.string()).executes((context) -> {
			return banUser(context.getSource(), getDiscordUser(context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class)), context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class));
		}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
			return banUser(context.getSource(), getDiscordUser(context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class)), context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class), context.getArgument("reason", String.class));
		}))))))));
	}
	
	private static DiscordUser getDiscordUser(String username, String discriminator) {
		DiscordUser user = DiscordUser.getDiscordUser(username + "#" + discriminator);
		if(user == null) {
			throw new IllegalArgumentException(username + "#" + discriminator);
		}
		return user;
	}
	
	@SuppressWarnings("rawtypes")
	private static int banUser(MessageContext context, DiscordUser user, int amount, String timeUnit) {
		return banUser(context, user, amount, timeUnit, null);
	}
	
	@SuppressWarnings("rawtypes")
	private static int banUser(MessageContext context, DiscordUser user, int amount, String timeUnit, String reason) {
		if(user != null) {
			Duration duration = TimeUtils.computeDuration(amount, timeUnit);
			if(duration != null) {
				return banUser(context, user, duration, reason);
			}
		}
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	private static int banUserForever(MessageContext context, DiscordUser user) {
		return banUserForever(context, user, null);
	}
	
	@SuppressWarnings("rawtypes")
	private static int banUserForever(MessageContext context, DiscordUser user, String reason) {
		if(context.isAdmin()) {
			if(user != null) {
				Duration duration = ChronoUnit.FOREVER.getDuration();
				if(duration != null) {
					user.ban(context, duration, parseReason(duration, reason));
				}
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 0;
	}

	
	@SuppressWarnings("rawtypes")
	private static int banUser(MessageContext context, DiscordUser user, Duration duration, String reason) {
		if(context.isAdmin()) {
			user.ban(new MessageContext(), duration, parseReason(duration, reason));
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	

	
	private static String parseReason(Duration duration, String reason) {
		if(ChronoUnit.FOREVER.getDuration().equals(duration)) {
			if(reason != null) {
				return "You have been banned from using Excite bot indefinetly due to " + reason;
			}
			else {
				return "You have been banned from using Excite bot indefinetly";
			}
		}
		if(reason != null) {
			return "You have been banned from using Excite Bot for " + TimeUtils.readableDuration(duration) + " due to " + reason;
		}
		else {
			return "You have been banned from using Excite Bot for " + TimeUtils.readableDuration(duration);
		}
	}
}
