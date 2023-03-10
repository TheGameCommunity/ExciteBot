package com.gamebuster19901.excite.bot.command.argument.suggestion;

import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;

import net.dv8tion.jda.api.entities.User;

public class DiscordUserSuggestion extends MatchingSuggestion<User> {

	public DiscordUserSuggestion(StringRange range, User val) {
		super(range, val);
	}
	
	public DiscordUserSuggestion(StringRange range, User val, Message tooltip) {
		super(range, val, tooltip);
	}

	@Override
	public boolean matches(String s) {
		User user = getValue();
		if(s.isBlank()) {
			return false;
		}
		if((user.getIdLong() + "").startsWith(s)) {
			return true;
		}
		else if(user.getAsTag().startsWith(s)) {
			return true;
		}
		else if(DiscordUser.toSuggestionString(user).startsWith(s)) {
			return true;
		}
		return false;
	}

	@Override
	public String getText() {
		return DiscordUser.toDetailedString(getValue());
	}

}
