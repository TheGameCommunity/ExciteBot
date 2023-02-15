package com.gamebuster19901.excite.util;

import java.util.concurrent.ConcurrentLinkedDeque;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.CommandContext;

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
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						for(Thread t : threads) {
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
	
	public static Thread run(String name, Runnable runnable) {
		Thread thread = new Thread(runnable);
		return run(name, thread);
	}
	
	public static Thread run(String name, Thread thread) {
		thread.setName(name);
		if(!thread.isAlive()) {
			thread.start();
			threads.add(thread);
		}
		return thread;
	}
	
	public static void add(String name, Thread thread) {
		thread.setName(name);
		Thread.State state = thread.getState();
		if(state == Thread.State.NEW) {
			throw new IllegalStateException("Thread " + thread + "has not started!");
		}
		threads.add(thread);
	}
	
	@SuppressWarnings("rawtypes")
	public static void shutdown(CommandContext context, int ExitCode) {
		Main.stopping = true;
		Thread shutdownHandler = new Thread() {
			@Override
			public void run() {
				
				int threadCount;
				int waitTime = 0;
				do {
					for(Thread t : threads) {
						t.interrupt();
					}
					threadCount = threads.size();
					if(threadCount == 0) {
						break;
					}
					String message = "==========\nWaiting for " + threadCount + " thread(s) to finish:\n\n";
					for(Thread t : threads) {
						message = message + t.getName() + " - " + t.getState() + "\n";
					}
					message = message + "==========";
					context.sendMessage(message);
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
						if(context.getAuthor() != Main.CONSOLE) {
							Main.CONSOLE.sendMessage(stacktrace);
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
