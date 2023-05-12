package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.command.argument.DiscordUserArgumentType;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.dv8tion.jda.api.entities.User;

public class RankCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		dispatcher.register(Commands.userGlobal("rank")
				.then(Commands.literal("add")
						.then(Commands.argument("rank", StringArgumentType.word())
								.then(Commands.argument("user", new DiscordUserArgumentType())
									.executes(context -> {
										return addRank(context.getSource(), context.getArgument("user", User.class), context.getArgument("rank", String.class));
									})
								)
						)
				).then(Commands.literal("remove")
						.then(Commands.argument("rank", StringArgumentType.word())
								.then(Commands.argument("user", new DiscordUserArgumentType())
									.executes(context -> {
										return removeRank(context.getSource(), context.getArgument("user", User.class), context.getArgument("rank", String.class));
									})
								)
						)
				)
		);
	}
	
	@SuppressWarnings("rawtypes")
	private static int addRank(CommandContext context, User user, String rank) {
		if(context.isOperator()) {
			if(rank.equalsIgnoreCase("admin")) {
				return addAdmin(context, user);
			}
			else if (rank.equalsIgnoreCase("operator") || rank.equalsIgnoreCase("op")) {
				return addOperator(context, user);
			}
			else if (rank.equalsIgnoreCase("tester")) {
				return addTester(context, user);
			}
		}
		else {
			context.replyMessage("You do not have permission to execute that command.");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int removeRank(CommandContext context, User user, String rank) {
		if(context.isOperator()) {
			if(rank.equalsIgnoreCase("admin")) {
				return removeAdmin(context, user);
			}
			else if (rank.equalsIgnoreCase("operator") || rank.equalsIgnoreCase("op")) {
				return removeOperator(context, user);
			}
			else if (rank.equalsIgnoreCase("tester")) {
				return removeTester(context, user);
			}
		}
		else {
			context.replyMessage("You do not have permission to execute that command.");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int addAdmin(CommandContext context, User user) {		
		if(DiscordUser.isAdmin(user)) {
			context.replyMessage(user + " is already a bot admin");
		}
		else {
			DiscordUser.setAdmin(context, user, true);
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int removeAdmin(CommandContext context, User user) {
		if(!DiscordUser.isAdmin(user)) {
			context.replyMessage(user + " is already not an admin");
		}
		else {
			DiscordUser.setAdmin(context, user, false);
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int addOperator(CommandContext context, User user) {
		
		if(DiscordUser.isOperator(user)) {
			context.replyMessage(user + " is already a bot operator");
		}
		else {
			DiscordUser.setOperator(context, user, true);
		}

		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int removeOperator(CommandContext context, User user) {		
		if(!DiscordUser.isOperator(user)) {
			context.replyMessage(user + " is already not a bot operator");
		}
		else {
			DiscordUser.setOperator(context, user, false);
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int addTester(CommandContext context, User user) {
		if(DiscordUser.isTester(user)) {
			context.replyMessage(user + " is already a beta tester");
		}
		else {
			DiscordUser.setTester(context, user, true);
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int removeTester(CommandContext context, User user) {
		if(!DiscordUser.isTester(user)) {
			context.replyMessage(user + " is already not a beta tester");
		}
		else {
			DiscordUser.setTester(context, user, false);
		}
		return 1;
	}
}
