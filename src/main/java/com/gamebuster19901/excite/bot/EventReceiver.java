package com.gamebuster19901.excite.bot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.command.argument.GlobalNode;
import com.gamebuster19901.excite.bot.command.argument.suggestion.MatchingSuggestion;
import com.gamebuster19901.excite.bot.command.interaction.Interactions;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class EventReceiver extends ListenerAdapter {

	public static final char DATA_ESCAPE = 0x10;
	
	private final Sub sub = new Sub();
	
	@Override
	public void onGenericEvent(GenericEvent ge) {
		if(ge instanceof GenericInteractionCreateEvent) {
			GenericInteractionCreateEvent e = (GenericInteractionCreateEvent) ge;
			User user = e.getUser();
			try {
				if(!DiscordUser.isKnown(e.getUser())) {
					DiscordUser.addUser(e.getUser());
				}
			}
			catch(Throwable t) {
				if(e.getInteraction() instanceof IReplyCallback) {
					new CommandContext(e.getInteraction()).replyMessage(StacktraceUtil.getStackTrace(t));
				}
			}
		}
		sub.onEvent(ge);
	}
	

	
	private static final class Sub extends ListenerAdapter {
		
		public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
			StringBuilder c = new StringBuilder(e.getName());
			for(OptionMapping arg : e.getOptions()) {
				c.append(' ');
				c.append(arg.getAsString().trim());
			}
			CommandContext context = new CommandContext(e);
			try {
				Commands.DISPATCHER.getDispatcher().execute(c.toString() , context);
			} catch (Throwable t) {
				if(t.getMessage() != null && !t.getMessage().isBlank()) {
					context.sendMessage(t.toString());
				}
				else {
					context.sendMessage(t.toString());
				}
				if(!(t instanceof CommandSyntaxException)) {
					throw new RuntimeException(t);
				}
			}
		}
		
		@Override
		public void onGuildReady(GuildReadyEvent e) {
			List<CommandData> commands = new ArrayList<>();
			Commands.DISPATCHER.getDispatcher().getRoot().getChildren().forEach((command) -> {
				if(!Main.discordBot.isDev()) {
					if(command instanceof GlobalNode) {
						return; //Don't register global commands as guild commands if we're not in a dev environment
					}
				}
				SlashCommandData data = net.dv8tion.jda.api.interactions.commands.build.Commands.slash(command.getName(), command.getUsageText());
				
				if(command.getChildren().size() > 0) {
					data.addOption(OptionType.STRING, "arguments", "arguments", true, true);
				}
				
				commands.add(data);
				System.out.println(command.getUsageText());
			});
			Interactions.DISPATCHER.getDispatcher(); //initialize interactions
			
			e.getGuild().updateCommands().addCommands(commands).queue();
		}
		
		
		@Override
		@SuppressWarnings("unlikely-arg-type")
		public void onCommandAutoCompleteInteraction(final CommandAutoCompleteInteractionEvent e) {
			
			final String command = (e.getName() + " " + e.getFocusedOption().getValue());
			final List<String> arguments = Commands.getArgs(command);
			String lastArg = "";
			if(arguments.size() > 0) {
				lastArg = arguments.get(arguments.size() - 1);
				arguments.remove(arguments.size() - 1);
			}
			
			
			System.err.println(command + "-----");
			

			CommandContext<CommandAutoCompleteInteractionEvent> context = new CommandContext<>(e);

			ParseResults<CommandContext> parseResults = Commands.DISPATCHER.getDispatcher().parse(command, context);
			ParseResults<CommandContext> spacedParseResults = Commands.DISPATCHER.getDispatcher().parse(command + " ", context);
			List<Suggestion> suggestions = new ArrayList<>();
			try {
				List<Suggestion> foundSuggestions = new ArrayList<>();
				Suggestion completedSuggestion = null;
				for(Suggestion suggestion : Commands.DISPATCHER.getDispatcher().getCompletionSuggestions(parseResults, command.length()).get().getList()) {
					if(lastArg.equalsIgnoreCase(suggestion.getText()) || (suggestion instanceof MatchingSuggestion && ((MatchingSuggestion) suggestion).matches(lastArg))) {
						completedSuggestion = suggestion;
						break;
					}
					foundSuggestions.add(suggestion);
					System.out.println(lastArg + " != " + suggestion.getText());
				}
				suggestions.addAll(foundSuggestions);
				if(completedSuggestion != null) {
					System.out.println("EMEPTYASHDFPIOWHAEPHF AWIEUFHOUIWHQEF");
					for(Suggestion suggestion : Commands.DISPATCHER.getDispatcher().getCompletionSuggestions(spacedParseResults, command.length() + 1).get().getList()) {
						suggestions.add(suggestion);
					}
					if(suggestions.isEmpty() && !foundSuggestions.isEmpty()) {
						
					}
					else {
						suggestions.add(completedSuggestion);
					}
					
					//suggestions.add(completedSuggestion);
				}
			} catch (InterruptedException | ExecutionException e1) {
				throw new RuntimeException(e1);
			}
			String completedArgs = getCompletedArgs(arguments);
			List<String> returnedSuggestions = new ArrayList<String>();
				

			if(suggestions.size() > 25) {
				suggestions = suggestions.subList(0, 25);
			}
			a:
			{
			break a;
			
			}
			
			/*
			for(int s = 0; s < suggestions.size(); s++) {
				Suggestion suggestion = suggestions.get(s);
				StringBuilder returnedSuggestionsBuilder = new StringBuilder();
				int index = Commands.getMatchingIndex(arguments, suggestion);
				if(index > -1) {
					for(int i = 0; i < arguments.size() - 1; i++) {
						returnedSuggestionsBuilder.append(arguments.get(i));
						returnedSuggestionsBuilder.append(' ');
					}
					returnedSuggestionsBuilder.append(suggestion.getText());
				}
				else {
					for(int i = 0; i < arguments.size(); i++) {
						returnedSuggestionsBuilder.append(arguments.get(i));
						returnedSuggestionsBuilder.append(' ');
					}
					returnedSuggestionsBuilder.append(suggestion.getText());
				}
				returnedSuggestions.add(returnedSuggestionsBuilder.toString());
			}
			e.replyChoiceStrings(returnedSuggestions).queue();
			*/
			
			for(Suggestion suggestion : suggestions) {
				if(suggestion instanceof MatchingSuggestion) {
					if(((MatchingSuggestion) suggestion).matches(lastArg)) {
						System.out.println(suggestion + " matches " + lastArg);
						returnedSuggestions.add(completedArgs + " " + suggestion.getText());
					}
					else {
						returnedSuggestions.add(completedArgs);
					}
				}
				else {
					returnedSuggestions.add(completedArgs + " " + suggestion.getText());
				}
			}
			
			e.replyChoiceStrings(returnedSuggestions).queue();
		}
		
	}
		
	private static final String getCompletedArgs(List<String> args) {
		StringBuilder ret = new StringBuilder();
		Iterator<String> i = args.iterator();
		while(i.hasNext()) {
			ret = ret.append(i.next());
			if(i.hasNext()) {
				ret.append(' ');
			}
		}
		return ret.toString();
	}
	
}
