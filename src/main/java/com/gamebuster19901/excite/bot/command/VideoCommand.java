package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.video.Video;
import com.mojang.brigadier.CommandDispatcher;

public class VideoCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!video").executes((context) -> {
			return getVideo(context.getSource());
		}));
	}
	
	@SuppressWarnings("rawtypes")
	private static int getVideo(MessageContext context) {
		context.sendMessage(Video.getRandomVideo() + "");
		return 1;
	}
	
}
