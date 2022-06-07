package com.gamebuster19901.excite.bot.command.argument;

import java.time.Duration;

import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.command.exception.ParseExceptions;
import com.gamebuster19901.excite.util.TimeUtils;
import com.gamebuster19901.excite.util.TimeUtils.DurationBuilder;
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
		int initCursor = reader.getCursor();
		String part1 = Commands.readString(reader);
		DurationBuilder duration = new DurationBuilder(min, max);
		if(part1.equalsIgnoreCase("forever")) {
			if(max.compareTo(TimeUtils.FOREVER) < 0) {
				throw ParseExceptions.DURATION_TOO_LONG.create("forever", max);
			}
			return TimeUtils.FOREVER;
		}
		
		reader.setCursor(initCursor);
		if(reader.canRead()) {
			int firstAmount = reader.readInt();
			if(reader.canRead(2)) {
				reader.skip();
				String firstTimeUnit = Commands.readString(reader);
				if (duration.canAccept(firstAmount, firstTimeUnit)) {
					duration.add(firstAmount, firstTimeUnit);
				}
				else {
					throw ParseExceptions.INVALID_TIMEUNIT.create(firstTimeUnit);
				}
			}
			else {
				throw ParseExceptions.INVALID_DURATION.create(firstAmount);
			}
		}
		
		int prevCursor = reader.getCursor();
		while(reader.canRead()) {
			reader.skip();
			int firstAmount;
			try {
				firstAmount = reader.readInt();
			}
			catch(CommandSyntaxException e) {
				reader.setCursor(reader.getCursor() - 1);
				break;
			}
			if(reader.canRead(2)) {
				reader.skip();
				String firstTimeUnit = Commands.readString(reader);
				if (duration.canAccept(firstAmount, firstTimeUnit)) {
					duration.add(firstAmount, firstTimeUnit);
				}
				else {
					reader.setCursor(prevCursor - 1);
					break;
				}
			}
			else {
				reader.setCursor(prevCursor - 1);
				break;
			}
		}
		return duration.getDuration();
		
	}

}
