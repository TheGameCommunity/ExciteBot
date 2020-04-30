package com.gamebuster19901.excite.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public final class TimeUtils {
	public static String readableDuration(Duration duration) {
		String time = "";
		
		if(duration.equals(ChronoUnit.FOREVER.getDuration())) {
			return " forever";
		}
		
		if(duration.isNegative()) {
			time = "-";
			duration = duration.abs();
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
}
