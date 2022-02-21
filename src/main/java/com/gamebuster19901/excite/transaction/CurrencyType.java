package com.gamebuster19901.excite.transaction;

public enum CurrencyType {

	CRYSTALS((byte) 0),
	STARS((byte) 1);

	final byte i;
	
	CurrencyType(byte i) {
		this.i = i;
	}
	
	public byte getIndex() {
		return i;
	}
	
}
