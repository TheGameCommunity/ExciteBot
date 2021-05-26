package com.gamebuster19901.excite.bot.mail.mime;

import java.nio.charset.Charset;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;

public class WiiMultipartEntityBuilder {

	protected MultipartEntityBuilder parent = MultipartEntityBuilder.create();
	
	protected WiiMultipartEntityBuilder() {
		
	}
	
	public WiiMultipartEntityBuilder setMode(final HttpMultipartMode mode) {
		parent.setMode(mode);
		return this;
	}

	public WiiMultipartEntityBuilder setLaxMode() {
		parent.setLaxMode();
		return this;
	}
	
	public WiiMultipartEntityBuilder setStrictMode() {
		parent.setStrictMode();
		return this;
	}
	
	public WiiMultipartEntityBuilder setBoundary(final String boundary) {
		parent.setBoundary(boundary);
		return this;
	}
	
	public WiiMultipartEntityBuilder setMimeSubtype(final String subType) {
		parent.setMimeSubtype(subType);
		return this;
	}
	
    public WiiMultipartEntityBuilder setContentType(final ContentType contentType) {
    	parent.setContentType(contentType);
		return this;
    }
    
    public WiiMultipartEntityBuilder setCharset(final Charset charset) {
    	parent.setCharset(charset);
		return this;
    }
    
    public WiiMultipartEntityBuilder addPart(final FormBodyPart bodyPart) {
    	parent.addPart(bodyPart);
		return this;
    }
    
    public MultipartEntityBuilder addPart(final String name, final ContentBody contentBody) {
    	
    }
}
