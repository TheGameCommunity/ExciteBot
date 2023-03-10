package com.gamebuster19901.excite.bot.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.audit.CommandAudit;
import com.gamebuster19901.excite.bot.command.argument.GlobalLiteralArgumentBuilder;
import com.gamebuster19901.excite.bot.command.argument.suggestion.AnyStringSuggestionProvider;
import com.gamebuster19901.excite.util.StacktraceUtil;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;

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
	
	@Deprecated
	public static LiteralArgumentBuilder<CommandContext> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}
	
	public static GlobalLiteralArgumentBuilder<CommandContext> global(String name) {
		return GlobalLiteralArgumentBuilder.literal(name);
	}
	
	public static RequiredArgumentBuilder<CommandContext, String> anyString(String name) {
		return argument(name, StringArgumentType.string()).suggests(new AnyStringSuggestionProvider<>(name));
	}
	
	public static RequiredArgumentBuilder<CommandContext, String> anyStringGreedy(String name) {
		return argument(name, StringArgumentType.greedyString()).suggests(new AnyStringSuggestionProvider<>(name));
	}
	
	public static GlobalLiteralArgumentBuilder<CommandContext> userGlobal(String name) {
		return GlobalLiteralArgumentBuilder.literal(name, true);
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
		List<String> args = getArgs(command);
		if(args.size() > 0) {
			return args.get(args.size() - 1);
		}
		return "";
	}
	
	public static ArrayList<String> getArgs(String command) {
		ArrayList<String> args = new ArrayList<>();
		if(command.indexOf(' ') > 0) {
			String[] split = command.split(Pattern.quote(" "));
			for(int i = 1; i < split.length; i++) {
				String arg = split[i];
				if(!arg.isBlank()) {
					args.add(split[i]);
				}
			}
		}
		return args;
	}
	
	public static int getMatchingIndex(List<String> arguments, Suggestion suggested) {
		return getMatchingIndex(arguments, suggested.getText());
	}
	
	public static int getMatchingIndex(List<String> arguments, String suggested) {
		if(arguments.size() > 0) {
			String arg = arguments.get(arguments.size() - 1);
			String suggestion = suggested;
			if(!(arg.isBlank() || suggestion.isBlank())) {
				if(arg.charAt(0) == suggestion.charAt(0)) {
					int i = 1;
					for(; i < arg.length() && i < suggestion.length(); i++) {
						if(arg.charAt(i) != suggestion.charAt(i)) {
							break;
						}
					}
					return i;
				}
			}
		}
		return -1;
	}
	
}
