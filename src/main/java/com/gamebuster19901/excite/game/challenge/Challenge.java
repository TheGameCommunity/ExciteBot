package com.gamebuster19901.excite.game.challenge;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.gamebuster19901.excite.game.Bot;
import com.gamebuster19901.excite.game.Course;
import com.gamebuster19901.excite.game.Cup;
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
	
	public Challenge(File file) throws MessagingException, IOException {
		if(FilenameUtils.getExtension(file.getAbsolutePath()).contains("email")) {
			
			MimeMessage message = new MimeMessage(null, new FileInputStream(file));
			MimeMultipart multiMessage = new MimeMultipart(new ByteArrayDataSource(message.getInputStream(), "multipart/mixed"));
			challengeData = IOUtils.toString(multiMessage.getBodyPart(1).getDataHandler().getInputStream()).getBytes();
			
			writeChallengeData(new java.io.File(file.getParentFile().getAbsolutePath() + "/" + file.getName() + ".email.challenge"));
			
			reward = 0;
			bot = Bot.FROG;
			course = Cup.SCHOOL.getCourses()[0];
		}
		else {
			throw new UnsupportedOperationException("Not implemented yet");
		}
	}
	
	public void writeChallengeData(java.io.File file) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(challengeData);
		fos.close();
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
