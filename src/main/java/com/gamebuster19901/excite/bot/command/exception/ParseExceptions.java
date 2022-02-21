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
	
	static String handleDurationParam(Object o) {
		return "`" + (o instanceof Duration ? TimeUtils.fullReadableDuration((Duration) o) : o.toString()) + "`";
	}
	
}
