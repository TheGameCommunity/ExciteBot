package com.gamebuster19901.excite.bot.command.exception;

import java.time.Duration;

import com.gamebuster19901.excite.util.TimeUtils;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public interface ParseExceptions {

	/**
	 * DISCORD
	 */
	public DynamicCommandExceptionType DISCORD_NOT_FOUND = new DynamicCommandExceptionType(input -> new LiteralMessage("Could not find a discord user using the input `" + input + "`"));
	public Dynamic2CommandExceptionType DISCORD_AMBIGUITY = new Dynamic2CommandExceptionType((input, found) -> new LiteralMessage("`" + input + "` matches " + ((DiscordUser[])found).length + " users, supply a discriminator or specify an ID"));
	
	public SimpleCommandExceptionType DISCORD_CONTEXT_REQUIRED = new SimpleCommandExceptionType(new LiteralMessage("Command must be executed via discord."));
	public SimpleCommandExceptionType DISCORD_CHANNEL_REQUIRED = new SimpleCommandExceptionType(new LiteralMessage("Command must be executed in a discord server."));
	public SimpleCommandExceptionType PRIVATE_CHANNEL_REQUIRED = new SimpleCommandExceptionType(new LiteralMessage("Command must be executed via private message."));
	
	/**
	 * CHANNEL
	 */
	public Dynamic2CommandExceptionType TEXT_CHANNEL_REQUIRED = new Dynamic2CommandExceptionType((server, channel) -> new LiteralMessage(channel + " in " + server + " is not a text channel."));
	public DynamicCommandExceptionType PUBLIC_CHANNEL_REQUIRED = new DynamicCommandExceptionType((channel) -> new LiteralMessage(channel + " in is not a public channel."));
	public DynamicCommandExceptionType CHANNEL_NOT_FOUND = new DynamicCommandExceptionType(channel -> new LiteralMessage("Could not find channel " + channel + " in any server"));
	public Dynamic2CommandExceptionType CHANNEL_NOT_FOUND_IN_SERVER = new Dynamic2CommandExceptionType((server, channel) -> new LiteralMessage("Could not find channel " + channel + " in " + server));
	public Dynamic2CommandExceptionType INSUFFICIENT_BOT_PERMISSION = new Dynamic2CommandExceptionType((action, requiredPermission) -> {
		if(requiredPermission != null) {
			return new LiteralMessage("I do not have permission to " + action + ". (" + requiredPermission + ") required.");
		}
		else {
			return new LiteralMessage("I do not have permission to " + action);
		}
	});
	
	/**
	 * PLAYER
	 */
	public DynamicCommandExceptionType PLAYER_NOT_FOUND = new DynamicCommandExceptionType(input -> new LiteralMessage("Could not find a profile using the input `" + input + "`"));
	public Dynamic2CommandExceptionType PLAYER_AMBIGUITY = new Dynamic2CommandExceptionType((input, found) -> new LiteralMessage("`" + input + "` matches " + ((DiscordUser[])found).length + " profiles, supply an ID"));
	
	
	/**
	 * DURATION
	 */
	public DynamicCommandExceptionType NEGATIVE_DURATION = new DynamicCommandExceptionType(duration -> new LiteralMessage(handleDurationParam(duration) + " is negative."));
	public SimpleCommandExceptionType ZERO_DURATION = new SimpleCommandExceptionType(new LiteralMessage("Duration is zero"));
	public DynamicCommandExceptionType INVALID_TIMEUNIT = new DynamicCommandExceptionType(timeUnit -> new LiteralMessage(timeUnit + " is not a valid time unit. Only `second(s)`, `minute(s)`, `hour(s)`, `day(s)`, `week(s)`, `month(s)`, `year(s)` are allowed."));
	public DynamicCommandExceptionType INVALID_DURATION = new DynamicCommandExceptionType(duration -> new LiteralMessage("`" + duration + "` is not a valid duration"));
	public Dynamic2CommandExceptionType INVALID_DURATION_2 = new Dynamic2CommandExceptionType((amount, timeUnit) -> new LiteralMessage("`" + amount + " " + timeUnit + "` is not a valid duration."));
	public Dynamic2CommandExceptionType DURATION_TOO_SHORT = new Dynamic2CommandExceptionType((duration, shortest) -> new LiteralMessage(handleDurationParam(duration) + " is too short, the minumum duration allowed is " + handleDurationParam(shortest)));
	public Dynamic2CommandExceptionType DURATION_TOO_LONG = new Dynamic2CommandExceptionType((duration, longest) -> new LiteralMessage(handleDurationParam(duration) + " is too long, the maximum duration allowed is " + handleDurationParam(longest)));
	
	/**
	 * WII
	 */
	public DynamicCommandExceptionType WII_NOT_FOUND = new DynamicCommandExceptionType(wii -> new LiteralMessage("Unable to find wii `" + wii + '`'));
	public DynamicCommandExceptionType WII_INVALID = new DynamicCommandExceptionType(wii -> new LiteralMessage("Invalid wii: `" + wii + '`'));
	
	static String handleDurationParam(Object o) {
		return "`" + (o instanceof Duration ? TimeUtils.fullReadableDuration((Duration) o) : o.toString()) + "`";
	}
	
}
