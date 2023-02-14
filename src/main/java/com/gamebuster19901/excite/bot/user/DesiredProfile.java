package com.gamebuster19901.excite.bot.user;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import com.gamebuster19901.excite.Player;

import net.dv8tion.jda.api.entities.User;

public class DesiredProfile {

	public static final String validPasswordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ123456789,.!?";
	
	private final User requester;
	private final Player desiredProfile;
	private final Random random = new Random();
	private final String registrationCode = generateRegistrationCode();
	private final Instant registrationTimeout = Instant.now().plus(Duration.ofMinutes(5));
	
	public DesiredProfile(User requester, Player player) {
		this.requester = requester;
		this.desiredProfile = player;
		if(player.getDiscord() != 0) {
			throw new IllegalArgumentException(player.toString() + " is already registered!");
		}
	}
	
	public User getRequester() {
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
		desiredProfile.setDiscord(requester.getIdLong());
		DiscordUser.sendMessage(requester, desiredProfile.toFullString() + " successfully registered!");
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
