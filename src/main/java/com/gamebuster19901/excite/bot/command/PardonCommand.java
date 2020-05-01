package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class PardonCommand extends WiimmfiCommand{

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!pardon").then(Commands.argument("discordUser", StringArgumentType.string()).then(Commands.argument("discriminator", StringArgumentType.string()).executes((context) -> {
			return pardon(context.getSource(), context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class), 1);
		}).then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((context) -> {
			return pardon(context.getSource(), context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class), context.getArgument("count", Integer.class));
		})))));
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(MessageContext context, String username, String discriminator, int count) {
		if(context.isAdmin()) {
			DiscordUser user = DiscordUser.getDiscordUser(username + "#" + discriminator);
			if(user == null) {
				context.sendMessage("Could not find user " + username + "#" + discriminator);
			}
			user.pardon(count);
			context.sendMessage("Pardoned " + user.getJDAUser().getAsTag());
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
