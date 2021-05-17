package com.gamebuster19901.excite.game.challenge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import com.gamebuster19901.excite.game.Bot;
import com.gamebuster19901.excite.game.Course;
import com.gamebuster19901.excite.util.file.File;

public class Challenge implements Rewardable, DataSource {

	protected static final String CONTENT_TYPE = "application/octet-stream";
	
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

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public String getName() {
		return "challenge.dat";
	}
	
}
