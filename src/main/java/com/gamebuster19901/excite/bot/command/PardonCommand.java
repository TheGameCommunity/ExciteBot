package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.audit.ban.Ban;
import com.gamebuster19901.excite.bot.audit.ban.Banee;
import com.gamebuster19901.excite.bot.audit.ban.Pardon;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class PardonCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("pardon").then(Commands.argument("baneeOrBanID", LongArgumentType.longArg()).executes((context) -> {
			return pardon(context.getSource(), context.getArgument("baneeOrBanID", Long.class));
			}).then(Commands.argument("banID", LongArgumentType.longArg()).executes((context) -> {
				return pardon(context.getSource(), context.getArgument("baneeOrBanID", Long.class), context.getArgument("banID", Long.class));
			}))
		.then(Commands.argument("banee", StringArgumentType.greedyString()).executes((context) -> {
			return pardon(context.getSource(), context.getArgument("banee", String.class));
		}))
		));
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(MessageContext context, String name) {
		if(context.isAdmin()) {
			Banee banee = Banee.getBanee(context, name);
			if(banee != null) {
				return pardon(context, banee);
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(MessageContext context, long baneeOrBanID) {
		if(context.isAdmin()) {
			Ban ban = Ban.getBanByAuditId(context, baneeOrBanID);
			if(ban != null) {
				return pardon(context, ban);
			}
			Banee banee = Banee.getBanee(context, baneeOrBanID);
			return pardon(context, banee);
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(MessageContext context, long baneeID, long banId) {
		if(context.isAdmin()) {
			Banee banee = Banee.getBanee(context, baneeID);
			return pardon(context, banee, banId);
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static final int pardon(MessageContext context, Banee banee, long banId) {
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
	private static final int pardon(MessageContext context, Banee banee) {
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
	private static final int pardon(MessageContext context, Ban ban) {
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
