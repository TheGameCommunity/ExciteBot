package com.gamebuster19901.excite.bot.command;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.audit.ban.Ban;
import com.gamebuster19901.excite.bot.command.argument.DurationArgumentType;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.TimeUtils;

import com.mojang.brigadier.CommandDispatcher;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;

public class BanCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		dispatcher.register(Commands.userGlobal("ban")
			.then(Commands.user("discordUser")
				.executes((context) -> {
					return banDiscordUser(context.getSource(), context.getArgument("discordUser", User.class), TimeUtils.FOREVER, parseReason(TimeUtils.FOREVER, null));
				}
			)
				.then(Commands.argument("duration", DurationArgumentType.duration())
					.executes((context) -> {
						Duration duration = context.getArgument("duration", Duration.class);
						return banDiscordUser(context.getSource(), context.getArgument("discordUser", User.class), duration, parseReason(duration, null));
					}
					).then(Commands.anyStringGreedy("reason")
						.executes((context) -> {
							Duration duration = context.getArgument("duration", Duration.class);
							return banDiscordUser(context.getSource(), context.getArgument("discordUser", User.class), duration, parseReason(duration, context.getArgument("reason", String.class)));
						})
					)
				)
			.then(Commands.anyStringGreedy("reason")
				.executes((context) -> {
					return banDiscordUser(context.getSource(), context.getArgument("discordUser", User.class), TimeUtils.FOREVER, parseReason(TimeUtils.FOREVER, context.getArgument("reason", String.class)));
				})
			)
		)
		
			
			
			
			
		
			
		.then(Commands.player("player")
			.executes((context) -> {
				return banProfile(context.getSource(), context.getArgument("player", Player.class), TimeUtils.FOREVER, parseReason(TimeUtils.FOREVER, null));
			}
		)
			
			.then(Commands.argument("duration", DurationArgumentType.duration())
				.executes((context) -> {
					Duration duration = context.getArgument("duration", Duration.class);
					return banProfile(context.getSource(), context.getArgument("player", Player.class), duration, parseReason(duration, null));
				}
				).then(Commands.anyStringGreedy("reason")
					.executes((context) -> {
						Duration duration = context.getArgument("duration", Duration.class);
						return banProfile(context.getSource(), context.getArgument("player", Player.class), duration, parseReason(duration, context.getArgument("reason", String.class)));
					})
				)
			)
			.then(Commands.anyStringGreedy("reason")
					.executes((context) -> {
						return banProfile(context.getSource(), context.getArgument("player", Player.class), TimeUtils.FOREVER, parseReason(TimeUtils.FOREVER, context.getArgument("reason", String.class)));
					})
				)
			)
			
		);
	}
	
	@SuppressWarnings("rawtypes")
	private static int banDiscordUser(CommandContext context, User user, Duration duration, String reason) {
		if(context.isAdmin()) {
			Ban ban = DiscordUser.ban(context, user, duration, reason);
			String message = "Banned discord user " + user.getAsMention() + ": \n\n" + ban;
			if(context.isConsoleMessage()) {
				context.sendMessage(message);
			}
			else {
				PrivateChannel privateChannel;
				if(context.isPrivateContext()) {
					privateChannel = (PrivateChannel) context.getChannel();
				}
				else {
					privateChannel = context.getDiscordAuthor().openPrivateChannel().complete();
				}
				privateChannel.sendMessage(message).complete();
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private static int banProfile(CommandContext context, Player profile, Duration duration, String reason) {
		if(context.isAdmin()) {
			Ban ban = profile.ban(context, duration, reason);
			String message = "Banned profile " + profile.getPrettyDiscord() + ": \n\n" + ban;
			if(context.isConsoleMessage()) {
				context.sendMessage(message);
			}
			else {
				PrivateChannel privateChannel;
				if(context.isPrivateContext()) {
					privateChannel = (PrivateChannel) context.getChannel();
				}
				else {
					privateChannel = context.getDiscordAuthor().openPrivateChannel().complete();
				}
				privateChannel.sendMessage(message);
			}
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
	private static String parseReason(Duration duration, String reason) {
		if(ChronoUnit.FOREVER.getDuration().equals(duration)) {
			if(reason != null) {
				return "You have been banned from using " + Main.discordBot.getSelfUser().getAsTag() + " indefinetly due to " + reason;
			}
			else {
				return "You have been banned from using " + Main.discordBot.getSelfUser().getAsTag() + " indefinetly";
			}
		}
		if(reason != null) {
			return "You have been banned from using " + Main.discordBot.getSelfUser().getAsTag() + " for " + TimeUtils.readableDuration(duration) + " due to " + reason;
		}
		else {
			return "You have been banned from using " + Main.discordBot.getSelfUser().getAsTag() + " for " + TimeUtils.readableDuration(duration);
		}
	}
}
