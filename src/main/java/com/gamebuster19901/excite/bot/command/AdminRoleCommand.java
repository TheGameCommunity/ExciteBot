package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.bot.server.DiscordServer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.dv8tion.jda.api.entities.Role;

public class AdminRoleCommand {
	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!role").then(Commands.literal("add").then(Commands.argument("role", StringArgumentType.greedyString()).executes((context) -> {
			return addAdminRole(context.getSource(), context.getArgument("role", String.class));
		})))
		.then(Commands.literal("remove").then(Commands.argument("role", StringArgumentType.greedyString()).executes((context) -> {
			return removeAdminRole(context.getSource(), context.getArgument("role", String.class));
		}))));
	}
	
	@SuppressWarnings("rawtypes")
	private static int addAdminRole(MessageContext context, String role) {
		if(context.isOperator()) {
			if(!context.isConsoleMessage()) {
				if(context.isGuildMessage()) {
					DiscordServer server = context.getServer();
					for(Role r : server.getRoles()) {
						if (r.getName().equalsIgnoreCase(role) || r.getAsMention().equalsIgnoreCase(role) || r.getId().equals(role)) {
							server.addAdminRole(r);
							context.sendMessage(r.getAsMention() + " You have been added as an administrator for Excite bot in this server.");
							return 1;
						}
					}
					context.sendMessage("There is no role called " + role);
					return 1;
				}
			}
			context.sendMessage("This command must be executed in a discord server");
			return 1;
		}
		context.sendMessage("You do not have permission to execute this command");
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int removeAdminRole(MessageContext context, String role) {
		if(context.isAdmin()) {
			if(!context.isConsoleMessage()) {
				if(context.isGuildMessage()) {
					DiscordServer server = context.getServer();
					for(Role r : server.getRoles()) {
						if (r.getName().equalsIgnoreCase(role) || r.getAsMention().equalsIgnoreCase(role) || r.getId().equals(role)) {
							server.removeAdminRole(r);
							context.sendMessage(r.getAsMention() + " You are no longer an administrator for Excite bot in this server.");
							return 1;
						}
					}
					context.sendMessage("There is no registered administrative role called " + role);
					return 1;
				}
			}
			context.sendMessage("This command must be executed in a discord server");
			return 1;
		}
		context.sendMessage("You do not have permission to execute this command");
		return 1;
	}
	
}
