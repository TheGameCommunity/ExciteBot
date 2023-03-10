package com.gamebuster19901.excite.bot.command.argument;

import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class GlobalLiteralArgumentBuilder<S> extends LiteralArgumentBuilder<S> {

	private final boolean privateNode;
	private SuggestionProvider<S> suggestionProvider = null;
	
	protected GlobalLiteralArgumentBuilder(String literal, boolean privateNode) {
		super(literal);
		this.privateNode = privateNode;
	}
	
	protected GlobalLiteralArgumentBuilder(String literal) {
		this(literal, false);
	}
	
	public static <S> GlobalLiteralArgumentBuilder<S> literal(final String name) {
		return new GlobalLiteralArgumentBuilder<>(name);
	}
	
	public static <S> GlobalLiteralArgumentBuilder<S> literal(final String name, final boolean privateNode) {
		return new GlobalLiteralArgumentBuilder<>(name);
	}
	
	public GlobalLiteralArgumentBuilder<S> suggests(final SuggestionProvider<S> provider) {
		this.suggestionProvider = provider;
		return getThis();
	}
	
	public SuggestionProvider<S> getSuggestionsProvider() {
		return suggestionProvider;
	}
	
	@Override
	protected GlobalLiteralArgumentBuilder<S> getThis() {
		return this;
	}

	@Override
	public GlobalLiteralCommandNode<S> build() {
		final GlobalLiteralCommandNode<S> result = new GlobalLiteralCommandNode<>(getLiteral(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork());
		
		for(final CommandNode<S> argument : getArguments()) {
			result.addChild(argument);
		}
		
		return result;
	}
	
	public static final class GlobalLiteralCommandNode<S> extends LiteralCommandNode<S> implements GlobalNode, PrivateNode {

		public GlobalLiteralCommandNode(String literal, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
			super(literal, command, requirement, redirect, modifier, forks);
		}
		
	}
	
}