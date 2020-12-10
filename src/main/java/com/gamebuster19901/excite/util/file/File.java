package com.gamebuster19901.excite.util.file;

import java.io.IOError;
import java.io.IOException;

@SuppressWarnings("serial")
public class File extends java.io.File{

	public File(java.io.File file) throws IOException {
		super(file.getCanonicalPath());
		
		if(!file.exists()) {
			throw new IOException(file.getCanonicalPath() + " does not exist");
		}
		
		if(file.isDirectory()) {
			throw new IOException(file.getCanonicalPath() + " is a directory");
		}
	}
	
	public File(String path) throws IOException {
		this(new java.io.File(path));
	}
	
	@Override
	public Directory getParentFile() {
		try {
			return new Directory(super.getParentFile());
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	@Override
	public File getCanonicalFile() throws IOException {
		return new File(super.getCanonicalFile());
	}
	
	@Override
	public boolean isFile() {
		return true;
	}
	
	@Override
	public boolean isDirectory() {
		return false;
	}
	
	public boolean isDownloadable() throws IOException {
		return !isSecret() && length() < 8388608;
	}
	
	@Override
	public File[] listFiles() {
		return new File[]{};
	}
	
	public boolean isInDirectory(Directory directory) throws IOException {
		
		if(directory == null) {
			return false;
		}
		
		if(this.getParentFile().equals(directory)) {
			return true;
		}
		
		return isInDirectory(directory.getParentFile());
	}
	
	public boolean isSecret() throws IOException {
		return FileUtils.isSecret(this);
	}
}
