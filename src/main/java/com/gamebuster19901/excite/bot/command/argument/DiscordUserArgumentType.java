package com.gamebuster19901.excite.bot.command.argument;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

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

public class DiscordUserArgumentType implements ArgumentType<User>{
	
	@Override
	public <S> User parse(S context, StringReader reader) throws CommandSyntaxException {
		String s = Commands.readString(reader);
		long id;
			if(s.length() > 3) {
				if(s.startsWith("<@")) {
					try {
						id = Long.parseLong(s.substring(StringUtils.lastIndexOf(s, '(') + 1, s.length() - 2));
					}
					catch(Throwable t) {
						t.printStackTrace();
						throw t;
					}
				}
				else {
					id = Long.parseLong(s);
				}
				return Main.discordBot.jda.retrieveUserById(id).complete();
			}
		throw ParseExceptions.DISCORD_NOT_FOUND.create(s);
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
	
}