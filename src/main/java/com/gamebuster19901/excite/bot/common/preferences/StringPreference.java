package com.gamebuster19901.excite.bot.common.preferences;

public class StringPreference extends Preference<CharSequence>{

	public StringPreference(String name, String value) {
		super(name, value);
	}

	@Override
	public String convertString(String value) {
		return value;
	}

	@Override
	public String toString() {
		return name + ":" + value;
	}

}
