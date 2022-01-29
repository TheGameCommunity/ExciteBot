package com.gamebuster19901.excite.crc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

public class Test {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		FileInputStream attachment = new FileInputStream(new File(args[0]));
		final byte[] undecoded = IOUtils.toByteArray(attachment);
		attachment.close();
		final byte[] decoded = new String(Base64.getMimeDecoder().decode(new String(undecoded))).getBytes();
		String crc = Integer.toHexString(new CRCTester(decoded).test());
		
	}
	
}
