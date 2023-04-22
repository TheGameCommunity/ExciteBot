package com.gamebuster19901.excite.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.gamebuster19901.excite.game.challenge.Challenge;

public class LZXTest {

	public static void main(String[] args) throws IOException {
		//LZXResult r = LZX.decode(new File("./run/decodeMe.bin"));
		File oldChallengeFile = new File("./run/challenge.bin");
		System.out.println(oldChallengeFile.getCanonicalPath());
		Challenge challenge = Challenge.fromMailData(new FileInputStream(oldChallengeFile));
		new File("./dev").mkdirs();
		File challengeFile = new File("./dev/challenge1.bin");
		if(!challengeFile.exists()) {
			challengeFile.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(challengeFile);
		challenge.getInputStream().transferTo(fos);
		fos.close();
	}
	
}
