package com.gamebuster19901.excite.bot.mail;

public enum Applications {

	EXCITEBOTS("1-52583345-0001"),
	FRIEND_REQUEST("0-00000001-0001"),
	WII_MESSAGE("2-48414541-0001");

	final String id;
	
	Applications(String id) {
		this.id = id;
	}
	
	public boolean matches(String id) {
		return this.id.equals(id);
	}
	
	public final String header() {
		return id;
	}
	
	public static Applications getApplicaiton(String id) {
		for(Applications app : values()) {
			if(app.matches(id)) {
				return app;
			}
		}
		return null;
	}
	
}
