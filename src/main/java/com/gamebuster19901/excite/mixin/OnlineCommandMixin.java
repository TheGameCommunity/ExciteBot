package com.gamebuster19901.excite.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.command.OnlineCommand;
import com.mojang.brigadier.context.CommandContext;

@Mixin(OnlineCommand.class)
public class OnlineCommandMixin extends OnlineCommand{
	
	@Overwrite
	@SuppressWarnings("rawtypes")
	public static int sendResponse(MessageContext context, CommandContext<MessageContext> cmdContext) {
		context.sendMessage("Overwrite test mixin successful!");
		return 1;
	}
	
}
