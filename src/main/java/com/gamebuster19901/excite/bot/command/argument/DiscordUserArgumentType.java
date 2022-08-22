package com.gamebuster19901.excite.bot.command.argument;

import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.command.exception.ParseExceptions;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.Nobody;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;

import static com.gamebuster19901.excite.bot.command.argument.UserObtainer.*;
import static com.gamebuster19901.excite.bot.command.argument.DiscordUserArgumentType.UnknownType.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class DiscordUserArgumentType implements ArgumentType<DiscordUser> {
	
	private UnknownType unknownType = FULLY_KNOWN;
	private boolean treatUnknownsAsNobody = false;
	private UserObtainer requiredContext = UNCHANGED;
	
	private DiscordUserArgumentType(){}
	
	public DiscordUserArgumentType setUnknown(UnknownType unknownType) {
		unknownType = unknownType;
		return this;
	}
	
	public DiscordUserArgumentType treatUnknownsAsNobody() {
		if(unknownType != FULLY_KNOWN) {
			treatUnknownsAsNobody = true;
			return this;
		}
		else {
			throw new IllegalStateException("Unknowns are not allowed, allow unknowns before attempting to treat them as nobody.");
		}
	}
	
	public DiscordUserArgumentType of(UserObtainer type) {
		requiredContext = type;
		return this;
	}
	
	public static DiscordUserArgumentType user() {return new DiscordUserArgumentType();}
	
	public static DiscordUser getDiscordUser(final CommandContext<?> context, final String name) {
		return context.getArgument(name, DiscordUser.class);
	}
	
	@Override
	public <S> DiscordUser parse(S source, StringReader reader) throws CommandSyntaxException {
		MessageContext context = (MessageContext) source;
		if(requiredContext == UserObtainer.JDA_ONLY) {
			source = (S) ConsoleContext.INSTANCE;
		}
		if(requiredContext == UserObtainer.GUILD_CHANNEL_ONLY) {
			if(!(context.isDiscordContext())) {
				throw ParseExceptions.DISCORD_CONTEXT_REQUIRED.create();
			}
			else if (!context.isGuildMessage()) {
				throw ParseExceptions.DISCORD_CHANNEL_REQUIRED.create();
			}
		}
		String input;
		if(reader.peek() == '"') {
			input = Commands.readQuotedString(reader);
		}
		else {
			input = Commands.readString(reader);
		}
		DiscordUser[] users = DiscordUser.getUnknownDiscordUsersWithUsernameOrID((MessageContext)source, input);
		if(users.length == 0) {
			if(unknownType != FULLY_KNOWN) {
				if(treatUnknownsAsNobody) {
					return Nobody.INSTANCE;
				}
				DiscordUser user = DiscordUser.getDiscordUserIncludingUnknown((MessageContext)source, input);
				if(unknownType == KNOWN_ID) {
					if(user instanceof UnknownDiscordUser) {
						if(!((UnknownDiscordUser) user).hasID() ) {
							throw ParseExceptions.DISCORD_NOT_FOUND.create(input);
						}
					}
				}
				return user;
			}
			throw ParseExceptions.DISCORD_NOT_FOUND.create(input);
		}
		if(users.length == 1) {
			return users[0];
		}
		throw ParseExceptions.DISCORD_AMBIGUITY.create(input, users);
	}
	
	public static enum UnknownType {
		/**
		 * Username and ID are unknown
		 */
		UNKNOWN_ID,
		
		/**
		 * Username is unknown, but ID is known
		 */
		KNOWN_ID,
		
		/**
		 * Username and ID are fully known
		 */
		FULLY_KNOWN
	}

}
