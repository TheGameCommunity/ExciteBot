package com.gamebuster19901.excite.util;

public class MessageUtil {

	public static String[] toMessages(String text) {
		return text.split("(?<=\\G[\\s\\S]{2000})");
	}
	
}
