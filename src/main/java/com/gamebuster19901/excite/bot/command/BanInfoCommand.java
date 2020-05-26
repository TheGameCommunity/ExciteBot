package com.gamebuster19901.excite.bot.command;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;

import com.gamebuster19901.excite.bot.WiimmfiCommand;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class BanInfoCommand extends WiimmfiCommand {
	
	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("!baninfo").then(Commands.argument("discordUser", StringArgumentType.greedyString()).executes((context) -> {
			return sendBanInfo(context.getSource(), getDiscordUser(context.getArgument("discordUser", String.class)));
		}))
		.then(Commands.argument("discordId", LongArgumentType.longArg()).executes((context) -> {
			return sendBanInfo(context.getSource(), getDiscordUser(context.getArgument("discordId", Long.class)));
		})));
	}

	private static DiscordUser getDiscordUser(String discordUser) {
		if(discordUser.indexOf('#') == -1) {
			byte[] bytes = discordUser.getBytes();
			for(int i = discordUser.length() - 1; i > 0; i--) {
				if(bytes[i] == ' ') {
					bytes[i] = '#';
					break;
				}
			}
			discordUser = new String(bytes);
		}
		DiscordUser user = DiscordUser.getDiscordUser(discordUser);
		if(user == null) {
			return new UnknownDiscordUser(-1);
		}
		return user;
	}
	
	private static DiscordUser getDiscordUser(long discordId) {
		return DiscordUser.getDiscordUserIncludingUnloaded(discordId);
	}
	
	@SuppressWarnings("rawtypes")
	private static int sendBanInfo(MessageContext context, DiscordUser user) {
		if(context.isAdmin()) {
			if(context.isPrivateMessage() || context.isConsoleMessage()) {
				if(user instanceof UnknownDiscordUser) {
					context.sendMessage("Could not find that user");
					return 1;
				}
				String bannedUntil; 
				if(user.isBanned()) {
					try {
						bannedUntil = TimeUtils.getDate(user.getBanExpireTime());
					} catch(DateTimeException e) {
						bannedUntil = "Forever";
					}
					context.sendMessage("Ban information for " + user + ":\n\n" + 
						"Reason: " + user.getBanReason() + "\n" +
						"Banned until: " + bannedUntil + "\n" +
						"Time remaining: " + TimeUtils.readableDuration(Duration.between(Instant.now(), user.getBanExpireTime())) + "\n" +
						"Unpardoned Bans: " + user.getUnpardonedBanCount() + "\n" +
						"Total Bans: " + user.getTotalBanCount()
					);
				}
				else {
					context.sendMessage("Ban information for " + user + ":\n\n" + 
						"This user is not currently banned \n\n" +
						"Unpardoned Bans:" + user.getUnpardonedBanCount() + "\n" +
						"Total Bans:" + user.getTotalBanCount()
					);
				}
			}
			else {
				context.sendMessage("This command must be executed in a private message or in the console");
			}
		}
		return 1;
	}
	
}
