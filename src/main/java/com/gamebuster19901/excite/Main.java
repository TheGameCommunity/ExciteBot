package com.gamebuster19901.excite;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import com.gamebuster19901.excite.backup.Backup;
import com.gamebuster19901.excite.bot.DiscordBot;
import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.server.DiscordServer;
import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.UserPreferences;
import com.gamebuster19901.excite.util.StacktraceUtil;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class Main {
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public static String botOwner;
	
	public static Wiimmfi wiimmfi;
	public static DiscordBot discordBot;
	
	private static ConcurrentLinkedDeque<String> consoleCommandsAwaitingProcessing = new ConcurrentLinkedDeque<String>();
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws InterruptedException {
	
		if(args.length % 2 != 0) {
			throw new IllegalArgumentException("Must be started with an even number of arguments!");
		}
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-owner")) {
				botOwner = args[++i];
			}
		}
		
		wiimmfi = startWiimmfi(args);
		discordBot = null;
		
		int bootAttempts = 0;
		while(discordBot == null) {
			try {
				bootAttempts++;
				discordBot = startDiscordBot(args, wiimmfi);
				discordBot.setWiimmfi(wiimmfi);
				discordBot.setLoading();
			} catch (LoginException | IOException | ErrorResponseException e) {
				LOGGER.log(Level.SEVERE, e, () -> e.getMessage());
				if(bootAttempts >= 3) {
					System.exit(-2);
				}
				Thread.sleep(5000);
			}
		}
	
		Throwable prevError = null;
		Instant nextWiimmfiPing = Instant.now();
		Instant nextDiscordPing = Instant.now();
		Instant updateCooldowns = Instant.now();
		Instant updateWarningCooldowns = Instant.now();
		Instant nextBackupTime = Instant.now();
		startConsole();
		
		try {
			while(true) {
				try {
					Throwable error = wiimmfi.getError();
					if(nextBackupTime.isBefore(Instant.now())) {
						Backup.backup(new MessageContext());
						nextBackupTime = nextBackupTime.plus(Duration.ofHours(1));
					}
					if(nextWiimmfiPing.isBefore(Instant.now())) {
						wiimmfi.update();
						if(error == null) {
							if(prevError != null) {
								LOGGER.log(Level.SEVERE, "Error resolved.");
							}
							Wiimmfi.updateOnlinePlayers();
							Player.updatePlayerListFile();
							
							int waitTime = 3000;
							nextWiimmfiPing = Instant.now().plus(Duration.ofMillis(waitTime));
						}
						else {
							nextWiimmfiPing = Instant.now().plus(Duration.ofMillis(5000));
							if(prevError == null || !prevError.getClass().equals(error.getClass())) {
								System.out.println("Error!");
								LOGGER.log(Level.SEVERE, error, () -> error.getMessage());
							}
						}
					}
					if(discordBot != null) {
						if(nextDiscordPing.isBefore(Instant.now())) {
							nextDiscordPing = Instant.now().plus(Duration.ofSeconds(5));
							DiscordServer.updateServerList();
							DiscordServer.updateServerPreferencesFile();
							DiscordUser.updateUserList();
							DiscordUser.updateUserPreferencesFile();
							discordBot.updatePresence();
							UserPreferences.attemptRegister();
						}
						if(updateCooldowns.isBefore(Instant.now())) {
							updateCooldowns = Instant.now().plus(Duration.ofSeconds(4));
							DiscordUser.updateCooldowns();
						}
						if(updateWarningCooldowns.isBefore(Instant.now())) {
							updateWarningCooldowns = Instant.now().plus(Duration.ofSeconds(15));
							DiscordUser.updateWarningCooldowns();
						}
					}
					
					while(!consoleCommandsAwaitingProcessing.isEmpty()) {
						Commands.DISPATCHER.handleCommand(consoleCommandsAwaitingProcessing.pollFirst());
					}
					prevError = error;
				}
				catch(ErrorResponseException e) {
					ConsoleUser.INSTANCE.sendMessage(StacktraceUtil.getStackTrace(e));
					ConsoleUser.INSTANCE.sendMessage("An ErrorResponseException occurred... waiting 10 seconds");
				}
				Thread.sleep(1000);
			}
		}
		catch(Throwable t) {
			t.printStackTrace();
			if(discordBot != null) {
				discordBot.jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(ActivityType.DEFAULT, "Bot Crashed"));
				if(botOwner != null) {
					try {
						DiscordUser user = DiscordUser.getDiscordUser(botOwner);
						if(user != null) {
							user.sendMessage(StacktraceUtil.getStackTrace(t));
						}
					}
					catch(Throwable t2) {
						ConsoleUser.INSTANCE.sendMessage(StacktraceUtil.getStackTrace(t));
					}
				}
				while(true) {Thread.sleep(1000);}
			}
			else {
				ConsoleUser.INSTANCE.sendMessage(StacktraceUtil.getStackTrace(t));
			}
		}
		System.exit(-1);
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
	
	private static void startConsole() {
		Thread consoleThread = new Thread() {
			@Override
			public void run() {
				Scanner scanner = new Scanner(System.in);
				while(scanner.hasNextLine()) {
					consoleCommandsAwaitingProcessing.addFirst(scanner.nextLine());
				}
			}
		};
		consoleThread.setName("consoleReader");
		consoleThread.setDaemon(true);
		consoleThread.start();
	}
}
