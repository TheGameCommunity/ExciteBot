package com.gamebuster19901.excite.bot.user;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.gamebuster19901.excite.bot.common.preferences.Preference;

public class DurationPreference extends Preference<Duration>{
	
	private Duration minimum = Duration.ZERO;
	private Duration maximum = ChronoUnit.FOREVER.getDuration();
	
	public DurationPreference() {
		this(Duration.ZERO);
	}
	
	public DurationPreference(Duration value, Duration minimum, Duration maximum) {
		this(value);
		this.minimum = minimum;
		this.maximum = maximum;
		if(maximum.compareTo(minimum) < 0) {
			throw new IllegalArgumentException("Maximum value must be greater than minimum value");
		}
		if(value.compareTo(minimum) < 0) {
			this.value = maximum;
		}
		if(value.compareTo(maximum) > 0) {
			this.value = minimum;
		}
	}
	
	public DurationPreference(Duration value) {
		super(value);
	}
	
	@Override
	public Duration setValue(Duration duration) {
		if(value.compareTo(minimum) < 0) {
			this.value = maximum;
		}
		if(value.compareTo(maximum) > 0) {
			this.value = minimum;
		}
		this.value = duration;
		return this.value;
	}

	@Override
	public Duration convertString(String value) {
		long minutes = Long.parseLong(value);
		if(minutes < 5) {
			minutes = 5;
		}
		return Duration.ofMinutes(minutes);
	}

	@Override
	public String toString() {
		return value.toMinutes() + "";
	} 

}
