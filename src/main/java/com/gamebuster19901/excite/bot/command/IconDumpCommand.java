package com.gamebuster19901.excite.bot.command;

import java.util.List;

import com.gamebuster19901.excite.Main;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

public class IconDumpCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		dispatcher.register(Commands.literal("icondump").executes((context) -> {
			context.getSource().sendMessage("Provide a server id");
			return 0;
		}).then(Commands.argument("server", LongArgumentType.longArg()).executes((command) -> {
				return sendResponse(command.getSource(), command.getArgument("server", Long.class));
			}
		)));
	}
		
	@SuppressWarnings("rawtypes")
	public static int sendResponse(CommandContext context, long serverId) {
		if(context.isOperator()) {
			Guild server = Main.discordBot.jda.getGuildById(serverId);
			if(server != null && server.isLoaded()) {
				List<RichCustomEmoji> emotes = server.getEmojis();
				String ret = server.getName() + " has " + emotes.size() + " emotes:\n\n";
				for(RichCustomEmoji emote : emotes) {
					ret += emote.getName() + ": " + emote.getAsMention() + "\n";
				}
				context.sendMessage(ret);
			}
			else {
				context.sendMessage("Could not find a server with id " + serverId);
			}
		}
		else {
			context.sendMessage("You don't have permission to execute this command");
		}
		return 0;
	}
	
}
