package com.gamebuster19901.excite.bot.command.argument;

import java.util.concurrent.CompletableFuture;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.UnknownPlayer;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.exception.ParseExceptions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class PlayerArgumentType implements ArgumentType<Player> {
	
	private boolean allowUnknown = false;
	
	private PlayerArgumentType() {}
	
	public PlayerArgumentType allowUnknown() {
		this.allowUnknown = true;
		return this;
	}
	
	public static PlayerArgumentType player() {
		return new PlayerArgumentType();
	}
	
	@Override
	public <S> Player parse(S source, StringReader reader) throws CommandSyntaxException {
		String input;
		if(reader.peek() == '"') {
			input = Commands.readQuotedString(reader);
		}
		else {
			input = Commands.readString(reader);
		}
		Player[] players = Player.getPlayersByAnyIdentifier(ConsoleContext.INSTANCE, input);
		if(players.length == 0) {
			if(allowUnknown) {
				return new UnknownPlayer(input);
			}
			throw ParseExceptions.PLAYER_NOT_FOUND.create(input);
		}
		if(players.length == 1) {
			return players[0];
		}
		throw ParseExceptions.PLAYER_AMBIGUITY.create(input, players);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(com.mojang.brigadier.context.CommandContext<S> context, SuggestionsBuilder builder) {
		Player[] players = Player.searchPlayers((CommandContext) context.getSource(), Commands.lastArgOf(builder.getInput()));
		for(Player p : players) {
			builder.suggest(p.getIdentifierName());
		}
		return builder.buildFuture();
	}
	
}
