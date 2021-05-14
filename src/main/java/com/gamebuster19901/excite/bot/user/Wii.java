package com.gamebuster19901.excite.bot.user;

import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class Wii {

	private static final Pattern PATTERN = Pattern.compile("^\\d{16}$");
	
	@Nullable
	public static WiiCode getWiiCode(String code) {
		WiiCode wiiCode = new WiiCode(code);
		if(wiiCode.toString() != null) {
			return wiiCode;
		}
		return null;
	}
	
	@Nullable
	public static WiiCode getWiiCodeFromEmail(String code) {
		if(code == null) {
			return null;
		}
		if(code.contains("@rc24.xyz")) {
			return new WiiCode(code.replace("@rc24.xyz", ""));
		}
		return new WiiCode(null);
	}
	
	@SuppressWarnings("rawtypes")
	public static class WiiCode implements Comparable {
		
		private String code;
		
		private WiiCode(String code) {
			if(code == null) {
				return;
			}
			if(code.startsWith("w")) {
				code = code.substring(1);
			}
			code = code.replace("-", "");
			
			if(PATTERN.matcher(code).matches()) {
				this.code = code;
			}
		}
		
		@Override
		@Nullable
		public String toString() {
			return code;
		}
		
		public boolean equals(Object o) {
			if(o instanceof WiiCode || o instanceof String) {
				return code.toString().equals(o.toString());
			}
			return false;
		}

		@Override
		public int compareTo(Object o) {
			if(o instanceof WiiCode || o instanceof String) {
				return code.toString().compareTo(o.toString());
			}
			if(o == null) {
				throw new NullPointerException();
			}
			throw new IllegalArgumentException(o.getClass().getCanonicalName());
		}
		
		public String toRiiConnect24() {
			return "w" + code;
		}
		
		public String hyphenate() {
			return code.substring(0, 4) + "-" + code.substring(4, 8) + "-" + code.substring(8, 12) + "-" + code.substring(12, 16);
		}
	}
	
}
