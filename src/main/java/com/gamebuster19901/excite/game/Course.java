package com.gamebuster19901.excite.game;

public class Course {

	public final Cup cup;
	public String name;
	
	public Course(Cup cup, String name) {
		this.cup = cup;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return cup + " " + name;
	}
	
	public String toFullString() {
		if(cup.isMode(Mode.SUPER_EXCITE)) {
			return cup + " cup: " + name;
		}
		else {
			return cup + ": " + name;
		}
	}
	
}
