package com.gamebuster19901.mailtest;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Date;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.AbstractContentBody;

import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.TimeUtils;

public class WiiMessageBody extends AbstractContentBody {
	
	protected final Wii from;
	protected final Wii to;
	protected final boolean noReply;
	
	
	public WiiMessageBody(Wii from, Wii to) {
		this(from, to, true);
	}
	
	public WiiMessageBody(Wii from, Wii to, boolean noReply) {
		super(ContentType.TEXT_PLAIN);
		this.from = from;
		this.to = to;
		this.noReply = noReply;
	}
	
	
	@Override
	public String getFilename() {
		return null;
	}
	@Override
	public void writeTo(OutputStream out) throws IOException {
		out.write(TimeUtils.getRC24Date(Date.from(Instant.now())).getBytes());
		out.close();
		
	}
	@Override
	public String getTransferEncoding() {
		return "7bit";
	}
	@Override
	public long getContentLength() {
		return -1;
	}

}
