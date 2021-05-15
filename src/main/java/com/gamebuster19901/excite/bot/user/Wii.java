package com.gamebuster19901.excite.bot.user;

import java.io.IOError;
import java.sql.SQLException;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Result;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.util.Named;
import com.gamebuster19901.excite.util.Owned;

import static com.gamebuster19901.excite.bot.database.Comparator.*;
import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.WIIS;
import static com.gamebuster19901.excite.bot.user.DesiredProfile.validPasswordChars;

public class Wii implements Named, Owned<DiscordUser> {

	private static final String EMAIL_SUFFIX = "@rc24.xyz";
	private static final Pattern PATTERN = Pattern.compile("^\\d{16}$");
	private static final Random RANDOM = new Random();
	
	private WiiCode wiiCode;
	
	private Wii(WiiCode code) {
		this.wiiCode = code;
	}
	
	public WiiCode getWiiCode() {
		return wiiCode;
	}
	
	public DiscordUser getOwner() {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, DISCORD_ID, WIIS, new Comparison(WII_ID, EQUALS, wiiCode));
			if(result.hasNext()) {
				return DiscordUser.getDiscordUserIncludingUnknown(ConsoleContext.INSTANCE, result.getLong(DISCORD_ID));
			}
			return Nobody.INSTANCE;
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@Override
	public long getID() {
		try {
			return Integer.parseInt(wiiCode.toString());
		}
		catch(NumberFormatException e) {
			return -1;
		}
	}
	

	@Override
	public String getName() {
		if(wiiCode.isValid) {
			return wiiCode.hyphenate();
		}
		return wiiCode.toString();
	}
	
	@Override
	public String getIdentifierName() {
		return getOwnershipString();
	}
	
	private String generateRegistrationCode() {
		char[] sequence = new char[16];
		for(int i = 0; i < sequence.length; i++) {
			sequence[i] = validPasswordChars.charAt(RANDOM.nextInt(validPasswordChars.length()));
		}
		return new String(sequence);
	}
	
	public static Wii getWii(String code) {
		WiiCode wiiCode = getWiiCode(code);
		if(wiiCode.isValid) {
			return new Wii(wiiCode);
		}
		return new InvalidWii(wiiCode);
	}
	
	@Nullable
	public static WiiCode getWiiCode(String code) {
		return new WiiCode(code);
	}
	
	@SuppressWarnings("rawtypes")
	public static class WiiCode implements Comparable {
		
		protected final String code;
		protected final boolean isValid;
		
		private WiiCode(String code) {
			if(code == null) {
				this.code = null;
				this.isValid = false;
				return;
			}
			if(code.startsWith("w")) {
				code = code.substring(1);
			}
			code = code.replace("-", "");
			code = code.replace(EMAIL_SUFFIX, "");
			
			isValid = PATTERN.matcher(code).matches() ? true : false;
			this.code = code;
		}
		
		public boolean isValid() {
			return isValid;
		}
		
		@Override
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
			throw new IllegalArgumentException(o.getClass().getCanonicalName());
		}
		
		public String toRiiConnect24() {
			return "w" + code;
		}
		
		public String toEmail() {
			return toRiiConnect24() + EMAIL_SUFFIX;
		}
		
		public String hyphenate() {
			return code.substring(0, 4) + "-" + code.substring(4, 8) + "-" + code.substring(8, 12) + "-" + code.substring(12, 16);
		}
	}
	
	public static final class InvalidWii extends Wii {
		private InvalidWii(WiiCode code) {
			super(code);
		}
		
		@Override
		public boolean isKnown() {
			return false;
		}
	}
	
}
