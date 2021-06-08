package com.gamebuster19901.excite.game.challenge;

public final class InvalidChallenge implements Rewardable {

	public static final InvalidChallenge INSTANCE = new InvalidChallenge();

	private InvalidChallenge() {}
	
	@Override
	public int getReward() {
		return 0;
	}
	
}
