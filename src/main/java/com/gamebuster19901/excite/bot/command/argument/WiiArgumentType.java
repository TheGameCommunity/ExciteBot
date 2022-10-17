package com.gamebuster19901.excite.bot.command.argument;

import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.command.exception.ParseExceptions;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.bot.user.Wii.InvalidWii;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class WiiArgumentType implements ArgumentType<Wii> {

	public static final String IGNORE_CHARS = " -";
	
	private boolean allowUnknown = false;
	
	private WiiArgumentType() {}
	
	public WiiArgumentType allowUnknown() {
		this.allowUnknown = true;
		return this;
	}
	
	public static WiiArgumentType wii() {
		return new WiiArgumentType();
	}
	
	@Override
	public <S> Wii parse(S context, StringReader reader) throws CommandSyntaxException {
		String input;
		if(reader.peek() == '"') {
			input = Commands.readQuotedString(reader);
		}
		else {
			input = Commands.readString(reader);
		}
		
		Wii wii = Wii.getWii(input);
		if(wii instanceof InvalidWii) {
			throw ParseExceptions.WII_INVALID.create(input);
		}
		if(allowUnknown && !wii.isKnown()) {
			throw ParseExceptions.WII_NOT_FOUND.create(wii.getWiiCode());
		}
		
		return wii;
		
	}

}
