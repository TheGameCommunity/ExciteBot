package com.gamebuster19901.excite.bot.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class ChannelArgumentType implements ArgumentType<MessageChannelObtainer>{
	
	public static final ChannelArgumentType TEXT_CHANNEL = new ChannelArgumentType();
	
	public ChannelArgumentType() {}
	
	@Override
	public MessageChannelObtainer parse(StringReader reader) throws CommandSyntaxException {
		return new MessageChannelObtainer(reader);
	}
	
}
