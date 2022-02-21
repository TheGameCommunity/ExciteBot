package com.gamebuster19901.excite.bot.command.argument;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.Named;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class NamedArgumentType implements ArgumentType<Named> {
	
	private boolean lookForDiscordUsers = true;
	private boolean lookForProfiles = true;
	private boolean lookForWiis = true;
	
	@SuppressWarnings("unchecked")
	public NamedArgumentType lookingFor(Class<? extends Named>... types) throws CommandSyntaxException {
		lookForDiscordUsers = false;
		lookForProfiles = false;
		lookForWiis = false;
		for(Class<? extends Named> t : types) {
			if(t == DiscordUser.class) {
				lookForDiscordUsers = true;
			}
			else if (t == Player.class) {
				lookForProfiles = true;
			}
			else if (t == Wii.class) {
				lookForWiis = true;
			}
			else {
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Internal exception: Unexpected subclass of Named: " + t.getCanonicalName());
			}
		}
		if(lookForDiscordUsers || lookForProfiles || lookForWiis) {
			return this;
		}
		throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Internal exception: not looking for any subclass of Named?!");
	}
	
	@Override
	public Named parse(StringReader reader) throws CommandSyntaxException {
		DiscordUser.getDiscordUsersWithUsernameOrID(ConsoleContext.INSTANCE, reader.readQuotedString());
		return null;
	}

}
