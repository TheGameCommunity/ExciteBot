package com.gamebuster19901.excite.bot.command;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.UnknownPlayer;
import com.gamebuster19901.excite.bot.audit.ban.DiscordBan;
import com.gamebuster19901.excite.bot.audit.ban.ProfileBan;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.dv8tion.jda.api.entities.PrivateChannel;

public class BanCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("ban")
			.then(Commands.literal("discord").then(Commands.argument("discordUser", StringArgumentType.string()).then((Commands.argument("discriminator", StringArgumentType.string()).executes((context) -> {
					return banDiscordUserForever(context.getSource(), getDiscordUser(context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class)));
				}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
					return banDiscordUserForever(context.getSource(), getDiscordUser(context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class)), context.getArgument("reason", String.class));
				}))
				.then(Commands.argument("amount", IntegerArgumentType.integer(1)).then(Commands.argument("timeUnit", StringArgumentType.string()).executes((context) -> {
					return banDiscordUser(context.getSource(), getDiscordUser(context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class)), context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class));
				}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
					return banDiscordUser(context.getSource(), getDiscordUser(context.getArgument("discordUser", String.class), context.getArgument("discriminator", String.class)), context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class), context.getArgument("reason", String.class));
				})))))))
				.then(Commands.argument("discordId", LongArgumentType.longArg()).executes((context) -> {
					return banDiscordUserForever(context.getSource(), getDiscordUser(context.getArgument("discordId", Long.class)));
				}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
					return banDiscordUserForever(context.getSource(), getDiscordUser(context.getArgument("discordId", Long.class)), context.getArgument("reason", String.class));
				}))
				.then(Commands.argument("amount", IntegerArgumentType.integer(1)).then(Commands.argument("timeUnit", StringArgumentType.string()).executes((context) -> {
					return banDiscordUser(context.getSource(), getDiscordUser(context.getArgument("discordId", Long.class)), context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class));
				}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
					return banDiscordUser(context.getSource(), getDiscordUser(context.getArgument("discordId", Long.class)), context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class), context.getArgument("reason", String.class));
				}))))))
			.then(Commands.literal("profile").then(Commands.argument("profile", StringArgumentType.string()).executes((context) -> {
					return banProfileForever(context.getSource(), getProfile(context.getArgument("profile", String.class)));
				}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
					return banProfileForever(context.getSource(), getProfile(context.getArgument("profile", String.class)), context.getArgument("reason", String.class));
				}))
				.then(Commands.argument("amount", IntegerArgumentType.integer(1)).then(Commands.argument("timeUnit", StringArgumentType.string()).executes((context) -> {
					return banProfile(context.getSource(), getProfile(context.getArgument("profile", String.class)), context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class));
				})
				.then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
					return banProfile(context.getSource(), getProfile(context.getArgument("profile", String.class)), context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class), context.getArgument("reason", String.class));
				})))))
				.then(Commands.argument("pid", IntegerArgumentType.integer()).executes((context) -> {
					return banProfileForever(context.getSource(), new Player[] {getProfile(context.getArgument("pid", Integer.class))});
				}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
					return banProfileForever(context.getSource(), new Player[] {getProfile(context.getArgument("pid", Integer.class))}, context.getArgument("reason", String.class));
				}))
				.then(Commands.argument("amount", IntegerArgumentType.integer(1)).then(Commands.argument("timeUnit", StringArgumentType.string()).executes((context) -> {
					return banProfile(context.getSource(), new Player[] {getProfile(context.getArgument("pid", Integer.class))}, context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class));
				}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
					return banProfile(context.getSource(), new Player[] {getProfile(context.getArgument("pid", Integer.class))}, context.getArgument("amount", Integer.class), context.getArgument("timeUnit", String.class), context.getArgument("reason", String.class));
				})))))
			)
		);
	}
	
	private static DiscordUser getDiscordUser(String username, String discriminator) {
		DiscordUser user = DiscordUser.getDiscordUser(username + "#" + discriminator);
		if(user == null) {
			user = new UnknownDiscordUser(username, discriminator);
		}
		return user;
	}
	
	private static Player[] getProfile(String username) {
		return Player.getPlayersByName(username);
	}
	
	private static DiscordUser getDiscordUser(long id) {
		DiscordUser user = DiscordUser.getDiscordUserIncludingUnknown(id);
		return user;
	}
	
	private static Player getProfile(int pid) {
		return Player.getPlayerByID(pid);
	}
	
	@SuppressWarnings("rawtypes")
	private static int banDiscordUser(MessageContext context, DiscordUser user, int amount, String timeUnit) {
		return banDiscordUser(context, user, amount, timeUnit, null);
	}
	
	@SuppressWarnings("rawtypes")
	private static int banProfile(MessageContext context, Player[] profile, int amount, String timeUnit) {
		return banProfile(context, profile, amount, timeUnit, null);
	}
	
	@SuppressWarnings("rawtypes")
	private static int banDiscordUser(MessageContext context, DiscordUser user, int amount, String timeUnit, String reason) {
		if(!(user instanceof UnknownDiscordUser)) {
			Duration duration = TimeUtils.computeDuration(amount, timeUnit);
			if(duration != null) {
				return banDiscordUser(context, user, duration, reason);
			}
			else {
				context.sendMessage(amount + " " + timeUnit + " is not a valid timeunit");
			}
		}
		else {
			if(context.isAdmin()) {
				context.sendMessage("There is no discord user known as " + user);
			}
		}
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	private static int banProfile(MessageContext context, Player[] profile, int amount, String timeUnit, String reason) {
		if(profile.length != 1) {
			context.sendMessage("Ambigious name, enter a PID or FC");
			return 0;
		}
		if(!(profile[0] instanceof UnknownPlayer)) {
			Duration duration = TimeUtils.computeDuration(amount, timeUnit);
			if(duration != null) {
				return banProfile(context, profile[0], duration, reason);
			}
			else {
				context.sendMessage(amount + " " + timeUnit + " is not a valid timeunit");
			}
		}
		else {
			if(context.isAdmin()) {
				context.sendMessage("Could not find that excitebot's profile");
			}
		}
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	private static int banDiscordUserForever(MessageContext context, DiscordUser user) {
		return banDiscordUserForever(context, user, null);
	}
	
	@SuppressWarnings("rawtypes")
	private static int banProfileForever(MessageContext context, Player[] profile) {
		return banProfileForever(context, profile, null);
	}
	
	@SuppressWarnings("rawtypes")
	private static int banDiscordUserForever(MessageContext context, DiscordUser user, String reason) {
		if(context.isAdmin()) {
			if(user instanceof UnknownDiscordUser && !((UnknownDiscordUser) user).hasID()) {
				context.sendMessage("There is no discord user known as " + user);
				return 0;
			}
			Duration duration = ChronoUnit.FOREVER.getDuration();
			user.ban(context, duration, parseReason(duration, reason));
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 0;
	}

	@SuppressWarnings("rawtypes")
	private static int banProfileForever(MessageContext context, Player[] profile, String reason) {
		if(profile.length != 1) {
			context.sendMessage("Ambigious name, enter a PID or FC");
			return 0;
		}
		if(context.isAdmin()) {
			if(profile[0] instanceof UnknownPlayer) {
				context.sendMessage("There is no profile known as " + profile);
				return 0;
			}
			Duration duration = ChronoUnit.FOREVER.getDuration();
			profile[0].ban(context, duration, parseReason(duration, reason));
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	private static int banDiscordUser(MessageContext context, DiscordUser user, Duration duration, String reason) {
		if(context.isAdmin()) {
			DiscordBan ban = user.ban(new MessageContext(), duration, parseReason(duration, reason));
			String message = "Banned discord user" + user + ": \n\n" + ban;
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
	
	@SuppressWarnings("rawtypes")
	private static int banProfile(MessageContext context, Player profile, Duration duration, String reason) {
		if(context.isAdmin()) {
			ProfileBan ban = profile.ban(new MessageContext(), duration, reason);
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
				return "You have been banned from using Excite bot indefinetly due to " + reason;
			}
			else {
				return "You have been banned from using Excite bot indefinetly";
			}
		}
		if(reason != null) {
			return "You have been banned from using Excite Bot for " + TimeUtils.readableDuration(duration) + " due to " + reason;
		}
		else {
			return "You have been banned from using Excite Bot for " + TimeUtils.readableDuration(duration);
		}
	}
}
