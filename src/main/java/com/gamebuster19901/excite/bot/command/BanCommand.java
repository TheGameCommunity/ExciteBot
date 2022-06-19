package com.gamebuster19901.excite.bot.command;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.audit.ban.Ban;
import com.gamebuster19901.excite.bot.command.argument.DiscordUserArgumentType;
import com.gamebuster19901.excite.bot.command.argument.DurationArgumentType;
import com.gamebuster19901.excite.bot.command.argument.PlayerArgumentType;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.TimeUtils;
import static com.gamebuster19901.excite.bot.command.argument.DiscordUserArgumentType.UnknownType.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.dv8tion.jda.api.entities.PrivateChannel;

public class BanCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("ban")
			.then(Commands.argument("discordUser", DiscordUserArgumentType.user().setUnknown(KNOWN_ID))
				.executes((context) -> {
					return banDiscordUser(context.getSource(), context.getArgument("discordUser", DiscordUser.class), TimeUtils.FOREVER, parseReason(TimeUtils.FOREVER, null));
				}
			)
				.then(Commands.argument("duration", DurationArgumentType.duration())
					.executes((context) -> {
						Duration duration = context.getArgument("duration", Duration.class);
						return banDiscordUser(context.getSource(), context.getArgument("discordUser", DiscordUser.class), duration, parseReason(duration, null));
					}
					).then(Commands.argument("reason", StringArgumentType.greedyString())
						.executes((context) -> {
							Duration duration = context.getArgument("duration", Duration.class);
							return banDiscordUser(context.getSource(), context.getArgument("discordUser", DiscordUser.class), duration, parseReason(duration, context.getArgument("reason", String.class)));
						})
					)
				)
			.then(Commands.argument("reason", StringArgumentType.greedyString())
				.executes((context) -> {
					return banDiscordUser(context.getSource(), context.getArgument("discordUser", DiscordUser.class), TimeUtils.FOREVER, parseReason(TimeUtils.FOREVER, context.getArgument("reason", String.class)));
				})
			)
		)
		
			
			
			
			
		
			
		.then(Commands.argument("player", PlayerArgumentType.player())
			.executes((context) -> {
				return banProfile(context.getSource(), context.getArgument("player", Player.class), TimeUtils.FOREVER, parseReason(TimeUtils.FOREVER, null));
			}
		)
			
			.then(Commands.argument("duration", DurationArgumentType.duration())
				.executes((context) -> {
					Duration duration = context.getArgument("duration", Duration.class);
					return banProfile(context.getSource(), context.getArgument("player", Player.class), duration, parseReason(duration, null));
				}
				).then(Commands.argument("reason", StringArgumentType.greedyString())
					.executes((context) -> {
						Duration duration = context.getArgument("duration", Duration.class);
						return banProfile(context.getSource(), context.getArgument("player", Player.class), duration, parseReason(duration, context.getArgument("reason", String.class)));
					})
				)
			)
			.then(Commands.argument("reason", StringArgumentType.greedyString())
					.executes((context) -> {
						return banProfile(context.getSource(), context.getArgument("player", Player.class), TimeUtils.FOREVER, parseReason(TimeUtils.FOREVER, context.getArgument("reason", String.class)));
					})
				)
			)
			
		);
	}
	
	@SuppressWarnings("rawtypes")
	private static int banDiscordUser(MessageContext context, DiscordUser user, Duration duration, String reason) {
		if(context.isAdmin()) {
			Ban ban = user.ban(context, duration, reason);
			String message = "Banned discord user " + user.getAsMention() + ": \n\n" + ban;
			if(context.isConsoleMessage()) {
				context.sendMessage(message);
			}
			else {
				PrivateChannel privateChannel;
				if(context.isPrivateMessage()) {
					privateChannel = (PrivateChannel) context.getChannel();
				}
				else {
					privateChannel = context.getDiscordAuthor().getJDAUser().openPrivateChannel().complete();
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
	private static int banProfile(MessageContext context, Player profile, Duration duration, String reason) {
		if(context.isAdmin()) {
			Ban ban = profile.ban(context, duration, reason);
			String message = "Banned profile " + profile.getPrettyDiscord() + ": \n\n" + ban;
			if(context.isConsoleMessage()) {
				context.sendMessage(message);
			}
			else {
				PrivateChannel privateChannel;
				if(context.isPrivateMessage()) {
					privateChannel = (PrivateChannel) context.getChannel();
				}
				else {
					privateChannel = context.getDiscordAuthor().getJDAUser().openPrivateChannel().complete();
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
