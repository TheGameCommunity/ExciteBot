package com.gamebuster19901.excite.bot.common.preferences;

public class IntegerPreference extends Preference<Integer>{

	public IntegerPreference(Integer value) {
		super(value);
	}
	
	public IntegerPreference(String value) {
		super(Integer.parseInt(value));
	}

	@Override
	public Integer convertString(String value) {
		return Integer.parseInt(value);
	}

	@Override
	public String toString() {
		return value + "";
	}

}
