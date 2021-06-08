package com.gamebuster19901.mailtest;

public enum WiiApp {

	WII_SYSTEM_COMMAND("Wii System Command", "0-00000001-0001"),
	WII_MESSAGE("the standard wii mail system", "2-48414541-0001"), //might be wrong
	EXCITEBOTS_TRICK_RACING_NTSC("Excitebots: Trick Racing (USA)", "1-52583345-3031");
	
	private final String name;
	private final String appID;
	
	private WiiApp(String name, String appID) {
		this.name = name;
		this.appID = appID;
	}
	
	public final String getName() {
		return name;
	}
	
	public final String toString() {
		return appID;
	}
	
}
