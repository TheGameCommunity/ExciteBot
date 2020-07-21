package com.gamebuster19901.excite;

public class UnknownPlayer extends Player {

	public static final UnknownPlayer INSTANCE = new UnknownPlayer();
	
	private UnknownPlayer() {
		super("Unknown Player", "NOT_REAL", -1);
	}

}
