package com.gamebuster19901.excite.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LZX {


	private static final File RUN_PATH = new File("./run");
	private static final File LZX_PATH;
	private static File TEMP_PATH; 
	static {
		try {
			LZX_PATH = new File(RUN_PATH.getCanonicalPath() + "/lzx");
			TEMP_PATH = new File(RUN_PATH.getCanonicalPath() + "/tmp");
			if(!TEMP_PATH.exists()) {
				TEMP_PATH.deleteOnExit();
				TEMP_PATH.mkdirs();
			}
			if(!LZX_PATH.exists()) {
				LZX_PATH.createNewFile();
			}
			OutputStream o = new FileOutputStream(LZX_PATH);
			InputStream i = LZX.class.getResourceAsStream("/lzx");
			i.transferTo(o);
			o.close();
			i.close();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	public static LZXResult decode(InputStream source) throws IOException {
		try {
			File dest = File.createTempFile("bin", null, TEMP_PATH);
			//dest.deleteOnExit();
			FileOutputStream o = new FileOutputStream(dest);
			source.transferTo(o);
			o.close();
			source.close();
			ProcessBuilder processBuilder = new ProcessBuilder().command(LZX_PATH.getCanonicalPath(), "-d", dest.getCanonicalPath());
			Process process = processBuilder.start();
			while(process.isAlive()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					throw new IOException(e);
				}
			};
			System.out.println(process.exitValue());
			return new LZXResult(process, dest);
		}
		catch(Throwable t) {
			throw new IOException(t);
		}
	}
	
	public static LZXResult decode(File source) throws IOException {
		FileInputStream fis = null;
		try {
			if(source.exists()) {
				fis = new FileInputStream(source);
				return decode(fis);
			}
			throw new FileNotFoundException(source.toString());
		}
		finally {
			if(fis != null) {
				fis.close();
			}
		}
	}
	
	public static final class LZXResult {
		
		private final Process process;
		private final File dest;
		
		LZXResult(Process process, File dest) {
			this.process = process;
			this.dest = dest;
		}
		
		public Process getProcess() {
			return process;
		}
		
		public File getFile() {
			return dest;
		}
		
		public FileInputStream getData() throws IOException {
			return new FileInputStream(getFile());
		}
	}
	
}
