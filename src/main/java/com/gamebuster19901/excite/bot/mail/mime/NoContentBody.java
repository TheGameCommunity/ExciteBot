package com.gamebuster19901.excite.bot.mail.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.content.ContentBody;

public class NoContentBody implements ContentBody {

	private final String mimeType;
	private final Charset charset;
	private final String transferEncoding;
	
	public NoContentBody() {
		this("multipart/mixed", null, null);
	}
	
	public NoContentBody(String mimeType, Charset charset, String transferEncoding) {
		this.mimeType = mimeType;
		this.charset = charset;
		this.transferEncoding = transferEncoding;
	}
	
	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public String getMediaType() {
		String mimeType = getMimeType();
		int i = mimeType.indexOf('/');
		if (i != -1) {
			return mimeType.substring(0, i);
		}
		return mimeType;
	}

	@Override
	public String getSubType() {
		String mimeType = getMimeType();
		int i = mimeType.indexOf('/');
		if(i != -1) {
			return mimeType.substring(i + 1);
		}
		return null;
	}

	@Override
	public String getCharset() {
		return charset != null ? charset.name() : null;
	}

	@Override
	public String getTransferEncoding() {
		return transferEncoding;
	}

	@Override
	public long getContentLength() {
		return 0;
	}

	@Override
	public String getFilename() {
		return null;
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		out.flush();
	}

}
