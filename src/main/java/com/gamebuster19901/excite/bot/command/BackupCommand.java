package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.backup.Backup;
import com.mojang.brigadier.CommandDispatcher;

public class BackupCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("backup").executes((context) -> {
			return Backup.backup(context.getSource());
		}));
	}

}
