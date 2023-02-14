package com.gamebuster19901.excite.bot.user.console;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.user.ConsoleUser;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.RestAction;

public class ConsoleChannel implements PrivateChannel {

	@Override
	public long getLatestMessageIdLong() {
		return 0;
	}

	@Override
	public boolean canTalk() {
		return true;
	}

	@Override
	public ChannelType getType() {
		return ChannelType.PRIVATE;
	}

	@Override
	public JDA getJDA() {
		return Main.discordBot.jda;
	}

	@Override
	public RestAction<Void> delete() {
		return new CustomRestAction<Void>(() -> {return null;});
	}

	@Override
	public long getIdLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public User getUser() {
		return ConsoleUser.getConsoleUser();
	}

	@Override
	public RestAction<User> retrieveUser() {
		return new CustomRestAction<User>(() -> {return ConsoleUser.getConsoleUser();});
	}

	@Override
	public String getName() {
		return "CONSOLE";
	}

}
