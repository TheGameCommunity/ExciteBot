package com.gamebuster19901.excite.bot.command;

import java.time.Duration;

import com.gamebuster19901.excite.bot.command.argument.ChannelArgumentType;
import com.gamebuster19901.excite.bot.command.argument.DurationArgumentType;
import com.gamebuster19901.excite.bot.command.argument.MessageChannelObtainer;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class Debug {

	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		
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
								MessageChannel channel = getMessageChannel(context.getSource(), context.getArgument("channel", MessageChannelObtainer.class));
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
		);
		
	}
	
	private static MessageChannel getMessageChannel(MessageContext context, MessageChannelObtainer channel) throws CommandSyntaxException {
		return channel.obtain(context);
	}
	
	private static int setDebugOutput(MessageContext context, MessageChannel channel) {
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
	
}
