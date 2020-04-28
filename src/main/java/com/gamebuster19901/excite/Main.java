package com.gamebuster19901.excite;

import java.net.MalformedURLException;

public class Main {
	
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
    	Wiimmfi wiimmfi = null;
    	if(args.length > 0) {
    		for(int i = 0; i < args.length; i++) {
    			if(args[i] == "-url") {
					wiimmfi = new Wiimmfi(args[++i]);
    			}
    		}
    	}
    	if(wiimmfi == null) {
    		wiimmfi = new Wiimmfi();
    	}
    	
    	Player[] onlinePlayers = wiimmfi.getOnlinePlayers();
		System.out.println("Online players (" + onlinePlayers.length + "):");
    	for(int i = 0; i < onlinePlayers.length; i++) {
    		Player p = onlinePlayers[i];
    		if(!Player.isPlayerKnown(p.getPlayerID())) {
    			Player.addPlayer(p);
    		}
    		System.out.println(p.getName());
    	}
    	
    	Player.updatePlayerListFile();
    	
    	@SuppressWarnings("static-access")
		Player[] knownPlayers = wiimmfi.getKnownPlayers();
    	System.out.println("Known players :(" + knownPlayers.length + ")");
    	for(int i = 0; i < knownPlayers.length; i++) {
    		System.out.println(knownPlayers[i].getName());
    	}
    	
    	while(true) {
    		if(wiimmfi.getOnlinePlayers().length == 0) {
    			Thread.sleep(60000);
    		}
    		else {
    			Thread.sleep(30000);
    		}
    	}
    }
}
