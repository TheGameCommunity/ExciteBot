package com.gamebuster19901.excite.bot.command;

import java.time.Duration;

import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class NotifyCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		dispatcher.register(Commands.userGlobal("notify").then(Commands.literal("threshold").then(Commands.argument("amount", IntegerArgumentType.integer()).executes((context) -> {
			return setThreshold(context.getSource(), context.getArgument("amount", Integer.class));
		})))
		.then(Commands.literal("frequency").then(Commands.argument("amount", IntegerArgumentType.integer()).then(Commands.argument("timeUnit", StringArgumentType.word()).executes((context) -> {
			return setFrequency(context.getSource(), context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class));
		}))))
		.then(Commands.literal("continuous").then(Commands.argument("continuous", BoolArgumentType.bool()).executes((context) -> {
			return setContinuous(context.getSource(), context.getArgument("continuous", Boolean.class));
		})))
		.then(Commands.literal("disable").executes((context) -> {
			return setThreshold(context.getSource(), -1);
		}))
		.then(Commands.literal("detailed").then(Commands.argument("detailed", BoolArgumentType.bool()).executes((context) -> {
			return setDetailed(context.getSource(), context.getArgument("detailed", Boolean.class));
		}))));
	}
	
	@SuppressWarnings("rawtypes")
	private static int setThreshold(CommandContext context, int threshold) {
		if(!context.isConsoleMessage()) {
			if(threshold > 0) {
				DiscordUser.setNotifyThreshold(context.getAuthor(), threshold);
				context.sendMessage(context.getMention() + ", the player count must now be " + threshold + " or higher for you to be notified");
			}
			else if (threshold == -1) {
				DiscordUser.setNotifyThreshold(context.getAuthor(), threshold);
				context.sendMessage(context.getMention() + ", you will no longer be notified if players are online.");
			}
			else {
				context.sendMessage("Player threshold must be `1` or heigher, or `-1` to never be notified when players are online");
			}
		}
		else {
			context.sendMessage("This command must be executed from discord");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int setFrequency(CommandContext context, int amount, String timeUnit) {
		if(!context.isConsoleMessage()) {
			Duration frequency = TimeUtils.computeDuration(amount, timeUnit);
			if(frequency != null) {
				Duration min = Duration.ofMinutes(5);
				if(frequency.compareTo(min) >= 0) {
					DiscordUser.setNotifyFrequency(context.getAuthor(), frequency);
					context.sendMessage(context.getMention() + ", You will now be notified of online players a maximum of once every " + TimeUtils.readableDuration(frequency));
				}
				else {
					context.sendMessage("Notification frequency must be 5 minutes or longer");
				}
			}
			else {
				context.sendMessage("[" + amount + " " +  timeUnit + "] is not a valid duration");
			}
		}
		else {
			context.sendMessage("This command must be executed from discord");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int setContinuous(CommandContext context, boolean continuous) {
		if(!context.isConsoleMessage()) {
			DiscordUser.setNotifyContinuously(context.getAuthor(), continuous);
			context.sendMessage(context.getAuthor().getAsMention() + ", you have set continuous notifications to " + continuous);
		}
		else {
			context.sendMessage("This command must be executed from discord");
		}
		return 1;
	}
	
	private static int setDetailed(CommandContext context, boolean detailed) {
		if(!context.isConsoleMessage()) {
			DiscordUser.setSendDetailedPM(context.getAuthor(), detailed);
			context.sendMessage(context.getAuthor().getAsMention() + ", you have set detailed PM messages to " + detailed);
		}
		else {
			context.sendMessage("This command must be executed from discord");
		}
		return 1;
	}
	
}
