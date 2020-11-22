package com.gamebuster19901.excite.bot.user;

import java.time.Instant;

import com.gamebuster19901.excite.bot.common.preferences.Preference;

public class InstantPreference extends Preference<Instant>{

	public InstantPreference(Instant value) {
		super(value);
	}

	@Override
	public Instant convertString(String value) {
		return Instant.parse(value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
