package com.gamebuster19901.excite.bot.common.preferences;

public class BytePreference extends Preference<Byte>{

	public BytePreference(Byte value) {
		super(value);
	}

	@Override
	public Byte convertString(String value) {
		return Byte.parseByte(value);
	}

	@Override
	public String toString() {
		return value + "";
	}

}
