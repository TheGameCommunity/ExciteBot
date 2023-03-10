package com.gamebuster19901.excite.bot.command.argument.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;

public class MatchingStringSuggestion extends Suggestion implements MatchingSuggestion {

	public MatchingStringSuggestion(StringRange range, String text) {
		super(range, text);
	}
	
	public MatchingStringSuggestion(StringRange range, String text, Message tooltip) {
		super(range, text, tooltip);
	}

	@Override
	public boolean matches(String s) {
		if(s.isBlank()) {
			return false;
		}
		return getText().toLowerCase().endsWith(s.toLowerCase());
	}

}
