package com.gamebuster19901.excite.game.challenge;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import javax.activation.DataSource;

import com.gamebuster19901.excite.util.LZX;
import com.gamebuster19901.excite.util.LZX.LZXResult;

public class Challenge implements Rewardable, DataSource {

	protected static final String CONTENT_TYPE = "application/octet-stream";
	
	public static final int COMP_SIZE = 16;
	public static final int COMP = 0x436F6D70;
	public static final int CHAL_SIZE = 0x7314;
	public static final int GHOS = 0X47686f73;
	public static final int BOT = 0xDC8;
	public static final int REWARD = 0xDD0;
	public static final int BEGIN_TICK_DATA = 0xDD4;
	
	private final int comp;
	private final byte[] headerUnknown = new byte[0x8]; //8
	private final byte[] description = new byte[0x44]; //68
	private final int ghos; //4
	private final int dataVersion; //4
	private final int dataLength; //4
	private final byte[] challengerName = new byte[0x18]; //24
	
	/*
	private final Course course;
	private final short reward;
	private final Bot bot;
	*/
	private final byte[] challengeData;
	
	private Challenge(InputStream data) throws IOException {
		this(readData(data));
	}
	
	private Challenge(byte[] data) throws IOException {
		this.challengeData = data;
		ByteBuffer reader = ByteBuffer.wrap(data);
		try {
			if((comp = reader.getInt()) == COMP) { // Comp - Competition
				reader.get(headerUnknown);
				dataLength = reader.getInt();
				if(dataLength == CHAL_SIZE) {
					if((ghos = reader.getInt()) == GHOS) {
						if((dataVersion = reader.getInt()) == 0x10) {
							reader.get(description);
							reader.get(challengerName);
							System.out.println(new String(description).replace('\0', '\u001A'));
							System.out.println(Character.codePointAt(new String(description), 25));
							System.out.println(getDescription());
						}
						else {
							throw new IOException("Expected 0x10: got 0x" + Integer.toHexString(dataVersion));
						}
					}
					else {
						throw new IOException("Unknown challenge type: 0x" + Integer.toHexString(ghos));
					}

				}
				else {
					throw new IOException("Invalid challenge data length: 0x" + Integer.toHexString(dataLength));
				}
			}
			else {
				throw new IOException("Competition header invalid: 0x" + Integer.toHexString(comp) + ", expected 0x" + COMP);
			}
		}
		catch(Throwable t) {
			IOException e = new IOException("Exception at position " + reader.position() + ": " + t.getMessage());
			e.initCause(t);
			throw e;
		}
	}

	@Override
	public int getReward() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ByteArrayInputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(challengeData);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException(new UnsupportedOperationException());
	}

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public String getName() {
		return "challenge.dat";
	}
	
	public String getDescription() {
		return new String(description).replace("\0", "").replace("\uFFFD", "");
	}
	
	public String getChallengerName() {
		StringBuilder b = new StringBuilder();
		for (int i = 8; i < challengerName.length; i++) {
			if(challengerName[i] == '\0') {
				break;
			}
			b.append((char)challengerName[i]);
		}
		return b.toString();
	}
	
	private static byte[] readData(InputStream data) throws IOException {
		byte[] bytes = new byte[0x7314];
		data.read(bytes);
		if(data.read() != -1) {
			throw new IOException("data stream larger than expected");
		}
		return bytes;
	}
	
	public static Challenge fromMailData(InputStream data) throws IOException {
		byte[] header = new byte[COMP_SIZE];
		data.read(header);
		byte[] challengeData = new byte[CHAL_SIZE];
		LZXResult lzxResult = LZX.decode(data);
		lzxResult.getData().read(challengeData);
		byte[] fullData = new byte[header.length + challengeData.length];
		ByteBuffer buff = ByteBuffer.wrap(fullData);
		
		buff.put(header);
		buff.put(challengeData);
		System.out.println(new String(header));
		return new Challenge(buff.array());
	}
	
}
