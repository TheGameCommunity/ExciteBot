package com.gamebuster19901.excite.bot.mail;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class EMessage {
	private static final Pattern REGEX = Pattern.compile("(.*:\\s.*)|(.*=.+)|(--.*)|(\\n\\n)");
	private static final String CLRF = "\r\n";
	
	public List<Part> messageParts = new ArrayList<Part>();
	
	public void addHeader(String header, String value) {
		messageParts.add(new Header(header, value));
	}
	
	public void addHeader(String header, String value, Subheader... subheaders) {
		messageParts.add(new Header(header, value, subheaders));
	}
	
	public void addBoundary(String name) {
		messageParts.add(new Boundary(name));
	}
	
	public void addBoundaryEnd(String name) {
		messageParts.add(new Boundary(name).toEnd());
	}
	
	public void addContent(String content) {
		messageParts.add(new Content(content));
	}
	
	public List<Part> getMessageParts() {
		return messageParts;
	}
	
	public abstract OutputStream getContent();
	
	public void write(OutputStream output) throws IOException {
		Part last = messageParts.get(messageParts.size());
		for(Part part : messageParts) {
			output.write(part.toString().getBytes());
			if(part != last) {
				output.write(CLRF.getBytes());
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for(Part part : messageParts) {
			b.append(part.toString());
			b.append(CLRF);
		}
		return b.toString();
	}
	
	public static EMessage fromMime(MimeMessage message) {
		Matcher matcher = REGEX.matcher(string);
		while(matcher.find()) {
			
		}
	}
	
	public static interface Part {
		
	}
	
	public static class Header implements Part {
		protected String name;
		protected String value;
		private Subheader[] subheaders;
		
		protected Header(String header, String value, Subheader... subHeaders) {
			this.name = header;
			this.value = value;
			this.subheaders = subHeaders;
		}
		
		@Override
		public String toString() {
			String ret = name + ": " + value;
			if(subheaders == null || subheaders.length == 0) {
				return ret;
			}
			ret = ret + ";" + CLRF;
			for(Subheader subheader : subheaders) {
				ret = ret + " " + subheader + CLRF;
			}
			return ret;
		}
		
		public List<Subheader> getSubheaders() {
			return Arrays.asList(subheaders);
		}
		
	}
	
	public static final class Subheader extends Header {

		protected Subheader(String header, String value) {
			super(header, value, null);
		}
		
		@Override
		public String toString() {
			return " " + name + "=" + value;
		}
	}
	
	public static final class Boundary implements Part {
		private final String name;
		
		private Boundary(String name) {
			this.name = "--" + name;
		}
		
		public Boundary toEnd() {
			return new Boundary(name + "--");
		}
		
		public String toString() {
			return name;
		}
	}
	
	public static final class Content implements Part {
		private final String content;
		
		private Content(String content) {
			this.content = content;
		}
		
		public String getContent() {
			return content;
		}
		
		public String toString() {
			return CLRF + content;
		}
	}
	
}
