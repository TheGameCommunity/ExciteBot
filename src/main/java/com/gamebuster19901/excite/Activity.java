package com.gamebuster19901.excite;

public enum Activity {

	ONLINE('o'),
	GLOBAL('G'),
	VIEWING('v'),
	SEARCHING('S'),
	NONE('-'),
	UNKNOWN('?');
	
	private char code;
	
	private Activity(char c) {
		
	}
	
	@Override
	public String toString() {
		return new String(new char[] {code});
	}
	
	public static Activity fromChar(char code) {
		for(Activity activity : Activity.values()) {
			if(activity.code == code) {
				return activity;
			}
		}
		return UNKNOWN;
	}
	
}
