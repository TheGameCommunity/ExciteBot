package com.gamebuster19901.excite.bot.command;

import java.io.IOException;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.security.auth.login.LoginException;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.argument.DiscordUserArgumentType;
import com.gamebuster19901.excite.bot.command.argument.PlayerArgumentType;
import com.gamebuster19901.excite.bot.command.argument.WiiArgumentType;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.Nobody;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.gamebuster19901.excite.bot.command.argument.DiscordUserArgumentType.UnknownType;
import com.mojang.brigadier.CommandDispatcher;

public class ForceRegister {

	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("forceRegister")
			.then(Commands.argument("player", PlayerArgumentType.player())
				.then(Commands.argument("discord", DiscordUserArgumentType.user().setUnknown(UnknownType.KNOWN_ID))
					.executes(context -> {
						forceRegister(context.getSource(), context.getArgument("player", Player.class), context.getArgument("discord", DiscordUser.class));
						return 1;
					})	
				)
			)
			.then(Commands.argument("wii", WiiArgumentType.wii())
				.then(Commands.argument("discord", DiscordUserArgumentType.user().setUnknown(UnknownType.KNOWN_ID))
					.executes(context -> {
						forceRegister(context.getSource(), context.getArgument("wii", Wii.class), context.getArgument("discord", DiscordUser.class));
						return 1;
					})
				)
			)
		);
	}
	
	private static final void forceRegister(MessageContext context, Player player, DiscordUser discord) {
		if((context.isAdmin() && player.getOwner() == Nobody.INSTANCE) || context.isOperator()) {
			if(discord instanceof UnknownDiscordUser) {
				context.sendMessage("Note: " + discord.getIdentifierName() + " does not share any servers with " + Main.discordBot.getSelfUser().getAsMention());
			}
			player.setDiscord(discord.getID());
			context.sendMessage(player.toFullString());
		}
		else if (context.isAdmin()) {
			context.sendMessage("Insufficient permissions. " + player.getOwnershipString() + " is already owned. Only operators can change account ownership.");
		}
		else {
			context.sendMessage("Insufficient permissions.");
		}
	}
	
	public static void forceRegister(MessageContext context, Wii wii, DiscordUser discord) {
		if((context.isAdmin() && wii.getOwner() == Nobody.INSTANCE || context.isOperator())) {
			if(discord instanceof UnknownDiscordUser) {
				context.sendMessage("Note: " + discord.getIdentifierName() + " does not share any servers with " + Main.discordBot.getSelfUser().getAsMention());
			}
			try {
				wii.register(context, discord);
				context.sendMessage(wii.getOwnershipString());
			} catch (LoginException | SQLException | MessagingException | IOException e) {
				context.sendMessage(StacktraceUtil.getStackTrace(e));
			}
		}
		else if (context.isAdmin()) {
			context.sendMessage("Insufficient permissions. " + wii.getOwnershipString() + " is already owned. Only operators can change account ownership.");
		}
		else {
			context.sendMessage("Insufficient Permissions");
		}
	}
	
}
