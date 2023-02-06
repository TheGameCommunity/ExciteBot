package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.command.argument.DiscordUserArgumentType;
import com.gamebuster19901.excite.bot.command.argument.DiscordUserArgumentType.UnknownType;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class RankCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		dispatcher.register(Commands.literal("rank")
				.then(Commands.literal("add")
						.then(Commands.argument("rank", StringArgumentType.word())
								.then(Commands.argument("user", DiscordUserArgumentType.user().setUnknown(UnknownType.FULLY_KNOWN))
									.executes(context -> {
										return addRank(context.getSource(), context.getArgument("user", DiscordUser.class), context.getArgument("rank", String.class));
									})
								)
						)
				).then(Commands.literal("remove")
						.then(Commands.argument("rank", StringArgumentType.word())
								.then(Commands.argument("user", DiscordUserArgumentType.user().setUnknown(UnknownType.FULLY_KNOWN))
									.executes(context -> {
										return removeRank(context.getSource(), context.getArgument("user", DiscordUser.class), context.getArgument("rank", String.class));
									})
								)
						)
				)
		);
	}
	
	@SuppressWarnings("rawtypes")
	private static int addRank(CommandContext context, DiscordUser user, String rank) {
		if(context.isOperator()) {
			if(rank.equalsIgnoreCase("admin")) {
				return addAdmin(context, user);
			}
			else if (rank.equalsIgnoreCase("operator") || rank.equalsIgnoreCase("op")) {
				return addOperator(context, user);
			}
		}
		else {
			context.sendMessage("You do not have permission to execute that command.");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int removeRank(CommandContext context, DiscordUser user, String rank) {
		if(context.isOperator()) {
			if(rank.equalsIgnoreCase("admin")) {
				return removeAdmin(context, user);
			}
			else if (rank.equalsIgnoreCase("operator") || rank.equalsIgnoreCase("op")) {
				return removeOperator(context, user);
			}
		}
		else {
			context.sendMessage("You do not have permission to execute that command.");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int addAdmin(CommandContext context, DiscordUser user) {		
		if(user instanceof UnknownDiscordUser) {
			context.sendMessage("Unknown user: " + user);
			return 1;
		}
		
		if(user.isAdmin()) {
			context.sendMessage(user + " is already a bot admin");
		}
		else {
			user.setAdmin(context, true);
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int removeAdmin(CommandContext context, DiscordUser user) {
		
		if(user instanceof UnknownDiscordUser) {
			context.sendMessage("Unknown user: " + user);
			return 1;
		}
		
		if(!user.isAdmin()) {
			context.sendMessage(user + " is already not an admin");
		}
		else {
			user.setAdmin(context, false);
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int addOperator(CommandContext context, DiscordUser user) {
		
		if(user instanceof UnknownDiscordUser) {
			context.sendMessage("Unknown user: " + user);
			return 1;
		}
		
		if(user.isOperator()) {
			context.sendMessage(user + " is already a bot operator");
		}
		else {
			user.setOperator(context, true);
		}

		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int removeOperator(CommandContext context, DiscordUser user) {
		if(user instanceof UnknownDiscordUser) {
			context.sendMessage("Unknown user: " + user);
			return 1;
		}
		
		if(!user.isOperator()) {
			context.sendMessage(user + " is already not a bot operator");
		}
		else {
			user.setOperator(context, false);
		}
		return 1;
	}
	
}
