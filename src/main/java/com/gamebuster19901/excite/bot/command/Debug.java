package com.gamebuster19901.excite.bot.command;

import java.time.Duration;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.argument.ChannelArgumentType;
import com.gamebuster19901.excite.bot.command.argument.DiscordUserArgumentType;
import com.gamebuster19901.excite.bot.command.argument.DurationArgumentType;
import com.gamebuster19901.excite.bot.command.argument.PlayerArgumentType;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class Debug {

	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		
		dispatcher.register(
			Commands.literal("debug")
				.then(Commands.literal("out")
					/*.executes(
							(context -> 
								{
									return setDebugOutput(context.getSource(), context.getSource().getChannel());
								}
							)
					)*/
				
					.then((Commands.argument("channel", new ChannelArgumentType()).executes(
							(context) -> {
								MessageChannel channel = context.getArgument("channel", MessageChannel.class);
								context.getSource().sendMessage("argument was read as " + channel.getAsMention() + "\n\n(Literally): ```" + channel.getAsMention() + "```\n\nname: " + channel.getName() ); return 1;})))
				)
				.then(Commands.literal("time")
						.then(Commands.argument("duration", DurationArgumentType.duration())
								.executes(
									(context) -> {
										context.getSource().sendMessage("total length was " + TimeUtils.fullReadableDuration(context.getArgument("duration", Duration.class)));
										return 1;
									}
								)
								.then(Commands.literal("yay")
										.executes(
												(context) -> 
													{
														context.getSource().sendMessage("yes this parses correctly!");
														return 1;
													}
										)
								)
				))
				.then(Commands.literal("user")
					.then(Commands.argument("user", DiscordUserArgumentType.user())
						.executes((context)	-> {
							relayUser(context.getSource(), context.getArgument("user", DiscordUser.class));
							return 1;
						})
					)
				)
				.then(Commands.literal("player")
					.then(Commands.argument("player", PlayerArgumentType.player())
						.executes((context) -> {
							relayPlayer(context.getSource(), context.getArgument("player", Player.class));
							return 1;
						})
					)	
				)
		);
		
	}
	
	private static int setDebugOutput(CommandContext context, MessageChannel channel) {
		if(context.isOperator()) {
			if(channel instanceof TextChannel) {
				TextChannel tChannel = (TextChannel) channel;
				context.sendMessage("Set debug output to " + tChannel.getAsMention() + " in " + context.getServer().getName());
			}
			else {
				context.sendMessage(channel.getName() + " is not a guild text channel");
			}
		}
		else {
			context.sendMessage("You do not have permission to execute that command");
		}
		return 1;
	}
	
	public static void relayUser(CommandContext context, DiscordUser user) {
		context.sendMessage("Your argument appears to be " + user.getIdentifierName());
	}
	
	public static void relayPlayer(CommandContext context, Player player) {
		context.sendMessage("Your argument appears to be " + player.getIdentifierName());
	}
	
}
