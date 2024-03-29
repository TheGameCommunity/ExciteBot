package com.gamebuster19901.excite.bot.command.argument.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;

public abstract class MatchingSuggestion<T> extends Suggestion {
	private T val;
	
	public MatchingSuggestion(StringRange range, T val) {
		super(range, val.toString());
	}
	
	public MatchingSuggestion(StringRange range, T val, Message tooltip) {
		super(range, val.toString(), tooltip);
	}
	
	@Override
	public abstract String getText();
	public abstract boolean matches(String s);
	public final T getValue() {
		return val;
	}
	public final Class<T> getMatchingType() {
		return (Class<T>) val.getClass();
	}
	
}
