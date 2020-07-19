package com.gamebuster19901.excite.bot.common.preferences;

public class ShortPreference extends Preference<Short>{

	public ShortPreference(Short value) {
		super(value);
	}

	@Override
	public Short convertString(String value) {
		return Short.parseShort(value);
	}

	@Override
	public String toString() {
		return value + "";
	}

}
