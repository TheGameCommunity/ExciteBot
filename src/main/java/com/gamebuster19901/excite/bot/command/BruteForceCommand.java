package com.gamebuster19901.excite.bot.command;

import static com.gamebuster19901.excite.bot.command.ArchiveCommand.WorkerStatus.*;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;

import com.gamebuster19901.excite.bot.command.ArchiveCommand.WorkerStatus;
import com.gamebuster19901.excite.game.crc.CRCTester;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.gamebuster19901.excite.util.ThreadService;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class BruteForceCommand {
	private static final Duration FIVE_MINUTES = Duration.ofMinutes(5);
	public static final boolean[] DAYS_HOURS_AND_MINUTES = new boolean[] {true, false, false, true, true, true, false};
	public static final boolean[] MINUTES_AND_SECONDS = new boolean[] {false, false, false, false, false, true, true};
	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("bruteForce")
				.then(Commands.argument("seed", IntegerArgumentType.integer(0))
					.then(Commands.argument("expectedCRC", IntegerArgumentType.integer())
						.then(Commands.argument("file", StringArgumentType.greedyString())
							.executes(context -> {
								return bruteForce(context.getSource(), context.getArgument("file", String.class), context.getArgument("seed", int.class), context.getArgument("expectedCRC", int.class));
							})
						)
					)
				)
		);
	}
	
	@SuppressWarnings("rawtypes")
	private static int bruteForce(MessageContext context, String fileToCheck, int start, int expected) {
		if(true == true) {
			context.sendMessage("This command has been disabled. Update the code.");
			return 1;
		}
		if(context.isConsoleMessage()) {
			context.sendMessage("You must execute this command in discord.");
		}
		else if(context.isOperator()) {
			Thread monitorThread = ThreadService.run("CRCMonitor", new Thread() {
				private final Thread monitorThread = this;
				private volatile AtomicInteger currentSeed = new AtomicInteger(start);
				private volatile AtomicInteger matches = new AtomicInteger(0);
				private volatile Thread[] workerThreads = new Thread[Runtime.getRuntime().availableProcessors()];
				public volatile byte updates = 0;
				public volatile WorkerStatus currentStatus = NOT_STARTED;
				
				public void run() {
					Thread messageUpdater = ThreadService.run("Brute Force monitor", new Thread() { public void run(){
						Instant bruteForceStart = Instant.now();
						EmbedBuilder embed = new EmbedBuilder();
						embed.setTitle("CRC brute force in progress...");
						Field fileField = new Field("File:", fileToCheck, true, true);
						Field seedsCheckedField = new Field("Seeds checked:", "0", true, true);
						Field currentSeedField = new Field("Current seed:", currentSeed.get() + "", true, true);
						Field seedsRemainingField = new Field("Seeds Remaining:", Integer.MAX_VALUE - currentSeed.get() + "", true, true);
						Field seedsPerSecondField = new Field("Seeds Per Second:", "0", true, true);
						Field matchesField = new Field("Matches:", "0", true, true);
						Field activeThreadsField = new Field("Threads active:", "0", true, true);
						Field timeElapsedField = new Field("Time elapsed:", "0 seconds", true, true);
						Field timeRemainingField = new Field("Time remaining:", "forever", true, true);
						embed.setTimestamp(bruteForceStart);
						embed.addField(fileField);
						embed.addField(seedsCheckedField);
						embed.addField(currentSeedField);
						embed.addField(seedsRemainingField);
						embed.addField(seedsPerSecondField);
						embed.addField(matchesField);
						embed.addField(activeThreadsField);
						embed.addField(timeElapsedField);
						embed.addField(timeRemainingField);
						Message message = context.sendMessage(embed.build());
						int prev = currentSeed.get();
						try {
							
							
							File file = new File(fileToCheck);
							if(!file.exists()) {
								throw new NoSuchFileException(context.getMention() + ", the file `" + file + "` does not exist!");
							}
							if(!file.canRead()) {
								throw new IOException(context.getMention() + ", I do not have permission to read `" + file + "`");
							}
							if(file.isDirectory()) {
								throw new IOException(context.getMention() + ", `" + file + "` is a directory, not a file...");
							}
							
							FileInputStream attachment = new FileInputStream(file);
							byte[] bytes = IOUtils.toByteArray(attachment);
							attachment.close();
							
							for(int i = 0; i < workerThreads.length; i++) {
								Thread worker = ThreadService.run("BRUTE-FORCER-" + i, () -> {
									CRCTester tester = new CRCTester(Base64.getMimeDecoder().decode(bytes));
									try {
										while(!currentStatus.finished()) {
											int seed = incrementSeed();
											/*if(tester.test(seed) == expected) {
												context.sendMessage(context.getMention() + ": " + Thread.currentThread().getName() + " found possible CRC seed: " + Integer.toHexString(seed));
												matches.incrementAndGet();
											}*/
										}
									}
									catch(Throwable t) {
										context.sendMessage(context.getMention() + " " + Thread.currentThread() + " died!");
										context.sendMessage(StacktraceUtil.getStackTrace(t));
									}
								});
								workerThreads[i] = worker;
							}
							
							currentStatus = WORKING;
							while(true) {
								prev = currentSeed.get();
								Thread.sleep(1000);
								Instant now = Instant.now();
								if(currentStatus == NOT_STARTED) {
									embed.setColor(Color.DARK_GRAY);
								}
								if(currentStatus == WORKING) {
									embed = new EmbedBuilder();
									if(updates++ % 2 == 0) {
										embed.setColor(Color.yellow);
									}
									else {
										embed.setColor(new Color(204, 204, 0)); //dark yellow
									}
									embed.setTitle("CRC brute force in progress...");
									updateEmbed(embed, prev, bruteForceStart);
								}
								message.editMessage(embed.build()).complete();
								if(currentStatus.finished()) {
									break;
								}
							}
							
							if(currentStatus == COMPLETE) {
								embed = new EmbedBuilder();
								embed.setTitle("Brute Force Complete");
								embed.setColor(Color.GREEN);
								context.sendMessage(context.getMention() + " Brute force complete, there were " + matches + " possible matching seeds");
								updateEmbed(embed, prev, bruteForceStart);
								message.editMessage(embed.build()).complete();
							}
							
						}
						catch(InterruptedException e) {
							currentStatus = CANCELLED;
							embed = new EmbedBuilder();
							embed.setTitle("CRC brute force cancelled");
							embed.setColor(Color.DARK_GRAY);
							updateEmbed(embed, prev, bruteForceStart);
							message.editMessage(embed.build()).complete();
						}
						catch(Throwable t) {
							currentStatus = ERRORED;
							embed.setTitle("CRC brute force failed");
							embed.setColor(Color.RED);
							try {
								message.editMessage(embed.build()).complete();
							}
							catch(Throwable t2) {}
							System.out.println("Monitor thread encountered an exception, stopping!");
							context.sendMessage(context.getMention() + ", An exception occured while brute forcing:");
							context.sendMessage(StacktraceUtil.getStackTrace(t));
							return;
						}
						
					}});
					
				}
				
				int incrementSeed() {
					return currentSeed.getAndAccumulate(1, (a, b) -> {if (a + b < 0) {currentStatus = COMPLETE; return a;} else {return a + b;}});
				}
				
				public void updateEmbed(EmbedBuilder embed, int prev, Instant bruteForceStart) {
					Field fileField = new Field("File:", fileToCheck, true, true);
					Field seedsCheckedField = new Field("Seeds checked:", "0", true, true);
					Field currentSeedField = new Field("Current seed:", currentSeed.get() + "", true, true);
					Field seedsRemainingField = new Field("Seeds Remaining:", Integer.MAX_VALUE - currentSeed.get() + "", true, true);
					Field seedsPerSecondField = new Field("Seeds Per Second:", "0", true, true);
					Field matchesField = new Field("Matches:", "0", true, true);
					Field activeThreadsField = new Field("Threads active:", "0", true, true);
					Field timeElapsedField = new Field("Time elapsed:", "0 seconds", true, true);
					Field timeRemainingField = new Field("Time remaining:", "forever", true, true);
					Instant now = Instant.now();
					embed.addField(fileField);
					embed.addField(seedsCheckedField = new Field("Seeds checked:", (currentSeed.get() - start) + "", true, true));
					embed.addField(currentSeedField = new Field("Current seed:", currentSeed + "", true, true));
					int seedsRemaining = Integer.MAX_VALUE - currentSeed.get();
					embed.addField(seedsRemainingField = new Field("Seeds Remaining:", seedsRemaining + "", true, true));
					int seedsPerSecond = (int) ((currentSeed.get() - prev));
					embed.addField(seedsPerSecondField = new Field("Seeds Per Second:", seedsPerSecond + "", true, true));
					embed.addField(matchesField = new Field("Matches:", matches + "", true, true));
					int alive = 0;
					for(Thread t : workerThreads) {
						if(t != null && t.isAlive()) {
							alive++;
						}
					}
					embed.addField(activeThreadsField = new Field("Threads active:", alive + "", true, true));
					Duration timeElapsed = Duration.between(bruteForceStart, now);
					boolean[] timeElapsedUnits;
					if(timeElapsed.compareTo(FIVE_MINUTES) >= 0) {
						timeElapsedUnits = DAYS_HOURS_AND_MINUTES;
					}
					else {
						timeElapsedUnits = MINUTES_AND_SECONDS;
					}
					embed.addField(timeElapsedField = new Field("Time elapsed:", TimeUtils.readableDuration(Duration.between(bruteForceStart, now), timeElapsedUnits), true, true));
					
					boolean[] timeRemainingUnits;
					Duration timeRemaining = TimeUtils.FOREVER;
					if(seedsPerSecond > 0) { 
						timeRemaining = Duration.ofSeconds(seedsRemaining / seedsPerSecond);
					}
					if(timeRemaining.compareTo(FIVE_MINUTES) >= 0) {
						timeRemainingUnits = DAYS_HOURS_AND_MINUTES;
					}
					else {
						timeRemainingUnits = MINUTES_AND_SECONDS;
					}
					embed.addField(timeRemainingField = new Field("Time remaining:", "Approximately " + TimeUtils.readableDuration(timeRemaining, timeRemainingUnits), true, true));
					if(currentStatus == WORKING && alive == 0) {
						throw new Error("All brute forcers have died!");
					}
				}
				
			});
		}
		else {
			context.sendMessage("You do not have permission to execute that command.");
		}
		return 1;
	}
	
}
