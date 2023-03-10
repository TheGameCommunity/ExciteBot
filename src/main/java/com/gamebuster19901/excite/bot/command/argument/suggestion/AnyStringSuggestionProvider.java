package com.gamebuster19901.excite.bot.command.argument.suggestion;

import java.util.concurrent.CompletableFuture;

import com.gamebuster19901.excite.bot.command.argument.suggestion.builder.ExciteSuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class AnyStringSuggestionProvider<S> implements SuggestionProvider<S> {
	
	private final String name;
	private final boolean greedy;
	
	public AnyStringSuggestionProvider(String name) {
		this(name, false);
	}
	
	public AnyStringSuggestionProvider(String name, boolean greedy) {
		this.name = name;
		this.greedy = greedy;
	}
	
	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		int i = context.getNodes().size() - 1 ;
		ExciteSuggestionsBuilder b = new ExciteSuggestionsBuilder(builder);
		String arg = builder.getRemaining();
		if(builder.getRemaining().isBlank()) {
			System.out.println("1AnyString: " + builder.getRemaining());
			b.suggestAnyString(name);
		}
		else {
			System.out.println("2AnyString: " + builder.getRemaining());
			b.suggestAsMatchable(b.getRemaining());
		}

		builder.add(b);
		return builder.buildFuture();
	}

}
