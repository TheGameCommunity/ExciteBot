package com.gamebuster19901.excite.bot.command;

import java.sql.SQLException;

import javax.mail.MessagingException;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.argument.PlayerArgumentType;
import com.gamebuster19901.excite.bot.database.Column;
import com.gamebuster19901.excite.bot.database.Comparator;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Result;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class RegisterCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		dispatcher.register(Commands.literal("register")
			.then(Commands.literal("profile")
				.then(Commands.argument("player", PlayerArgumentType.player())
					.executes(context -> {
						requestRegistration(context.getSource(), context.getArgument("player", Player.class));
						return 1;
					})	
				)
			).then(Commands.literal("wii")
				.then(Commands.argument("code", StringArgumentType.greedyString())
					.executes(context -> {
						registerWii(context.getSource(), context.getArgument("code", String.class));
						return 1;
					})
				)
			)
		);
	}
	
	@SuppressWarnings("rawtypes")
	private static void requestRegistration(CommandContext context, Player desiredProfile) {
		DiscordUser discordUser = context.getDiscordAuthor();
		if(context.isConsoleMessage()) {
			context.sendMessage("This command must be executed from discord.");
			return;
		}
		if(discordUser.requestingRegistration()) {
			context.sendMessage("You are already trying to register a profile! Please wait until registration is complete or the registration code expires.");
			return;
		}

		if(desiredProfile.isBanned()) {
			context.sendMessage("You cannot register a banned profile.");
			return;
		}
		
		String securityCode = discordUser.requestRegistration(desiredProfile);
		sendInfo(context, discordUser, desiredProfile, securityCode);
	}
	
	@SuppressWarnings("rawtypes")
	private static void sendInfo(CommandContext context, DiscordUser discordUser, Player desiredProfile, String securityCode) {
		context.getDiscordAuthor().sendMessage(
				discordUser.getJDAUser().getAsMention() + 
				", you have requested registration of the following profile:\n\n"
				+ desiredProfile.toFullString() 
				+ "\n\nRegistration Code: `" + securityCode + "`\n"
				+ "Change the profile's username to the registration code, then log in and search for a match.\n\n"
				+ "Registration may take up to two minutes to complete. The registration code expires after 5 minutes.\n\n"
				+ "You will receive a reply upon registration completion. Please stay logged in and searching until registration is completed.\n\n"
				);
	}
	
	@SuppressWarnings("rawtypes")
	private static void registerWii(CommandContext context, String securityCode) {
		if(context.isGuildMessage()) {
			context.deletePromptingMessage(ConsoleContext.INSTANCE, context.getMention() + " - Woah! Send your code to me via direct message! Nobody else should be seeing your registration code!");
			return;
		}
		if(context.isConsoleMessage()) {
			context.sendMessage("This command can only be executed in discord");
			return;
		}
		try {
			Result result = Table.selectColumnsFromWhere(context, Column.WII_ID, Table.WIIS, new Comparison(Column.REGISTRATION_CODE, Comparator.EQUALS, securityCode));
			if(result.hasNext()) {
				result.next();
				Wii wii = Wii.getWii(result.getString(Column.WII_ID));
				wii.register(context);
			}
			else {
				context.sendMessage("Unknown registration code.");
			}
		} catch (SQLException | MessagingException e) {
			context.sendMessage(StacktraceUtil.getStackTrace(e));
		}
	}
}
