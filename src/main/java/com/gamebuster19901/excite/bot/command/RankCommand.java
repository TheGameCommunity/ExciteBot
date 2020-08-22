package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class RankCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!rank")
				.then(Commands.literal("add")
						.then(Commands.argument("rank", StringArgumentType.word())
								.then(Commands.argument("user", StringArgumentType.greedyString())
									.executes(context -> {
										return addRank(context.getSource(), context.getArgument("user", String.class), context.getArgument("rank", String.class));
									})
								)
						)
				).then(Commands.literal("remove")
						.then(Commands.argument("rank", StringArgumentType.word())
								.then(Commands.argument("user", StringArgumentType.greedyString())
									.executes(context -> {
										return removeRank(context.getSource(), context.getArgument("user", String.class), context.getArgument("rank", String.class));
									})
								)
						)
				)
		);
	}
	
	@SuppressWarnings("rawtypes")
	private static int addRank(MessageContext context, String user, String rank) {
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
	private static int removeRank(MessageContext context, String user, String rank) {
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
	private static int addAdmin(MessageContext context, String user) {
		DiscordUser discordUser;
		try {
			discordUser = DiscordUser.getDiscordUserIncludingUnknown(Long.parseLong(user));
		}
		catch(NumberFormatException e) {
			discordUser = DiscordUser.getDiscordUserIncludingUnknown(user);
		}
		
		if(discordUser instanceof UnknownDiscordUser) {
			context.sendMessage("Unknown user: " + user);
			return 1;
		}
		
		if(discordUser.isAdmin()) {
			context.sendMessage(user + " is already a bot admin");
		}
		else {
			discordUser.setAdmin(context, true);
			context.sendMessage(discordUser.toDetailedString() + " is now a bot administrator for " + Main.discordBot.getSelfUser().getAsTag());
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int removeAdmin(MessageContext context, String user) {
		DiscordUser discordUser;
		try {
			discordUser = DiscordUser.getDiscordUserIncludingUnknown(Long.parseLong(user));
		}
		catch(NumberFormatException e) {
			discordUser = DiscordUser.getDiscordUser(user);
		}
		
		if(discordUser instanceof UnknownDiscordUser) {
			context.sendMessage("Unknown user: " + user);
			return 1;
		}
		
		if(!discordUser.isAdmin()) {
			context.sendMessage(user + " is already not an admin");
		}
		else {
			if(discordUser.isOperator()) {
				removeOperator(context, user);
			}
			discordUser.setAdmin(context, false);
			context.sendMessage(discordUser.toDetailedString() + " is no longer a bot administrator for " + Main.discordBot.getSelfUser().getAsTag());
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int addOperator(MessageContext context, String user) {
		DiscordUser discordUser;
		try {
			discordUser = DiscordUser.getDiscordUserIncludingUnknown(Long.parseLong(user));
		}
		catch(NumberFormatException e) {
			discordUser = DiscordUser.getDiscordUser(user);
		}
		
		if(discordUser instanceof UnknownDiscordUser) {
			context.sendMessage("Unknown user: " + user);
			return 1;
		}
		
		if(discordUser.isOperator()) {
			context.sendMessage(user + " is already a bot operator");
		}
		else {
			discordUser.setOperator(context, true);
			context.sendMessage(discordUser.toDetailedString() + " is now a bot operator for " + Main.discordBot.getSelfUser().getAsTag());
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int removeOperator(MessageContext context, String user) {
		DiscordUser discordUser;
		try {
			discordUser = DiscordUser.getDiscordUserIncludingUnknown(Long.parseLong(user));
		}
		catch(NumberFormatException e) {
			discordUser = DiscordUser.getDiscordUser(user);
		}
		
		if(discordUser instanceof UnknownDiscordUser) {
			context.sendMessage("Unknown user: " + user);
			return 1;
		}
		
		if(!discordUser.isOperator()) {
			context.sendMessage(user + " is already not a bot operator");
		}
		else {
			discordUser.setOperator(context, false);
			context.sendMessage(discordUser.toDetailedString() + " is no longer a bot operator for " + Main.discordBot.getSelfUser().getAsTag());
		}
		return 1;
	}
	
}
