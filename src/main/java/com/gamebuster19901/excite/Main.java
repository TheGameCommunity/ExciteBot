package com.gamebuster19901.excite;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;

import java.time.Duration;
import java.time.Instant;

import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.security.auth.login.LoginException;

import com.gamebuster19901.excite.bot.DiscordBot;
import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.database.sql.Database;
import com.gamebuster19901.excite.bot.mail.Mailbox;
import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.UnknownUser;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.gamebuster19901.excite.util.ThreadService;

import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.ConnectionIsClosedException;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public static long botOwner = -1;
	
	public static Database database;
	public static Wiimmfi wiimmfi;
	public static DiscordBot discordBot;
	
	private static ConcurrentLinkedDeque<String> consoleCommandsAwaitingProcessing = new ConcurrentLinkedDeque<String>();

	public static ConsoleUser CONSOLE = new ConsoleUser();
	public static boolean stopping = false;
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException, SQLException {
		if(args.length % 2 != 0) {
			throw new IllegalArgumentException("Must be started with an even number of arguments!");
		}
		
		LOGGER.setLevel(Level.FINEST);
		
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
		
		do {
			try {
				Database.INSTANCE = new Database();
			}
			catch(Throwable t) {
				System.out.println(t);
				discordBot.setNoDB();
				Thread.sleep(5000);
			}
		}
		while(Database.INSTANCE == null);
		
		Throwable prevError = null;
		Instant nextDiscordPing = Instant.now().minusMillis(1);
		Instant sendDiscordNotifications = Instant.now().minusMillis(1);
		startConsole();
		Thread mailThread = startMailThread();
		try {
			while(true) {
				try {
					System.gc();
					Throwable error = wiimmfi.getError();
					wiimmfi.update();
					if(error == null) {
						if(prevError != null) {
							//LOGGER.log(Level.SEVERE, "Error resolved.");
						}
					}
					else {
						if(prevError == null || !prevError.getClass().equals(error.getClass())) {
							System.out.println("Error!");
							LOGGER.log(Level.SEVERE, error, () -> error.getMessage());
						}
						prevError = error;
					}
					if(discordBot != null) {
						if(nextDiscordPing.isBefore(Instant.now())) {
							nextDiscordPing = Instant.now().plus(Duration.ofSeconds(5));
							updateLists(true, true);
						}
						if(sendDiscordNotifications.isBefore(Instant.now())) {
							sendDiscordNotifications = Instant.now().plus(Duration.ofSeconds(4));
							DiscordUser.notifyDiscordUsers();
						}
					}
					
					while(!consoleCommandsAwaitingProcessing.isEmpty()) {
						Commands.DISPATCHER.handleCommand(consoleCommandsAwaitingProcessing.pollFirst());
					}
				}
				catch(ErrorResponseException e) {
					CONSOLE.sendMessage(StacktraceUtil.getStackTrace(e));
					CONSOLE.sendMessage("An ErrorResponseException occurred... waiting 10 seconds");
				}
				catch(Throwable t) {
					if(t != null && (t instanceof ConnectionIsClosedException || t instanceof CommunicationsException || t instanceof CJCommunicationsException || t instanceof SQLNonTransientConnectionException || (t.getCause() != null && (t.getCause() instanceof IOException || t.getCause() instanceof SQLException || t.getCause() instanceof IOError)))) {
						System.err.println("Attempting to recover from database connection failure...");
						Database.INSTANCE.close();
						Database.INSTANCE = null;
						while(Database.INSTANCE == null) {
							discordBot.setNoDB();
							try {
								Database.INSTANCE = new Database();
							}
							catch(Throwable t2) {
								System.err.println("Attempting to recover from database connection failure...");
								t2.printStackTrace(System.err);
								Database.INSTANCE = null;
							}
							Thread.sleep(5000);
						}
						continue;
					}
					throw t;
				}
				if(!mailThread.isAlive() && !stopping) {
					throw new Error("Mail thread has died");
				}
				Thread.sleep(1000);
			}
		}
		catch(Throwable t) {
			t.printStackTrace();
			if(discordBot != null) {
				discordBot.jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(ActivityType.WATCHING, "Bot Crashed"));
				if(botOwner != -1) {
					try {
						User user = DiscordUser.getUser(botOwner);
						if(!(user instanceof UnknownUser)) {
							DiscordUser.sendMessage(user, StacktraceUtil.getStackTrace(t));
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
	
	private static Database startDatabase(String[] args) throws SQLException, IOException {
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("-dbURL")) {
				return new Database(args[++i]);
			}
		}
		return new Database();
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
	
	private static Thread startMailThread() {
		Thread mailThread = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						Mailbox.receive();
						Thread.sleep(5000);
					}
					catch(InterruptedException e) {
						break;
					}
					catch(MessagingException e) {
						e.printStackTrace(); //We don't want to crash if a message simply fails to parse
					}
					catch(Throwable t) {
						throw new RuntimeException(t);
						//t.printStackTrace();
					}
				}
			}
		};
		mailThread.setContextClassLoader(null);
		mailThread.setDaemon(false);
		ThreadService.run("mailThread", mailThread);
		return mailThread;
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
			ThreadService.add("listUpdater", listUpdater);
			if(join) {
				listUpdater.join();
			}
		}
		return listUpdater;
	}
	
}
