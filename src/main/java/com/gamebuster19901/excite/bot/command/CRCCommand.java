package com.gamebuster19901.excite.bot.command;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;

import com.gamebuster19901.excite.bot.server.emote.Emote;
import com.gamebuster19901.excite.game.crc.CRCTester;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class CRCCommand {
	public static final int POLYNOMIAL = 0x690CE0EE;

	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(
				Commands.literal("crc")
					.then(Commands.literal("test")
						.then(Commands.argument("data", StringArgumentType.greedyString())
							.executes(context -> {
								return checkCRC(context.getSource(), null, context.getArgument("data", String.class));
							})
						)
					)
					.then(Commands.literal("assert")
						.then(Commands.argument("expected", StringArgumentType.string())
							.then(Commands.argument("data", StringArgumentType.greedyString())
								.executes(context -> {
									String expectedString = context.getArgument("expected", String.class);
									Integer expected = null;
									try {
										if(expectedString.startsWith("0x")) {
											expected = Integer.parseUnsignedInt(expectedString.substring(2), 16);
										}
										else {
											expected = Integer.parseUnsignedInt(expectedString);
										}
									}
									catch(NumberFormatException e) {
										context.getSource().sendMessage("Invalid parameter: " + expectedString + " is not an unsigned base 16 or base 10 integer.");
										return -2;
									}
									return checkCRC(context.getSource(), expected, context.getArgument("data", String.class));
								})
							)
						)
					)
		);
	}
	
	
	public static int checkCRC(MessageContext context, Integer expected, String data) {
		boolean fromFile = false;
		File f = Paths.get(data).toFile();
		Integer crc = null;
		try {
			if(f.exists()) {
				crc = new CRCTester(new FileInputStream(f)).test();
				fromFile = true;
			}
		}
		catch(Throwable t) {
			throw new RuntimeException(t);
		}
		if(crc == null) {
			crc = new CRCTester(data.getBytes()).test();
		}
		
		if(context.isOperator()) {
			if(expected != null) {
				if(crc.equals(expected)) {
					context.sendMessage(Emote.getEmote("verified") + fromFile(fromFile) + "`" + data + "` matches " + asHexInfo(expected));
				}
				else {
					context.sendMessage(Emote.getEmote("banned") + fromFile(fromFile) + "`" + data + "` does not match " + asHexInfo(expected) + ".\n\nThe actual value was " + asHexInfo(crc));
				}
			}
			else {
				context.sendMessage(Emote.getEmote("info") + fromFile(fromFile) + "`" + data + "` CRCd with polynomial " + asHexInfo(POLYNOMIAL) + " is\n\n" + asHexInfo(crc));
			}
		}
		else {
			context.sendMessage("You do not have permission to execute that command");
		}
		return 1;
	}
	
	private static final String asHexInfo(int integer) {
		return "`0x" + Integer.toHexString(integer).toUpperCase() + "`(`" + Integer.toUnsignedString(integer)+ "`)";
	}
	
	private static final String fromFile(boolean fromFile) {
		if(fromFile) {
			return " Data from ";
		}
		return "";
	}
	
}
