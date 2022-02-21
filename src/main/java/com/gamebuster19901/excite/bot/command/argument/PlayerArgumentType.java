package com.gamebuster19901.excite.bot.command.argument;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.UnknownPlayer;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class PlayerArgumentType implements ArgumentType<Player> {
	
	private boolean allowUnknown = true;
	
	private PlayerArgumentType() {}
	
	public PlayerArgumentType allowUnknown() {
		this.allowUnknown = true;
		return this;
	}
	
	public static Player getPlayer(final CommandContext<?> context, final String name) {
		return context.getArgument(name, Player.class);
	}
	
	@Override
	public Player parse(StringReader reader) throws CommandSyntaxException {
		String input = reader.readQuotedString();
		Player[] players = Player.getPlayersByAnyIdentifier(ConsoleContext.INSTANCE, reader.readQuotedString());
		if(players.length == 0) {
			if(allowUnknown) {
				return new UnknownPlayer(input);
			}
			throw new NullPointerException("No player found (" + input + ")");
		}
		if(players.length == 1) {
			return players[0];
		}
		throw new IllegalStateException("Multiple profiles named " + input + ". Supply an ID instead.");
	}

}
