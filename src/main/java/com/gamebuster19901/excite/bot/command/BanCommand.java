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
		dispatcher.register(Commands.literal("!ban").requires((permission) -> {
			return true;//permission.isConsoleMessage() || permission.isAdmin();
		}).then(Commands.argument("discordUser", StringArgumentType.word()).then((Commands.argument("discriminator", StringArgumentType.string()).executes((command) -> {
			return banUserForever(getDiscordUser(command.getArgument("discordUser", String.class), command.getArgument("discriminator", String.class)));
		}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((command) -> {
			return banUserForever(getDiscordUser(command.getArgument("discordUser", String.class), command.getArgument("discriminator", String.class)), command.getArgument("reason", String.class));
		}))
		.then(Commands.argument("amount", IntegerArgumentType.integer(1)).then(Commands.argument("timeUnit", StringArgumentType.string()).executes((command) -> {
			return banUser(getDiscordUser(command.getArgument("discordUser", String.class), command.getArgument("discriminator", String.class)), command.getArgument("amount", Integer.class), command.getArgument("timeUnit", String.class));
		}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((command) -> {
			return banUser(getDiscordUser(command.getArgument("discordUser", String.class), command.getArgument("discriminator", String.class)), command.getArgument("amount", Integer.class), command.getArgument("timeUnit", String.class), command.getArgument("reason", String.class));
		}))))))));
	}
	
	private static DiscordUser getDiscordUser(String username, String discriminator) {
		DiscordUser user = DiscordUser.getDiscordUser(username + "#" + discriminator);
		if(user == null) {
			throw new IllegalArgumentException(username + "#" + discriminator);
		}
		return user;
	}
	
	private static int banUser(DiscordUser user, int amount, String timeUnit) {
		return banUser(user, amount, timeUnit, null);
	}
	
	private static int banUser(DiscordUser user, int amount, String timeUnit, String reason) {
		if(user != null) {
			Duration duration = computeDuration(amount, timeUnit);
			if(duration != null) {
				return banUser(user, duration, reason);
			}
		}
		return 0;
	}
	
	private static int banUserForever(DiscordUser user) {
		return banUserForever(user, null);
	}
	
	private static int banUserForever(DiscordUser user, String reason) {
		if(user != null) {
			Duration duration = ChronoUnit.FOREVER.getDuration();
			if(duration != null) {
				user.ban(duration, parseReason(duration, reason));
			}
		}
		return 0;
	}
	
	private static Duration computeDuration(int amount, String timeUnit) {
		Duration duration = null;
		if(isSeconds(timeUnit)) {
			duration = Duration.ofSeconds(amount);
		}
		else if(isMinutes(timeUnit)) {
			duration = Duration.ofMinutes(amount);
		}
		else if (isHours(timeUnit)) {
			duration = Duration.ofHours(amount);
		}
		else if (isDays(timeUnit)) {
			duration = Duration.ofDays(amount);
		}
		return duration;
	}
	
	private static int banUser(DiscordUser user, Duration duration, String reason) {
		user.ban(duration, parseReason(duration, reason));
		return 1;
	}
	
	private static boolean isSeconds(String timeUnit) {
		return timeUnit.equalsIgnoreCase("s") || timeUnit.equalsIgnoreCase("sec") || timeUnit.equalsIgnoreCase("secs") || timeUnit.equalsIgnoreCase("second") || timeUnit.equalsIgnoreCase("seconds");
	}
	
	private static boolean isMinutes(String timeUnit) {
		return timeUnit.equalsIgnoreCase("m") || timeUnit.equalsIgnoreCase("min") || timeUnit.equalsIgnoreCase("mins") ||  timeUnit.equalsIgnoreCase("minute") || timeUnit.equalsIgnoreCase("minutes");
	}
	
	private static boolean isHours(String timeUnit) {
		return timeUnit.equalsIgnoreCase("h") || timeUnit.equalsIgnoreCase("hr") || timeUnit.equalsIgnoreCase("hrs") ||  timeUnit.equalsIgnoreCase("hour") || timeUnit.equalsIgnoreCase("hours");
	}
	
	private static boolean isDays(String timeUnit) {
		return timeUnit.equalsIgnoreCase("d") || timeUnit.equalsIgnoreCase("day");
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
