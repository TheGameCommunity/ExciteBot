package com.gamebuster19901.excite.game;

public enum Placement {

	FIRST('1', "1st"),
	SECOND('2', "2nd"),
	THRID('3', "3rd"),
	FOURTH('4', "4th"),
	FIFTH('5', "5th"),
	SIXTH('6', "6th"),
	DNF('D', "DNF");

	private char firstChar;
	private String name;
	
	Placement(char c, String string) {
		this.firstChar = c;
		this.name = string;
	}

	public char getChar() {
		return firstChar;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static Placement fromString(String placement) {
		for(Placement p : values()) {
			if(p.getChar() == placement.charAt(0)) {
				return p;
			}
		}
		throw new IllegalArgumentException("Invalid placement: " + placement);
	}
	
}
