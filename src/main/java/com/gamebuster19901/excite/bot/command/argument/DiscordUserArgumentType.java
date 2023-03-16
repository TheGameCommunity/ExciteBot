package com.gamebuster19901.excite.bot.command.argument;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.command.exception.ParseExceptions;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class DiscordUserArgumentType implements ArgumentType<User>{
	
	@Override
	public <S> User parse(S context, StringReader reader) throws CommandSyntaxException {
		int beginIndex = reader.getCursor();
		User user = getUser(reader);
		int endIndex = reader.getCursor();
		if(user != null) {
			return user;
		}
		reader.setCursor(beginIndex);
		int amount = endIndex - beginIndex;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < amount; i++) {
			sb.append(reader.read());
		}
		throw ParseExceptions.DISCORD_NOT_FOUND.create(sb);
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(com.mojang.brigadier.context.CommandContext<S> context, SuggestionsBuilder builder) {
		CommandContext<?> c = (CommandContext<?>) context.getSource();
		Guild server = c.getServer();
		System.err.println(Commands.lastArgOf(builder.getInput()));
		if(server != null) {
			server.findMembers((member) -> {
				return (
						(member.getIdLong() + "").startsWith(Commands.lastArgOf(builder.getInput())) ||
						(member.getEffectiveName().toLowerCase() + "#" + member.getUser().getDiscriminator()).startsWith(Commands.lastArgOf(builder.getInput().toLowerCase().replace("@", "")))
				);
			}).onSuccess((foundMembers) -> {
				for(Member member : foundMembers) {
					builder.suggest(DiscordUser.toSuggestionString(member.getUser()));
				}
			});
		}
		return builder.buildFuture();
	}
	
	@Nullable
	public User getUser(StringReader s) {
		long id;
		int cursor = s.getCursor();
		StringBuilder sb = new StringBuilder();
		if(s.canRead(2)) {
			sb.append(s.read());
			sb.append(s.read());
			if(sb.toString().equals("<@")) {
				while(s.canRead()) {
					char c = s.read();
					sb.append(c);
					if(c == '>') {
						try {
							String userString = sb.toString();
							id = Long.parseLong(userString.substring(StringUtils.lastIndexOf(userString, '(') + 1, userString.length() - 2));
							return Main.discordBot.jda.retrieveUserById(id).complete();
						}
						catch(ErrorResponseException | NumberFormatException e) {
							
						}
					}
				}
			}
		}
		return null;
	}
	
}