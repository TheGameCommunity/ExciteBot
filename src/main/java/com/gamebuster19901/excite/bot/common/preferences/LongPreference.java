package com.gamebuster19901.excite.bot.common.preferences;

public class LongPreference extends Preference<Long>{

	public LongPreference(Long value) {
		super(value);
	}

	@Override
	public Long convertString(String value) {
		return Long.parseLong(value.replaceAll("\"", ""));
	}

	@Override
	public String toString() {
		return "\"'" + value + "\"";
	}

}
