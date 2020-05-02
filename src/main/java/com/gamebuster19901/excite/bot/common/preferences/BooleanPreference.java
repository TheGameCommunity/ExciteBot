package com.gamebuster19901.excite.bot.common.preferences;

public class BooleanPreference extends Preference<Boolean>{

	public BooleanPreference(Boolean value) {
		super(value);
	}

	@Override
	public Boolean convertString(String value) {
		return Boolean.parseBoolean(value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
