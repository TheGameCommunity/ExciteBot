package com.gamebuster19901.excite.bot.command;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.gamebuster19901.excite.bot.mail.Mailbox;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.gamebuster19901.excite.util.ThreadService;
import com.gamebuster19901.excite.util.TimeUtils;
import com.gamebuster19901.excite.util.file.FileUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;

import static com.gamebuster19901.excite.bot.command.ArchiveCommand.WorkerStatus.*;

public class ArchiveCommand {
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		dispatcher.register(Commands.global("archive").then(Commands.argument("channels", StringArgumentType.greedyString()).executes((context) -> {
			return archive(context.getSource(), context.getArgument("channels", String.class));
		})));
	}

	private static int archive(CommandContext source, String argument) {
		if(source.isAdmin()) {
			if(source.isGuildContext()) {
				if(argument.isEmpty()) {
					source.sendMessage("Usage: archive <TextChannels>");
					return 1;
				}
				Guild guild = source.getServer();
				Member member = DiscordUser.getMember((User) source.getAuthor(), guild);
				HashSet<TextChannel> channelsToArchive = getChannels(source, guild, Arrays.asList(argument.split(" ")));
				for(TextChannel channel : channelsToArchive) {
					if(!member.hasPermission(channel, Permission.MANAGE_CHANNEL, Permission.MESSAGE_MANAGE)) {
						source.sendMessage("You must have the `MANAGE_CHANNEL` and `MESSAGE_MANAGE` permission in " + channel.getAsMention() + " in order to archive it.");
						return 1;
					}
				}
				Thread workerThread = ThreadService.run("Archiver thread", new Thread() {
					private final Thread workerThread = this;
					private final int newlineSize = System.lineSeparator().length();
					public volatile WorkerStatus status = NOT_STARTED;
					public volatile byte updates = 0;
					public volatile int messagesArchived = 0;
					public volatile int wiiMessagesArchived = 0;
					public volatile int attachmentsArchived = 0;
					public volatile long estimatedSize = 0;
					private volatile Throwable error;
					
					@Override
					public void run() {
						Thread monitorThread = ThreadService.run("Archive monitor", new Thread() { public void run(){
							Instant start = Instant.now();
							EmbedBuilder embed = new EmbedBuilder();
							embed.setTitle("Archive in progress...");
							Field archivedDiscordMessagesField = new Field("Discord Messages archived:", "0", true, true);
							Field archivedDiscordAttachmentsField = new Field("Discord Attachments archived:", "0", true, true);
							Field emailsArchivedField = new Field("Wii Mails archived:", "0", true, true);
							Field estimatedSizeField = new Field("Estimated size:", FileUtils.humanReadableByteCount(0), true, true);
							Field timeElapsedField = new Field("Time elapsed:", "0 seconds", true, true);
							embed.setTimestamp(start);
							WorkerStatus currentStatus = status;
							embed.addField(archivedDiscordMessagesField);
							embed.addField(archivedDiscordAttachmentsField);
							embed.addField(emailsArchivedField);
							embed.addField(estimatedSizeField);
							embed.addField(timeElapsedField);
							InteractionHook message = source.sendMessage(embed);
							while(!currentStatus.finished()) {
								try {
									Thread.sleep(2500);
									currentStatus = status;
									Instant now = Instant.now();
									embed = new EmbedBuilder();
									if(currentStatus == NOT_STARTED) {
										embed.setColor(Color.DARK_GRAY);
									}
									if(currentStatus == WORKING) {
										if(updates++ % 2 == 0) {
											embed.setColor(Color.YELLOW);
										}
										else {
											embed.setColor(new Color(204, 204, 0)); //dark yellow
										}
										embed.setTitle("Archive in progress...");
										embed.addField(archivedDiscordMessagesField = new Field("Discord Messages archived:", messagesArchived + "", true, true));
										embed.addField(archivedDiscordAttachmentsField = new Field("Discord Attachments archived:", attachmentsArchived +"", true, true));
										embed.addField(emailsArchivedField = new Field("Wii Messages archived:", wiiMessagesArchived + "", true, true));
										embed.addField(estimatedSizeField = new Field("Estimated size:", FileUtils.humanReadableByteCount(estimatedSize), true, true));
										embed.addField(timeElapsedField = new Field ("Time elapsed:", TimeUtils.readableDuration(Duration.between(start, now)), true, true));
										embed.setTimestamp(now);
										message.editMessageEmbedsById("@original", embed.build()).complete();
									}
									Thread.sleep(2500);
								} catch (InterruptedException e) {
									currentStatus = ERRORED;
									embed.setTitle("Archive failed");
									embed.setColor(Color.RED);
									message.editMessageEmbedsById("@original", embed.build()).complete();
									System.out.println("Monitor thread interrupted... stopping!");
									return;
								}
							}
							Instant now = Instant.now();
							embed.addField(archivedDiscordMessagesField = new Field("Discord Messages archived:", messagesArchived + "", true, true));
							embed.addField(archivedDiscordAttachmentsField = new Field("Discord Attachments archived:", attachmentsArchived +"", true, true));
							embed.addField(emailsArchivedField = new Field("Wii Messages archived:", wiiMessagesArchived + "", true, true));
							embed.addField(estimatedSizeField = new Field("Estimated size:", FileUtils.humanReadableByteCount(estimatedSize), true, true));
							embed.addField(timeElapsedField = new Field ("Time elapsed:", TimeUtils.readableDuration(Duration.between(start, now)), true, true));
							embed.setTimestamp(now);
							message.editMessageEmbedsById("@original", embed.build()).complete();
							if(currentStatus == COMPLETE) {
								embed.setColor(Color.GREEN);
								embed.setTitle("Archive complete");
								source.sendMessage(source.getAuthor().getAsMention() + " Archive complete.");
							}
							else {
								embed.setColor(Color.RED);
								embed.setTitle("Archive failed");
								source.sendMessage(source.getAuthor().getAsMention() + " Archive FAILED.");
							}
							message.editMessageEmbedsById("@original", embed.build()).complete();
						}});
						
						String date = TimeUtils.getDBDate(Instant.now());
						try {
							status = WORKING;
							for(File f : org.apache.commons.io.FileUtils.listFiles(Mailbox.MAILBOX, null, true)) {
								File archive = new File(".archive/" + date + "/" + f.getPath().replace("/run", ""));
								if(!archive.getParentFile().mkdirs() && !archive.getParentFile().exists()) {
									throw new IOException("Could not create " + archive.getAbsolutePath());
								}
								org.apache.commons.io.FileUtils.copyFile(f, archive);
								wiiMessagesArchived++;
								estimatedSize += archive.length();
							}
							for(TextChannel channel : channelsToArchive) {
								try {
									File file = new File(".archive/" + date + "/" + channel.getName() + "/" + channel.getName() + ".arc");
									if(!file.getParentFile().mkdirs()) {
										
										throw new IOException("Could not create " + file.getAbsolutePath());
									}
									System.out.println(file.getAbsolutePath());
									file.createNewFile();
									MessagePaginationAction action = channel.getIterableHistory();
									BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
									action.forEach((message) -> {
										try {
											List<Attachment> attachments = message.getAttachments();
											write(fileWriter, "==========START " + message.getIdLong() + " USER:" + message.getAuthor().getAsTag());
											write(fileWriter, message.getContentRaw());
											if(!attachments.isEmpty()) {
												write(fileWriter, "==========ATTACHMENTS " + message.getIdLong());
												for(Attachment attachment : attachments) {
													File attachmentFile = new File(file.getParentFile().getPath() + "/attach" + attachment.getIdLong() + attachment.getFileName());
													CompletableFuture<File> future = attachment.downloadToFile(attachmentFile);
													future.get();
													write(fileWriter, attachment.getId() + attachmentFile.getName());
													estimatedSize += attachment.getSize();
													attachmentsArchived++;
												}
											}
											write(fileWriter, "==========END " + message.getIdLong());
											messagesArchived++;
										} catch (IOException | InterruptedException | ExecutionException e) {
											throw new RuntimeException(e);
										}
									});
									fileWriter.close();
								}
								catch(Throwable t) {
									source.sendMessage("Could not back up channel " + channel.getAsMention());
									throw t;
								}
							}
							status = COMPLETE;
						} catch (Throwable t) {
							source.sendMessage(StacktraceUtil.getStackTrace(t));
							status = ERRORED;
						}
					}
					
					private void write(BufferedWriter writer, String text) throws IOException {
						estimatedSize += text.length() + newlineSize;
						writer.write(text);
						writer.newLine();
					}
					
				});
			}
			else {
				source.sendMessage("You must execute this command in a server");
			}
		}
		else {
			source.sendMessage("You must be an administator to execute this command");
		}
		return 1;
	}
	
	private static HashSet<TextChannel> getChannels(CommandContext source, Guild guild, List<String> names) {
		HashSet<TextChannel> channels = new HashSet<TextChannel>();
		List<TextChannel> serverChannels = guild.getTextChannels();
		TextChannel channel = null;
		for(String name : names) {
			if(name.length() > 1) {
				if(name.startsWith("<#")){
					name = name.substring(2, name.length() - 1);
					channel = guild.getTextChannelById(name);
					if(channel != null) {
						channels.add(channel);
					}
					else {
						source.sendMessage("Could not find channel " + name);
					}
				}
				else {
					source.sendMessage("Please use channel mentions, `" + name + "` is not a mention");
				}
			}
		}
		return channels;
	}
	
	static enum WorkerStatus {
		NOT_STARTED,
		WORKING,
		COMPLETE,
		CANCELLED,
		ERRORED;
		
		public boolean finished() {
			return this == COMPLETE || this == ERRORED || this == CANCELLED;
		}
	}
	
}
