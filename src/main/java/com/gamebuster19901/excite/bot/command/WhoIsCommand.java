package com.gamebuster19901.excite.bot.command;

import java.time.Duration;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

@SuppressWarnings("rawtypes")
public class WhoIsCommand {

	private static final boolean [] UNDER_ONE_DAY = new boolean[]      {true, true, true, true, true, true, true};
	private static final boolean [] BETWEEN_DAY_WEEK = new boolean[]   {true, true, true, true, true, true, false};
	private static final boolean [] BETWEEN_WEEK_MONTH = new boolean[] {true, true, true, true, true, false, false};
	private static final boolean [] BETWEEN_MONTH_YEAR = new boolean[] {true, true, true, true, false, false, false};
	private static final boolean [] OVER_ONE_YEAR = new boolean[]      {true, true, true, false, false, false, false};
	
	private static final boolean [] HOURS_ONLY = new boolean[]         {false, false, false, false, true, false, false};
	
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		LiteralCommandNode<CommandContext> builder = dispatcher.register(Commands.literal("whois")
			.then(Commands.argument("player", StringArgumentType.greedyString()).executes((command) -> {
				return sendResponse(command.getSource(), command.getArgument("player", String.class));
			}
		)));
		
		dispatcher.register(Commands.literal("me").executes((command) ->  {
			return sendResponse(command.getSource(), "" + command.getSource().getAuthor().getIdLong());
		}));
		
		dispatcher.register(Commands.literal("wi").redirect(builder));
	}
	
	@SuppressWarnings("serial")
	public static int sendResponse(CommandContext context, String lookingFor) {
		//TODO: send response
		return 1;
	}
	
	public static final String readableDuration(Duration duration, boolean includeHours) {
		String suffix = "";
		if(includeHours) {
			suffix = " (" + TimeUtils.readableDuration(duration, HOURS_ONLY) + ")";
		}
		if(duration.compareTo(Duration.ofDays(1)) < 0) {
			return TimeUtils.readableDuration(duration, UNDER_ONE_DAY) + suffix;
		}
		if(duration.compareTo(Duration.ofDays(7)) < 0) {
			return TimeUtils.readableDuration(duration, BETWEEN_DAY_WEEK) + suffix;
		}
		if(duration.compareTo(Duration.ofDays(30)) < 0) {
			return TimeUtils.readableDuration(duration, BETWEEN_WEEK_MONTH) + suffix;
		}
		if(duration.compareTo(Duration.ofDays(365)) < 0) {
			return TimeUtils.readableDuration(duration, BETWEEN_MONTH_YEAR) + suffix;
		}
		return TimeUtils.readableDuration(duration, OVER_ONE_YEAR) + suffix;
	}
	
}
