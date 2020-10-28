package com.gamebuster19901.excite.bot.user;

import java.io.IOError;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.util.StacktraceUtil;

public class DesiredProfile {

	private static final String validPasswordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ123456789,.!?";
	
	private final DiscordUser requester;
	private final Player desiredProfile;
	private final Random random = new Random();
	private final String registrationCode = generateRegistrationCode();
	private final Instant registrationTimeout = Instant.now().plus(Duration.ofMinutes(5));
	
	public DesiredProfile(DiscordUser requester, Player player) {
		this.requester = requester;
		this.desiredProfile = player;
		if(player.getDiscord() != -1) {
			throw new IllegalArgumentException(player.toString() + " is already registered!");
		}
	}
	
	public DiscordUser getRequester() {
		return requester;
	}
	
	public Player getDesiredProfile() {
		return desiredProfile;
	}
	
	public String getRegistrationCode() {
		return registrationCode;
	}
	
	public Instant getRegistrationTimeout() {
		return registrationTimeout;
	}
	
	public boolean isVerified() {
		return desiredProfile.getName().equals(registrationCode);
	}
	
	public void register() {
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, Table.PLAYERS, DiscordUser.DISCORD_ID, requester.getId() + "", desiredProfile.idEqualsThis());
		} catch (SQLException e) {
			IOError IOError = new IOError(e);
			getRequester().sendMessage("Unable to register profile " + desiredProfile.getPlayerID());
			getRequester().sendMessage(StacktraceUtil.getStackTrace(IOError));
			throw IOError;
		}
	}
	
	private final String generateRegistrationCode() {
		char[] sequence = new char[7];
		for(int i = 0; i < sequence.length; i++) {
			sequence[i] = validPasswordChars.charAt(random.nextInt(validPasswordChars.length()));
		}
		return new String(sequence);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DesiredProfile) {
			return ((DesiredProfile) o).desiredProfile.equals(desiredProfile);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return desiredProfile.hashCode();
	}
}
