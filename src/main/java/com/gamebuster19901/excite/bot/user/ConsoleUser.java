package com.gamebuster19901.excite.bot.user;

import java.io.IOError;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Set;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.sql.DatabaseConnection;

import net.dv8tion.jda.api.entities.User;

public class ConsoleUser extends UnloadedDiscordUser {
	
	private final String name = "CONSOLE";
	
	public ConsoleUser() {
		super(1);
	}
	
	@Override
	public void initConnection() {
		try {
			connection = new DatabaseConnection();
		} catch (IOException | SQLException e) {
			throw new IOError(e);
		}
	}
	
	@Override
	public String getName() {
		return toDetailedString();
	}
	
	@Override
	public User getJDAUser() {
		throw new AssertionError();
	}
	
/*	@Override
	@SuppressWarnings("rawtypes")
	public DiscordBan ban(MessageContext context, Duration duration, String reason) {
		throw new AssertionError();
	}*/
	
	@Override
	@SuppressWarnings("rawtypes")
	public Set<Player> getProfiles(MessageContext context) {
		throw new AssertionError();
	}
/*	
	@Override
	public boolean isBanned() {
		return false;
	}
	
	@Override
	public int getUnpardonedBanCount() {
		throw new AssertionError();
	}
	
	@Override
	public int getTotalBanCount() {
		throw new AssertionError();
	}
*/	
	@Override
	public void setNotifyThreshold(int threshold) {
		throw new AssertionError();
	}
	
	@Override
	public void setNotifyFrequency(Duration frequency) {
		throw new AssertionError();
	}
	
	@Override
	public void setNotifyContinuously(boolean continuous) {
		throw new AssertionError();
	}
	
	@Override
	public String requestRegistration(Player desiredProfile) {
		throw new AssertionError();
	}
	
	@Override
	public boolean requestingRegistration() {
		throw new AssertionError();
	}
	
	@Override
	public void sendMessage(String message) {
		System.out.println(message);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void sendMessage(MessageContext context, String message) {
		System.out.println(message);
	}
	
	@Override
	public String toString() {
		return name + "(" + getID() + ")"; 
	}
	
	@Override
	public String toDetailedString() {
		return toString();
	}

	public static final ConsoleUser getConsoleUser() {
		return Main.CONSOLE;
	}
	
}
