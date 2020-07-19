package com.gamebuster19901.excite.bot.video;

public enum Placement {

	FIRST("1st", 1),
	SECOND("2nd", 2),
	THIRD("3rd", 3),
	FOURTH("4th", 4),
	FIFTH("5th", 5),
	SIXTH("6th", 6),
	DNF("DNF", 6);

	private String stringVal;
	private int placement;
	
	Placement(String stringVal, int placement) {
		this.stringVal = stringVal;
		this.placement = placement;
	}
	
	public String toString() {
		return stringVal;
	}
	
	public short toShort() {
		return (short) placement;
	}
	
}
