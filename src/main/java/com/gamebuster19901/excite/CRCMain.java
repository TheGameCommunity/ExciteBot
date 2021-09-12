package com.gamebuster19901.excite;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;

import com.gamebuster19901.excite.game.challenge.Challenge;
import com.gamebuster19901.excite.util.file.Directory;

public class CRCMain {

	public static void main(String[] args) throws IOException, MessagingException {
		Directory d = new Directory(args[0]);
		File[] files = d.listFiles();
		
		for(File f : files) {
			try {
				new Challenge(new com.gamebuster19901.excite.util.file.File(f));
			}
			catch(Throwable t) {
				System.err.println(f);
				t.printStackTrace();
				continue;
			}
		}
	}
	
}
