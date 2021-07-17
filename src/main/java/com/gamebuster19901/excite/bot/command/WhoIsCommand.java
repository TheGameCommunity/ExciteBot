package com.gamebuster19901.excite.bot.command;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.Named;
import com.gamebuster19901.excite.util.TimeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

@SuppressWarnings("rawtypes")
public class WhoIsCommand {

	private static final boolean [] UNDER_ONE_DAY = new boolean[]      {true, true, true, true, true, true, true};
	private static final boolean [] BETWEEN_DAY_WEEK = new boolean[]   {true, true, true, true, true, true, false};
	private static final boolean [] BETWEEN_WEEK_MONTH = new boolean[] {true, true, true, true, true, false, false};
	private static final boolean [] BETWEEN_MONTH_YEAR = new boolean[] {true, true, true, true, false, false, false};
	private static final boolean [] OVER_ONE_YEAR = new boolean[]      {true, true, true, false, false, false, false};
	
	private static final boolean [] HOURS_ONLY = new boolean[]         {false, false, false, false, true, false, false};
	
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		LiteralCommandNode<MessageContext> builder = dispatcher.register(Commands.literal("whois")
			.then(Commands.argument("player", StringArgumentType.greedyString()).executes((command) -> {
				return sendResponse(command.getSource(), command.getArgument("player", String.class));
			}
		)));
		
		dispatcher.register(Commands.literal("me").executes((command) ->  {
			return sendResponse(command.getSource(), "" + command.getSource().getAuthor().getID());
		}));
		
		dispatcher.register(Commands.literal("wi").redirect(builder));
	}
	
	@SuppressWarnings("serial")
	public static int sendResponse(MessageContext context, String lookingFor) {
		if(context.isConsoleMessage() || context.isIngameEvent()) {
			context.sendMessage("You cannot execute this command as " + context.getEvent().getClass().getSimpleName());
			return 1;
		}
		Wiimmfi wiimmfi = Main.discordBot.getWiimmfi();
		EmbedBuilder embed = new EmbedBuilder();
		boolean hasMembers = context.isGuildMessage();
		if(wiimmfi.getError() == null) {
			if(!lookingFor.isEmpty()) {
				HashSet<DiscordUser> users = new HashSet<DiscordUser>() {{this.addAll(Arrays.asList(DiscordUser.getDiscordUsersWithUsernameOrID(context, lookingFor)));}};
				HashSet<Player> players = new HashSet<Player>() {{this.addAll(Arrays.asList(Player.getPlayersByAnyIdentifier(context, lookingFor)));}};
				HashSet<Named> matches = new HashSet<Named>();
				matches.addAll(users);
				matches.addAll(players);
				SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd HH:mm z", Locale.ENGLISH);
				
				if(matches.size() == 1) {
					Named match = matches.iterator().next();
					embed.setTitle("Information about " + match.getIdentifierName());
					if(match instanceof DiscordUser) {
						DiscordUser user = (DiscordUser) match;
						Member member;
						embed.setColor(Color.WHITE);
						Wii[] wiis = user.getRegisteredWiis();
						Set<Player> profiles = user.getProfiles(context);
						Duration timeOnline = Duration.ZERO;
						Instant lastOnline = TimeUtils.PLAYER_EPOCH;
						String profileList = "";
						String wiiList = "";
						for(Player profile : profiles) {
							profileList = profileList + profile.toEmbedstring() + "\n";
							timeOnline = timeOnline.plus(profile.getOnlineDuration());
							Instant profileLastOnline = profile.getLastOnline();
							if(profileLastOnline.isAfter(lastOnline)) {
								lastOnline = profileLastOnline;
							}
						}
						for(Wii wii : wiis) {
							wiiList = wiiList + wii.getName() + "\n";
						}
						if(hasMembers && (member = user.getMember(context.getServer())) != null) {
							embed.setColor(member.getColor());
							embed.setThumbnail(user.getJDAUser().getEffectiveAvatarUrl());
							embed.addField("Username:", user.getJDAUser().getName(), false);
							embed.addField("Discriminator", user.getJDAUser().getDiscriminator(), false);
							//embed.addField("Badges:", "", false);
							embed.addField("ID:", "" + user.getID(), false);
							embed.addField("Nickname:", member.getNickname() != null ? member.getNickname() : "##Not Nicknamed##", false);
							embed.addField("Joined Discord:", date.format(member.getTimeCreated().toInstant().toEpochMilli()), false);
							embed.addField("Joined " + context.getServer().getName() + ":", date.format(member.getTimeJoined().toInstant().toEpochMilli()), false);
							embed.addField("Member for:", readableDuration(TimeUtils.since(member.getTimeJoined().toInstant()), false), false);
							embed.addField("Time Online:", readableDuration(timeOnline, true), false);
							embed.addField(profiles.size() + " registered Profiles:", profileList, false);
							embed.addField(wiis.length + " registered Wiis:", wiiList, false);
						}
						else {
							embed.setThumbnail(user.getJDAUser().getEffectiveAvatarUrl());
							embed.addField("Username:", user.getJDAUser().getName(), false);
							embed.addField("Discriminator", user.getJDAUser().getDiscriminator(), false);
							embed.addField("ID:", "" + user.getID(), false);
							embed.addField("Time Online:", readableDuration(timeOnline, true), false);
							embed.addField(profiles.size() + " registered Profiles:", profileList, false);
							embed.addField(wiis.length + " registered Wiis:", wiiList, false);
							embed.appendDescription("For more information, execute this command in a server the user is in.");
						}
					}
					else if (match instanceof Player) {
						Player profile = (Player) match;
						DiscordUser user = DiscordUser.getDiscordUserTreatingUnknownsAsNobody(context, profile.getDiscord());
						embed.addField("Name:", profile.getName(), false);
						embed.addField("ID:", profile.getID() + "", false);
						embed.addField("FC:", profile.getFriendCode(), false);
						embed.addField("Owner:", user.toDetailedString(), false);
						embed.addField("Time Online:", readableDuration(profile.getOnlineDuration(), true), false);
						embed.addField("First Seen:", date.format(profile.getFirstSeen().toEpochMilli()), false);
						embed.addField("Last Seen:", date.format(profile.getLastOnline().toEpochMilli()), false);
					}
				}
				else if (matches.size() == 0) {
					embed.setColor(Color.RED);
					embed.setTitle("Target not found");
					embed.addField("Target:", lookingFor, true);
				}
				else {
					embed.setTitle("Ambigious target string, supply an ID");
					embed.setColor(Color.RED);
					embed.addField("Target", lookingFor, true);
					embed.addField("Ambiguities", "" + matches.size(), true);
					if(users.size() > 0) {
						embed.appendDescription("Discord users:\n");
						String userList = "";
						for(DiscordUser user : users) {
							Member member;
							if(hasMembers && (member = user.getMember(context.getServer())) != null && member.getNickname() != null) {
								userList = userList + user.toDetailedString() + " AKA " + member.getEffectiveName() + "#" + member.getIdLong() + "\n";
							}
							else {
								userList = userList + user.toDetailedString() + "\n";
							}
						}
						embed.addField("Ambiguous Users:", userList, false);
					}
					if(players.size() > 0) {
						String playerList = "";
						for(Player player : players) {
							playerList = playerList + player.toFullString() + "\n";
						}
						embed.addField("Ambiguous Profiles:", playerList, false);
					}
				}
				embed.setTimestamp(Instant.now());
				context.sendMessage(embed.build());
			}
		}
		return 1;
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
