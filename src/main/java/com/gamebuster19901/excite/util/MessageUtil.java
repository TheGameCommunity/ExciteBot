package com.gamebuster19901.excite.util;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil {

	public static List<String> toMessages(String text) {
		ArrayList<String> messages = new ArrayList<String>();
		String[] strings = text.split("\\n");
		
		String message = "";
		for(int i = 0; i < strings.length; i++) {
			String s = strings[i] + "\n";
			if(s.length() > 2000) { //if the portion is longer than 2000 characters and has no newline, split into 2000 character chunks
				if(!message.isEmpty()) {
					messages.add(message);
					message = "";
				}
				int k = 0;
				while(k < s.length()) {
					for(int j = 0; j < 2000; j++, k++) {
						message = message + s.charAt(j);
					}
					messages.add(message);
					message = "";
				}
				continue;
			}
			if(message.length() + s.length() <= 2000) {
				message = message + s;
			}
			if(message.length() + s.length() > 2000 || i == strings.length - 1) {
				messages.add(message);
				message = s;
			}
		}
		return messages;
	}
	
}
