package com.gamebuster19901.excite.bot.command;

import java.time.Duration;

import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class NotifyCommand extends WiimmfiCommand{

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!notify").then(Commands.literal("threshold").then(Commands.argument("amount", IntegerArgumentType.integer()).executes((context) -> {
			return setThreshold(context.getSource(), context.getArgument("amount", Integer.class));
		})))
		.then(Commands.literal("frequency").then(Commands.argument("amount", IntegerArgumentType.integer()).then(Commands.argument("timeUnit", StringArgumentType.word()).executes((context) -> {
			return setFrequency(context.getSource(), context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class));
		}))))
		.then(Commands.literal("disable").executes((context) -> {
			return setThreshold(context.getSource(), -1);
		})));
	}
	
	@SuppressWarnings("rawtypes")
	private static int setThreshold(MessageContext context, int threshold) {
		if(!context.isConsoleMessage()) {
			if(threshold > 0) {
				context.getAuthor().setNotifyThreshold(threshold);
				context.sendMessage(context.getMention() + ", the player count must now be " + threshold + " or higher for you to be notified");
			}
			else if (threshold == -1) {
				context.getAuthor().setNotifyThreshold(threshold);
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
	private static int setFrequency(MessageContext context, int amount, String timeUnit) {
		if(!context.isConsoleMessage()) {
			Duration frequency = TimeUtils.computeDuration(amount, timeUnit);
			if(frequency != null) {
				Duration min = Duration.ofMinutes(5);
				if(frequency.compareTo(min) >= 0) {
					context.getAuthor().setNotifyFrequency(frequency);
					context.sendMessage(context.getMention() + "You will now be notified of online players a maximum of once every " + TimeUtils.readableDuration(frequency));
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
	
}
