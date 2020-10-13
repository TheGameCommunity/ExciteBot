package com.gamebuster19901.excite.bot.command;

import java.util.List;

import com.gamebuster19901.excite.bot.video.Video;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class VideoCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("video").executes((context) -> {
			return getVideo(context.getSource());
		}).then(Commands.argument("number", IntegerArgumentType.integer(0)).executes((context) -> {
			return getVideo(context.getSource(), context.getArgument("number", Integer.class));
		})));
	}
	
	@SuppressWarnings("rawtypes")
	private static int getVideo(MessageContext context) {
		context.sendMessage(Video.getRandomVideo() + "");
		return 1;
	}
	
	
	private static int updateDB(MessageContext context) {
		if(context.isGuildMessage() || context.isPrivateMessage()) {
			if(context.isOperator()) {
				Object o = context.getEvent();
				if(o instanceof GuildMessageReceivedEvent) {
					GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) o;
					List<Attachment> attachments = e.getMessage().getAttachments();
					if (attachments.size() != 1) {
						context.sendMessage("You must upload exactly 1 file");
					}
					else {
						Attachment attachment = attachments.get(0);
						if (attachment.getSize() > 1048576) {
							context.sendMessage("File size is too large!");
						}
						else if (attachment.getFileExtension() != ".csv"){
							context.sendMessage("The file provided is not a CSV file");
						}
						else {
							//attachment.downloadToFile(Video.VIDEO_DL).
							//Video.validateDB()
						}
					}
				}
				else if (o instanceof PrivateMessageReceivedEvent) {
					PrivateMessageReceivedEvent e = (PrivateMessageReceivedEvent) o;
				}
			}
			else {
				context.sendMessage("You do not have permission to execute this command");
			}
		}
		else {
			context.sendMessage("This command must be executed in discord");
		}
		return 1;
	}
	
	
	@SuppressWarnings("rawtypes")
	private static int getVideo(MessageContext context, int videoNumber) {
		context.sendMessage(Video.getVideo(videoNumber) + "");
		return 1;
	}
	
}
