package com.gamebuster19901.excite.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

	public static boolean contentEquals(File file1, File file2) throws IOException {
		if(file1.isFile()) {
			if(file2.isFile()) {
				if(file1.getTotalSpace() == file2.getTotalSpace()) {
					BufferedReader reader1 = new BufferedReader(new FileReader(file1));
					BufferedReader reader2 = new BufferedReader(new FileReader(file2));
					try {
						long lineNumber = 1;
						String line1 = "";
						String line2 = "";
						
						while(reader1.ready() || reader2.ready()) {
							if(reader1.ready()) {
								line1 = reader1.readLine();
							}
							if(reader2.ready()) {
								line2 = reader2.readLine();
							}
							
							if(!line1.equals(line2)) {
								System.out.println("Content differs on line " + lineNumber);
								return false;
							}
							lineNumber++;
						}
						return true;
					}
					finally {
						if(reader1 != null) {
							reader1.close();
						}
						if(reader2 != null) {
							reader2.close();
						}
					}
				}
				else {
					System.out.println("File length differs!");
					return false;
				}
			}
			else {
				throw new IOException(file2.getAbsoluteFile() + " does not exist or is a directory!");
			}
		}
		else {
			throw new IOException(file1.getAbsoluteFile() + " does not exist or is a directory!");
		}
	}
	
}
