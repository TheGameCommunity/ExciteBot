package com.gamebuster19901.excite.bot.command;

import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.gamebuster19901.excite.bot.server.emote.Emote;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.file.Directory;
import com.gamebuster19901.excite.util.file.File;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class CDCommand {
	
	static final HashMap<DiscordUser, Directory> DIRS = new HashMap<DiscordUser, Directory>();
	
	public static final Emote DIR = Emote.getEmote("dir");
	public static final Emote FILE = Emote.getEmote("file");
	public static final Emote RESTRICTED = Emote.getEmote("banned");
	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!cd")
			.then(Commands.argument("dir", StringArgumentType.greedyString()).executes((context) -> {
				return cd(context.getSource(), context.getArgument("dir", String.class));
			})));
		
	}
	
	@SuppressWarnings("rawtypes")
	private static int cd(MessageContext context, String dir) {
		if(context.isOperator()) {
			try {
				Directory directory;
				if(dir.startsWith(".")) {
					directory = new Directory ("./run");
					if(dir.equals("..")) {
						directory = DIRS.get(context.getAuthor()).getParentFile();
					}
					else {
						directory = DIRS.get(context.getAuthor());
						if(directory != null) {
							directory = new Directory(directory.getCanonicalPath() + File.separator + dir.substring(1, dir.length()));
						}
						else {
							directory = new Directory(".");
						}
					}
				}
				else if (dir.startsWith("~")) {
					directory = new Directory("./run" + File.separator + dir.substring(1, dir.length()));
				}
				else {
					directory = new Directory(dir);
				}
				
				if(directory.isSubDirectory(new Directory(System.getProperty("user.home")))) {
				
					DIRS.put(context.getAuthor(), directory);
					
					String message = "Current Directory: `" + DIRS.get(context.getAuthor()) + "`\n\n";
					final List<File> subFiles = Arrays.asList(directory.listFiles());
					if(directory.exists()) {
						if(directory.canRead()) {
							for(Directory subDirectory : directory.listDirectories()) {
								message = message +  "    " + DIR + File.separator + subDirectory.getName() + "\n";
							}
							message += "\n";
							File[] files = directory.listFiles();
							for(int i = 0; i < files.length; i++) {
								File file = files[i];
								if(file.isDownloadable()) {
									message = message + "(" + i + ")" + FILE + file.getName() + "\n";
								}
								else {
									message = message + "(" + i + ")" + RESTRICTED + file.getName() + "\n";
								}
							}
							context.sendMessage(message);
						}
						else {
							throw new IOException("Unable to read directory: `" + directory + "` (access denied)");
						}
					}
					else {
						throw new IOException("Unable to read directory: `" + directory + "` (nonexistant directory)");
					}
				}
				else {
					throw new IOException("Unable to read directory: `" + directory + "` (access denied)");
				}
			}
			catch(IOException | IOError e) {
				context.sendMessage(e.getMessage());
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
