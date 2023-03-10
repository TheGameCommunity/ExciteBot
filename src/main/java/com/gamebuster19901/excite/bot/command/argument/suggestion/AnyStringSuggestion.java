package com.gamebuster19901.excite.bot.command.argument.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;

public class AnyStringSuggestion extends Suggestion implements MatchingSuggestion {

	public AnyStringSuggestion(StringRange range, String text) {
		super(range, text);
	}
	
	public AnyStringSuggestion(StringRange range, String text, Message tooltip) {
		super(range, text, tooltip);
	}

	@Override
	public boolean matches(String s) {
		return s != null && !s.isBlank();
	}

}
