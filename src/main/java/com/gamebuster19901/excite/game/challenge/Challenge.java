package com.gamebuster19901.excite.game.challenge;

import com.gamebuster19901.excite.game.Bot;
import com.gamebuster19901.excite.game.Course;
import com.gamebuster19901.excite.util.file.File;

public class Challenge implements Rewardable {

	public static final int BOT = 0xDC8;
	public static final int REWARD = 0xDD0;
	public static final int BEGIN_TICK_DATA = 0xDD4;
	
	private final Course course;
	private final short reward;
	private final Bot bot;
	private byte[] challengeData;
	
	public Challenge(File file) {
		
	}

	@Override
	public int getReward() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
