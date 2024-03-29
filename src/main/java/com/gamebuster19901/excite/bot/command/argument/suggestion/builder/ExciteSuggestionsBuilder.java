package com.gamebuster19901.excite.bot.command.argument.suggestion.builder;

import com.gamebuster19901.excite.bot.command.argument.suggestion.AnyStringSuggestion;
import com.gamebuster19901.excite.bot.command.argument.suggestion.MatchingStringSuggestion;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class ExciteSuggestionsBuilder extends SuggestionsBuilder {

	public ExciteSuggestionsBuilder(SuggestionsBuilder builder) {
		this(builder.getInput(), builder.getStart());
	}
	
	public ExciteSuggestionsBuilder(String input, int start) {
		super(input, start);
	}
	
	@Override
	public ExciteSuggestionsBuilder suggest(final Suggestion suggestion) {
		return (ExciteSuggestionsBuilder) super.suggest(suggestion); //super always returns this
	}
	
	public <S> ExciteSuggestionsBuilder suggestAnyString(String name) {
		Thread.dumpStack();
		return suggest(new AnyStringSuggestion(getDefaultRange(), "<" + name + ">"));
	}
	
	public <S> ExciteSuggestionsBuilder suggestAnyString(String name, Message tooltip) {
		Thread.dumpStack();
		return suggest(new AnyStringSuggestion(getDefaultRange(), "<" + name + ">", tooltip));
	}
	
	public <S> ExciteSuggestionsBuilder suggestAsMatchable(String text) {
		return suggest(new MatchingStringSuggestion(getDefaultRange(), text));
	}
	
	protected StringRange getDefaultRange() {
		return StringRange.between(getStart(), getInput().length());
	}

}
