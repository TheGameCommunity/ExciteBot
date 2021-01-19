package com.gamebuster19901.excite.bot.command;

import java.sql.SQLException;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class InsertCommand {
	
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("insert").then(Commands.literal("profile")
			.then(Commands.argument("pid", IntegerArgumentType.integer(1, 999999999))
			.then(Commands.argument("fc", StringArgumentType.string())
			.then(Commands.argument("name", StringArgumentType.greedyString())
			.executes((context) -> {
				return addProfile(context.getSource(), context.getArgument("pid", Integer.class), context.getArgument("fc", String.class), context.getArgument("name", String.class));
			}
		))))));
	}
	
	private static int addProfile(MessageContext context, int pid, String fc, String name) {
		if(context.isOperator()) {
			if(!Player.isPlayerKnown(context, pid)) {
				try {
					if(Player.isValidFriendCode(fc)) {
						name = name.replace("\"", "");
						Player.addPlayer(context, false, pid, fc, name);
						Player player = Player.getPlayerByID(context, pid);
						context.sendMessage(player.toString() + " inserted");
						return 1;
					}
					else {
						context.sendMessage(fc + " is not a valid friend code");
						return 1;
					}
				} catch (SQLException e) {
					context.sendMessage("Unable to add profile " + pid + ":\n\n + " + StacktraceUtil.getStackTrace(e));
					return 1;
				}
			}
			else {
				context.sendMessage("A profile with PID " + pid + " is already known.");
				return 1;
			}
		}
		context.sendMessage("You do not have permission to execute that command.");
		return 1;
	}
	
}
