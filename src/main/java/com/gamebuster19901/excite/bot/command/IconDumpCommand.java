package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.server.DiscordServer;
import com.gamebuster19901.excite.bot.server.UnloadedDiscordServer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;

import net.dv8tion.jda.api.entities.Emote;

public class IconDumpCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!icondump").then(Commands.argument("server", LongArgumentType.longArg()).executes((command) -> {
				return sendResponse(command.getSource(), command.getArgument("server", Long.class));
			}
		)));
	}
		
	@SuppressWarnings("rawtypes")
	public static int sendResponse(MessageContext context, long serverId) {
		if(context.isOperator()) {
			DiscordServer server = DiscordServer.getServer(serverId);
			if(server != null && !(server instanceof UnloadedDiscordServer)) {
				String ret = "WhAT dO thEY MEaN?\n\n";
				for(Emote emote : server.getGuild().getEmotes()) {
					ret += emote.getAsMention();
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
