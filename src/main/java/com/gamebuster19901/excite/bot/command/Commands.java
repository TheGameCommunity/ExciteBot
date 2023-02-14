package com.gamebuster19901.excite.bot.command;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.audit.CommandAudit;
import com.gamebuster19901.excite.util.StacktraceUtil;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@SuppressWarnings("rawtypes")
public class Commands {
	private final CommandDispatcher<CommandContext> dispatcher = new CommandDispatcher<>();
	public static final Commands DISPATCHER = new Commands();
	
	public Commands() {
		OnlineCommand.register(dispatcher);
		WhoIsCommand.register(dispatcher);
		BanCommand.register(dispatcher);
		PardonCommand.register(dispatcher);
		RegisterCommand.register(dispatcher);
		NotifyCommand.register(dispatcher);
		StopCommand.register(dispatcher);
		HelpCommand.register(dispatcher);
		RestartCommand.register(dispatcher);
		IconDumpCommand.register(dispatcher);
		GameDataCommand.register(dispatcher);
		RankCommand.register(dispatcher);
		ChangelogCommand.register(dispatcher);
		InsertCommand.register(dispatcher);
		ArchiveCommand.register(dispatcher);
		CRCCommand.register(dispatcher);
		Debug.register(dispatcher);
	}
	
	public void handleCommand(String command) {
		CommandContext context = new CommandContext(Main.CONSOLE);
		try {
			CommandAudit.addCommandAudit(context, command);
			this.dispatcher.execute(command, context);
		}
		catch (CommandSyntaxException e) {
			context.sendMessage(e.getRawMessage().getString());
		}
		catch(Throwable t) {
			context.sendMessage(StacktraceUtil.getStackTrace(t));
			if(!context.isConsoleMessage()) {
				t.printStackTrace();
			}
			if(t instanceof Error) {
				throw t;
			}
		}
	}
	
	public static LiteralArgumentBuilder<CommandContext> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}
	
	public static <T> RequiredArgumentBuilder<CommandContext, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
	public CommandDispatcher<CommandContext> getDispatcher() {
		return this.dispatcher;
	}
	
	public static boolean isValidPrefix(String prefix) {
		if(prefix == null || prefix.isEmpty()) {
			return false;
		}
		for(int c : prefix.toCharArray()) {
			if(Character.isWhitespace(c) || Character.isSupplementaryCodePoint(c) || Character.isISOControl(c) || c == '@' || c == '#' || c == '`') {
				return false;
			}
		}
		return true;
	}
	
	public static String readString(StringReader reader) {
		StringBuilder ret = new StringBuilder("");
		while(reader.canRead() && !Character.isSpaceChar(reader.peek())) {
			ret.append(reader.read());
		}
		return ret.toString();
	}
	
	public static String readQuotedString(StringReader reader) throws CommandSyntaxException {
		StringBuilder ret = new StringBuilder("");
		if(reader.canRead()) {
			if(reader.peek() == '"') {
				reader.read();
			}
			else {
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(reader);
			}
		}
		boolean foundEndQuote = false;
		while(reader.canRead()) {
			char c = reader.read();
			if(c == '"') {
				foundEndQuote = true;
				break;
			}
			ret.append(c);
		}
		if(!foundEndQuote) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext(reader);
		}
		return ret.toString();
	}
	
	public static String lastArgOf(String command) {
		if(command.indexOf(' ') > 0) {
			return command.substring(command.lastIndexOf(' ') + 1);
		}
		return "";
	}
	
}
