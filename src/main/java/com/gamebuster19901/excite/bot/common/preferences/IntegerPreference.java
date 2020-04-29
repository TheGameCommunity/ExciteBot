package com.gamebuster19901.excite.bot.common.preferences;

public class IntegerPreference extends Preference<Integer>{

	public IntegerPreference(String name, Integer value) {
		super(name, value);
	}
	
	public IntegerPreference(String name, String value) {
		super(name, Integer.parseInt(value));
	}

	@Override
	public Integer convertString(String value) {
		return Integer.parseInt(value);
	}

	@Override
	public String toString() {
		return name + ":" + value;
	}

}
