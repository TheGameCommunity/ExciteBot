package com.gamebuster19901.excite.bot.command.argument;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.exception.ParseExceptions;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class DiscordUserArgumentType implements ArgumentType<DiscordUser> {
	
	private boolean allowUnknown = false;
	
	private DiscordUserArgumentType(){}
	
	public DiscordUserArgumentType allowUnknown() {
		allowUnknown = true;
		return this;
	}
	
	public static DiscordUserArgumentType user() {return new DiscordUserArgumentType();}
	
	public static DiscordUser getDiscordUser(final CommandContext<?> context, final String name) {
		return context.getArgument(name, DiscordUser.class);
	}
	
	@Override
	public DiscordUser parse(StringReader reader) throws CommandSyntaxException {
		String input = reader.readString();
		DiscordUser[] users = DiscordUser.getDiscordUsersWithUsernameOrID(ConsoleContext.INSTANCE, input);
		if(users.length == 0) {
			if(allowUnknown) {
				return DiscordUser.getDiscordUserIncludingUnknown(ConsoleContext.INSTANCE, input);
			}
			throw ParseExceptions.DISCORD_NOT_FOUND.create(input);
		}
		if(users.length == 1) {
			return users[0];
		}
		throw ParseExceptions.DISCORD_AMBIGUITY.create(input, users);
	}

}
