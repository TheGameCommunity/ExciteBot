package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.audit.ban.Ban;
import com.gamebuster19901.excite.bot.audit.ban.Banee;
import com.gamebuster19901.excite.bot.audit.ban.Pardon;
import com.gamebuster19901.excite.bot.command.argument.DiscordUserArgumentType;
import com.gamebuster19901.excite.bot.command.argument.PlayerArgumentType;
import com.gamebuster19901.excite.bot.command.argument.UserObtainer;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;

public class PardonCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		
		dispatcher.register(Commands.literal("pardon")
				
			.then(Commands.argument("discord", DiscordUserArgumentType.user().of(UserObtainer.UNCHANGED).setUnknown(UnknownType.KNOWN_ID))
				.executes((context) -> {
					return pardon(context.getSource(), context.getArgument("discord", User.class));
				})	
			).then(Commands.argument("banID", LongArgumentType.longArg()).executes((context) -> {
				return pardon(context.getSource(), Banee.of(context.getArgument("discord", User.class)), context.getArgument("banID", Long.class));
			}))
			
			.then(Commands.argument("player", PlayerArgumentType.player().allowUnknown())
				.executes((context) -> {
					return pardon(context.getSource(), context.getArgument("player", Player.class));
				})
			).then(Commands.argument("banID", LongArgumentType.longArg()).executes((context) -> {
				return pardon(context.getSource(), context.getArgument("player", Player.class), context.getArgument("banID", Long.class));
			}))
			
			.then(Commands.argument("banID", LongArgumentType.longArg()).executes((context) -> {
				return pardon(context.getSource(), context.getArgument("banID", Long.class));
			}))
			
		);
		
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(CommandContext context, long banID) {
		if(context.isAdmin()) {
			Ban ban = Ban.getBanByAuditId(context, banID);
			if(ban != null) {
				return pardon(context, ban);
			}
			else {
				context.sendMessage("Could not find ban #" + banID);
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(CommandContext context, Banee banee, long banId) {
		if(context.isAdmin()) {
			Ban pardoning = Ban.getBanByAuditId(context, banId);
			if(pardoning != null) {
				if(pardoning.getBannedID() == banee.getID()) {
					if(banee instanceof DiscordUser) {
						return pardon(context, (DiscordUser) banee);
					}
					else if (banee instanceof Player) {
						return pardon(context, (Player) banee);
					}
				}
				else {
					context.sendMessage("Ban #" + banId + " does not belong to " + banee.getIdentifierName());
				}
			}
			else {
				context.sendMessage("Ban #" + banId + " doesn't exist");
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(CommandContext context, Banee banee) {
		if(context.isAdmin()) {
			Ban ban = banee.getLongestActiveBan(context);
			if(ban == null) {
				context.sendMessage(banee.getIdentifierName() + " is not banned.");
				return 1;
			}
			Pardon pardon = Pardon.addPardonByAuditID(context, ban.getID());
			if(pardon != null) {
				context.sendMessage("pardoned the longest ban (#" + ban.getID() + ") of " + banee.getIdentifierName());
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	private static final int pardon(CommandContext context, Ban ban) {
		if(context.isAdmin()) {
			Pardon pardon = Pardon.addPardonByAuditID(context, ban.getID());
			if(pardon != null) {
				context.sendMessage("pardoned ban #" + ban.getID() + " belonging to " + ban.getBannedUsername() + "");
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
