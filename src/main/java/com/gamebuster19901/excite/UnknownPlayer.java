package com.gamebuster19901.excite;

public class UnknownPlayer extends Player {

	public static final UnknownPlayer INSTANCE = new UnknownPlayer();
	
	private UnknownPlayer() {
		super("Unknown Player", "NOT_REAL", -1);
	}
	
	private UnknownPlayer(String name) {
		super(name, "NOT_REAL", -1);
	}

}
