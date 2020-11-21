package com.gamebuster19901.excite;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import com.gamebuster19901.excite.bot.DiscordBot;
import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.database.sql.DatabaseConnection;
import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.gamebuster19901.excite.util.ThreadService;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class Main {
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public static long botOwner = -1;
	
	public static DatabaseConnection database;
	public static Wiimmfi wiimmfi;
	public static DiscordBot discordBot;
	
	private static ConcurrentLinkedDeque<String> consoleCommandsAwaitingProcessing = new ConcurrentLinkedDeque<String>();

	public static ConsoleUser CONSOLE;
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException, SQLException {
	
		if(args.length % 2 != 0) {
			throw new IllegalArgumentException("Must be started with an even number of arguments!");
		}
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-owner")) {
				botOwner = Long.parseLong(args[++i]);
			}
		}
		
		wiimmfi = startWiimmfi(args);
		discordBot = null;
		
		int bootAttempts = 0;
		while(discordBot == null) {
			try {
				bootAttempts++;
				discordBot = startDiscordBot(args, wiimmfi);
				discordBot.setLoading();
				discordBot.setWiimmfi(wiimmfi);
			} catch (LoginException | IOException | ErrorResponseException e) {
				LOGGER.log(Level.SEVERE, e, () -> e.getMessage());
				if(bootAttempts >= 3) {
					System.exit(-2);
				}
				Thread.sleep(5000);
			}
		}
		
		while(CONSOLE == null) {
			try {
				CONSOLE = new ConsoleUser();
			}
			catch(IOError e) {
				System.out.println(e);
				discordBot.setNoDB();
				Thread.sleep(5000);
			}
		}
		
		Throwable prevError = null;
		Instant nextWiimmfiPing = Instant.now();
		Instant nextDiscordPing = Instant.now();
		startConsole();
		
		try {
			while(true) {
				try {
					System.gc();
					Throwable error = wiimmfi.getError();
					if(nextWiimmfiPing.isBefore(Instant.now())) {
						wiimmfi.update();
						if(error == null) {
							if(prevError != null) {
								LOGGER.log(Level.SEVERE, "Error resolved.");
							}
							Wiimmfi.updateOnlinePlayers();
							
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
							updateLists(true, true);
						}
					}
					
					while(!consoleCommandsAwaitingProcessing.isEmpty()) {
						Commands.DISPATCHER.handleCommand(consoleCommandsAwaitingProcessing.pollFirst());
					}
					prevError = error;
				}
				catch(ErrorResponseException e) {
					CONSOLE.sendMessage(StacktraceUtil.getStackTrace(e));
					CONSOLE.sendMessage("An ErrorResponseException occurred... waiting 10 seconds");
				}
				Thread.sleep(1000);
			}
		}
		catch(Throwable t) {
			t.printStackTrace();
			if(discordBot != null) {
				discordBot.jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(ActivityType.DEFAULT, "Bot Crashed"));
				if(botOwner != -1) {
					try {
						DiscordUser user = DiscordUser.getDiscordUserIncludingUnknown(ConsoleContext.INSTANCE, botOwner);
						if(!(user instanceof UnknownDiscordUser)) {
							user.sendMessage(StacktraceUtil.getStackTrace(t));
						}
						else {
							CONSOLE.sendMessage(StacktraceUtil.getStackTrace(t));
						}
					}
					catch(Throwable t2) {
						CONSOLE.sendMessage(StacktraceUtil.getStackTrace(t));
					}
				}
				while(true) {Thread.sleep(1000);}
			}
			else {
				CONSOLE.sendMessage(StacktraceUtil.getStackTrace(t));
			}
		}
		System.exit(-1);
	}

	private static Wiimmfi startWiimmfi(String[] args) {
		for(int i = 0; i < args.length; i++) {
				if(args[i].equalsIgnoreCase("-wiimmfiUrl")) {
					return new Wiimmfi(args[++i]);
				}
		}
		return new Wiimmfi();
	}
	
	private static DatabaseConnection startDatabase(String[] args) throws SQLException, IOException {
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("-dbURL")) {
				return new DatabaseConnection(args[++i]);
			}
		}
		return new DatabaseConnection();
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
	
	public static Thread updateLists(boolean start, boolean join) throws InterruptedException {
		Thread listUpdater = new Thread() {
			public void run() {
				discordBot.updatePresence();
				DiscordUser.attemptRegister();
			}
		};
		if(start) {
			listUpdater.start();
			ThreadService.add(listUpdater);
			if(join) {
				listUpdater.join();
			}
		}
		return listUpdater;
	}
	
}
