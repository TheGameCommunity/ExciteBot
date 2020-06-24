package com.gamebuster19901.excite.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public final class TimeUtils {
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.US).withZone(ZoneId.of("UTC"));
	public static final Duration FOREVER = ChronoUnit.FOREVER.getDuration();
	
	public static String readableDuration(Duration duration) {
		String time = "";
		
		if(duration.equals(ChronoUnit.FOREVER.getDuration())) {
			return " forever";
		}
		
		if(duration.isNegative()) {
			time = "-";
			duration = duration.abs();
		}
		if(duration.toDays() / 365 > 0) {
			time += duration.toDays() / 365 + " years#";
			duration = duration.minus(Duration.ofDays((duration.toDays() / 365) * 365));
		}
		if(duration.toDays() / 30 > 0) {
			time += duration.toDays() / 30 + " months#";
			duration = duration.minus(Duration.ofDays((duration.toDays() / 30) * 30));
		}
		if (duration.toDays() / 7 > 0) {
			time += duration.toDays() / 7 + " weeks#";
			duration = duration.minus(Duration.ofDays((duration.toDays() / 7) * 30));
		}
		if(duration.toDays() > 0) {
			time += duration.toDays() + " days#";
			duration = duration.minus(Duration.ofDays(duration.toDays()));
		}
		if(duration.toHours() > 0) {
			time += duration.toHours() + " hours#";
			duration = duration.minus(Duration.ofHours(duration.toHours()));
		}
		if(duration.toMinutes() > 0) {
			time += duration.toMinutes() + " minutes#";
			duration = duration.minus(Duration.ofMinutes(duration.toMinutes()));
		}
		if(duration.getSeconds() > 0) {
			time += duration.getSeconds() + " seconds";
			duration = duration.minus(Duration.ofSeconds(duration.getSeconds()));
		}
		
		return time.replaceAll("#", " ");
	}
	
	public static Instant fromNow(Duration duration) {
		try {
			return Instant.now().plus(duration);
		}
		catch(ArithmeticException e) {
			return Instant.MAX;
		}
	}
	
	public static Duration computeDuration(int amount, String timeUnit) {
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
		else if (isWeeks(timeUnit)) {
			duration = Duration.ofDays(amount * 7);
		}
		else if (isMonths(timeUnit)) {
			duration = Duration.ofDays(amount * 30);
		}
		else if (isYears(timeUnit)) {
			duration = Duration.ofDays(amount * 365);
		}
		return duration;
	}
	
	public static String getDate(TemporalAccessor temporal) {
		return DATE_FORMATTER.format(temporal);
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
		return timeUnit.equalsIgnoreCase("d") || timeUnit.equalsIgnoreCase("day") || timeUnit.equalsIgnoreCase("days");
	}
	
	private static boolean isWeeks(String timeUnit) {
		return timeUnit.equalsIgnoreCase("w") || timeUnit.equalsIgnoreCase("week") || timeUnit.equalsIgnoreCase("weeks");
	}
	
	private static boolean isMonths(String timeUnit ) {
		return timeUnit.equalsIgnoreCase("mo") || timeUnit.equalsIgnoreCase("month") || timeUnit.equalsIgnoreCase("months");
	}
	
	private static boolean isYears(String timeUnit) {
		return timeUnit.equalsIgnoreCase("y") || timeUnit.equalsIgnoreCase("yr") || timeUnit.equalsIgnoreCase("yrs") || timeUnit.equalsIgnoreCase("year") || timeUnit.equalsIgnoreCase("years");
	}
}
