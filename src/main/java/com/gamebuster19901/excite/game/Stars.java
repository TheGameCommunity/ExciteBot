package com.gamebuster19901.excite.game;

public class Stars {
	
	short starCount;
	
	public Stars(String starCount) {
		if(starCount.equals("OUT")) {
			this.starCount = -1;
		}
		else if (starCount == null || starCount.isEmpty()) {
			this.starCount = -2;
		}
		else {
			this.starCount = Short.parseShort(starCount);
		}
	}
	
	public String toString() {
		if(starCount == -1) {
			return "OUT";
		}
		else if (starCount == -2) {
			return "";
		}
		return starCount + "";
	}
	
}
