package com.gamebuster19901.excite.bot.command.argument;

import java.time.Duration;

import com.gamebuster19901.excite.bot.command.exception.ParseExceptions;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class DurationArgumentType implements ArgumentType<Duration>{

	private Duration min = Duration.ZERO;
	private Duration max = TimeUtils.FOREVER;
	
	private DurationArgumentType() {}
	
	public static DurationArgumentType duration() {return new DurationArgumentType();}
	
	public DurationArgumentType min(Duration duration) {
		min = duration;
		return this;
	}
	
	public DurationArgumentType max(Duration duration) {
		max = duration;
		return this;
	}
	
	@Override
	public Duration parse(StringReader reader) throws CommandSyntaxException {
		String part1 = reader.readStringUntil(' ');
		if(part1.equalsIgnoreCase("forever ")) {
			if(max.compareTo(TimeUtils.FOREVER) < 0) {
				throw ParseExceptions.DURATION_TOO_LONG.create("forever", max);
			}
			return TimeUtils.FOREVER;
		}
		try {
			int amount = Integer.parseInt(part1);
			String part2 = reader.readStringUntil(' ');
			Duration duration = TimeUtils.computeDuration(amount, part2);
			if(duration != null) {
				int compare = Duration.ZERO.compareTo(duration);
				if(compare == 0) {
					throw ParseExceptions.ZERO_DURATION.createWithContext(reader);
				}
				else if (compare > 0) {
					throw ParseExceptions.NEGATIVE_DURATION.createWithContext(reader, duration);
				}
				compare = min.compareTo(duration);
				if(compare > 0) {
					throw ParseExceptions.DURATION_TOO_SHORT.createWithContext(reader, duration, min);
				}
				compare = max.compareTo(duration);
				if(compare < 0) {
					throw ParseExceptions.DURATION_TOO_LONG.createWithContext(reader, duration, max);
				}
				return duration;
			}
			throw ParseExceptions.INVALID_TIMEUNIT.createWithContext(reader, part2);
		}
		catch(NumberFormatException e) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader, part1);
		}
	}

}
