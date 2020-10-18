package com.gamebuster19901.excite.util;

import java.util.concurrent.ConcurrentLinkedDeque;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.user.ConsoleUser;

public class ThreadService {

	static {
		Thread updateThread = new Thread() {
			@Override
			public void run() {
				while(true) {
					for(Thread t : threads) {
						if(!t.isAlive()) {
							threads.remove(t);
						}
					}
				}
			}
		};
		updateThread.setDaemon(true);
		updateThread.setName("ThreadService");
		updateThread.start();
	}
	
	private static ConcurrentLinkedDeque<Thread> threads = new ConcurrentLinkedDeque<Thread>();
	
	public static void run(Runnable runnable) {
		Thread thread = new Thread(runnable);
		run(thread);
	}
	
	public static void run(Thread thread) {
		if(!thread.isAlive()) {
			thread.start();
			threads.add(thread);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void shutdown(MessageContext context) {
		Thread shutdownHandler = new Thread() {
			@Override
			public void run() {
				
				try {
					ThreadService.run(Main.updateFiles(false, false));
				} catch (InterruptedException e1) {
					throw new AssertionError(e1);
				}
				
				int threadCount;
				int waitTime = 0;
				do {
					threadCount = threads.size();
					if(threadCount == 0) {
						break;
					}
					context.sendMessage("Waiting for " + threadCount + " threads to finish...");
					try {
						Thread.sleep(1000);
						waitTime = waitTime + 1000;
						if(waitTime > 5000) {
							throw new InterruptedException("Took to long to stop!");
						}
					} catch (InterruptedException e) {
						context.sendMessage("Iterrupted, Emergency Stop!");
						String stacktrace = StacktraceUtil.getStackTrace(e);
						context.sendMessage(stacktrace);
						if(context.getDiscordAuthor() != ConsoleUser.INSTANCE) {
							ConsoleUser.INSTANCE.sendMessage(stacktrace);
						}
						break;
					}
				}
				while(threadCount > 0);
				context.sendMessage("Stopped!");
				Main.discordBot.jda.shutdown();
				System.exit(0);
			}
		};
		shutdownHandler.start();
	}
	
}
