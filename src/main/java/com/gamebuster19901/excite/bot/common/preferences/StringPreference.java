package com.gamebuster19901.excite.bot.common.preferences;

public class StringPreference extends Preference<CharSequence>{

	public StringPreference(String value) {
		super(value);
	}

	@Override
	public String convertString(String value) {
		return value;
	}

	@Override
	public String toString() {
		return (String) value;
	}

}
