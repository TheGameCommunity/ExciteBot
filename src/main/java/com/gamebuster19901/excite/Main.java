package com.gamebuster19901.excite;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import com.gamebuster19901.excite.bot.DiscordBot;

public class Main {
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public static Wiimmfi wiimmfi;
	public static DiscordBot discordBot;
	
	public static void main(String[] args) throws InterruptedException {
	
		if(args.length % 2 != 0) {
			throw new IllegalArgumentException("Must be started with an even number of arguments!");
		}
		
		wiimmfi = startWiimmfi(args);
		discordBot = null;
		try {
			discordBot = startDiscordBot(args, wiimmfi);
			discordBot.setWiimmfi(wiimmfi);
		} catch (LoginException | IOException e) {
			LOGGER.log(Level.SEVERE, e, () -> e.getMessage());
		}
	
		Throwable prevError = null;
		discordBot.updatePresence();
		while(true) {
			wiimmfi.update();
			Throwable error = wiimmfi.getError();
			if(error == null) {
				if(prevError != null) {
					LOGGER.log(Level.SEVERE, "Error resolved.");
				}
				
				Player[] onlinePlayers = wiimmfi.getOnlinePlayers();
				Player.updatePlayerListFile();
				
				LOGGER.info("Players online: " + onlinePlayers.length);
				int waitTime = 60000;
				if(onlinePlayers.length > 1) {
					waitTime = waitTime / onlinePlayers.length;
					if(waitTime < 4000) {
						waitTime = 4000;
					}
				}
				Thread.sleep(waitTime);
			}
			else {
				Thread.sleep(5000);
				if(prevError == null || !prevError.getClass().equals(error.getClass())) {
					System.out.println("Error!");
					LOGGER.log(Level.SEVERE, error, () -> error.getMessage());
				}
			}
				discordBot.updatePresence();
			prevError = error;
		}
	}

	private static Wiimmfi startWiimmfi(String[] args) {
		for(int i = 0; i < args.length; i++) {
				if(args[i].equalsIgnoreCase("-url")) {
					return new Wiimmfi(args[++i]);
				}
		}
		return new Wiimmfi();
	}
	
	private static DiscordBot startDiscordBot(String[] args, Wiimmfi wiimmfi) throws LoginException, IOException {
			String botOwner = null;
			File keyFile = new File("./discord.secret");
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("-owner")) {
				botOwner = args[++i];
			}
			if(args[i].equalsIgnoreCase("-keyFile")) {
				keyFile = new File(args[++i]);
			}
		}
		return new DiscordBot(wiimmfi, botOwner, keyFile);
	}
}
