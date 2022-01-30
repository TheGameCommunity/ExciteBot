package com.gamebuster19901.excite.crc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;


public class Test {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		FileInputStream attachmentIS = new FileInputStream(new File(args[0]));
		byte[] attachment = IOUtils.toByteArray(attachmentIS);
		System.out.println(Integer.toHexString(new CRCTester(attachment).test(0)));
		//System.out.println(Integer.toHexString(new CRCTester(attachmentIS).test(0)));
	}
	
}
