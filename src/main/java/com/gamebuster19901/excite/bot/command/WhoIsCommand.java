package com.gamebuster19901.excite.bot.command;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

@SuppressWarnings("rawtypes")
public class WhoIsCommand {

	private static final boolean [] UNDER_ONE_DAY = new boolean[]      {true, true, true, true, true, true, true};
	private static final boolean [] BETWEEN_DAY_WEEK = new boolean[]   {true, true, true, true, true, true, false};
	private static final boolean [] BETWEEN_WEEK_MONTH = new boolean[] {true, true, true, true, true, false, false};
	private static final boolean [] BETWEEN_MONTH_YEAR = new boolean[] {true, true, true, true, false, false, false};
	private static final boolean [] OVER_ONE_YEAR = new boolean[]      {true, true, true, false, false, false, false};
	
	private static final boolean [] HOURS_ONLY = new boolean[]         {false, false, false, false, true, false, false};
	
	private static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy/MM/dd HH:mm z", Locale.ENGLISH);
	
	public static void register(CommandDispatcher<CommandContext> dispatcher) {
		LiteralCommandNode<CommandContext> builder = dispatcher.register(Commands.userGlobal("whois")
			.then(Commands.user("user")
				.executes((command) -> {
					return sendResponse(command.getSource(), command.getArgument("user", User.class));
				})
			)
			.then(Commands.player("player")
				.executes((command) -> {
					return sendResponse(command.getSource(), command.getArgument("player", Player.class));
				})
			)
			/*.then(Commands.anyStringGreedy("arg")
					.exe
			)*/
		);
		
		dispatcher.register(Commands.literal("me").executes((command) ->  {
			return sendResponse(command.getSource(), command.getSource().getDiscordAuthor());
		}));
		
		dispatcher.register(Commands.literal("wi").redirect(builder));
	}

	@SuppressWarnings("serial")
	public static int sendResponse(CommandContext context, User user) {
		if(context.isConsoleMessage() || !context.isDiscordContext()) {
			context.replyMessage("You cannot execute this command as " + context.getEvent(Object.class).getClass().getSimpleName());
			return 1;
		}
		boolean hasMembers = context.isGuildContext();
		EmbedBuilder embed = new EmbedBuilder();
		Member member;
		embed.setColor(Color.WHITE);
		Wii[] wiis = DiscordUser.getRegisteredWiis(user);
		Set<Player> profiles = DiscordUser.getProfiles(context, user);
		Duration timeOnline = Duration.ZERO;
		Instant lastOnline = TimeUtils.PLAYER_EPOCH;
		StringBuilder profileList = new StringBuilder();
		StringBuilder wiiList = new StringBuilder();
		for(Player profile : profiles) {
			profileList.append(profile.toEmbedstring());
			profileList.append('\n');
			timeOnline = timeOnline.plus(profile.getOnlineDuration());
			Instant profileLastOnline = profile.getLastOnline();
			if(profileLastOnline.isAfter(lastOnline)) {
				lastOnline = profileLastOnline;
			}
		}
		for(Wii wii : wiis) {
			wiiList.append(wii.getName());
			wiiList.append('\n');
		}
		if(hasMembers && (member = DiscordUser.getMember(user, context.getServer())) != null) {
			embed.setColor(member.getColor());
			embed.setThumbnail(user.getEffectiveAvatarUrl());
			embed.addField("Username:", user.getName(), false);
			embed.addField("Discriminator", user.getDiscriminator(), false);
			//embed.addField("Badges:", "", false);
			embed.addField("ID:", "" + user.getIdLong(), false);
			embed.addField("Nickname:", member.getNickname() != null ? member.getNickname() : "##Not Nicknamed##", false);
			embed.addField("Joined Discord:", DATE.format(member.getTimeCreated().toInstant().toEpochMilli()), false);
			embed.addField("Joined " + context.getServer().getName() + ":", DATE.format(member.getTimeJoined().toInstant().toEpochMilli()), false);
			embed.addField("Member for:", readableDuration(TimeUtils.since(member.getTimeJoined().toInstant()), false), false);
			embed.addField("Time Online:", readableDuration(timeOnline, true), false);
			embed.addField(profiles.size() + " registered Profiles:", profileList.toString(), false);
			embed.addField(wiis.length + " registered Wiis:", wiiList.toString(), false);
		}
		else {
			embed.setThumbnail(user.getEffectiveAvatarUrl());
			embed.addField("Username:", user.getName(), false);
			embed.addField("Discriminator", user.getDiscriminator(), false);
			embed.addField("ID:", "" + user.getIdLong(), false);
			embed.addField("Time Online:", readableDuration(timeOnline, true), false);
			embed.addField(profiles.size() + " registered Profiles:", profileList.toString(), false);
			embed.addField(wiis.length + " registered Wiis:", wiiList.toString(), false);
			embed.appendDescription("For more information, execute this command in a server the user is in.");
		}
		embed.setTimestamp(Instant.now());
		context.sendMessage(embed);
		return 1;
	}
	
	private static int sendResponse(CommandContext context, Player profile) {
		EmbedBuilder embed = new EmbedBuilder();
		User user = profile.getOwner();
		embed.addField("Name:", profile.getName(), false);
		embed.addField("ID:", profile.getID() + "", false);
		embed.addField("FC:", profile.getFriendCode(), false);
		embed.addField("Owner:", DiscordUser.toDetailedString(user), false);
		embed.addField("Time Online:", readableDuration(profile.getOnlineDuration(), true), false);
		embed.addField("First Seen:", DATE.format(profile.getFirstSeen().toEpochMilli()), false);
		embed.addField("Last Seen:", DATE.format(profile.getLastOnline().toEpochMilli()), false);
		embed.setTimestamp(Instant.now());
		context.sendMessage(embed);
		return 0;
	}
	
	public static final String readableDuration(Duration duration, boolean includeHours) {
		String suffix = "";
		if(includeHours) {
			suffix = " (" + TimeUtils.readableDuration(duration, HOURS_ONLY) + ")";
		}
		if(duration.compareTo(Duration.ofDays(1)) < 0) {
			return TimeUtils.readableDuration(duration, UNDER_ONE_DAY) + suffix;
		}
		if(duration.compareTo(Duration.ofDays(7)) < 0) {
			return TimeUtils.readableDuration(duration, BETWEEN_DAY_WEEK) + suffix;
		}
		if(duration.compareTo(Duration.ofDays(30)) < 0) {
			return TimeUtils.readableDuration(duration, BETWEEN_WEEK_MONTH) + suffix;
		}
		if(duration.compareTo(Duration.ofDays(365)) < 0) {
			return TimeUtils.readableDuration(duration, BETWEEN_MONTH_YEAR) + suffix;
		}
		return TimeUtils.readableDuration(duration, OVER_ONE_YEAR) + suffix;
	}
	
}
