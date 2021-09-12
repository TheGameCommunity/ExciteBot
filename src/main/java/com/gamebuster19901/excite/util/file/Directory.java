package com.gamebuster19901.excite.util.file;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("serial")
public class Directory extends java.io.File {

	public Directory(java.io.File file) throws IOException {
		super(ensureNotNull(file));
		
		if(file != null) {
			if(!file.exists()) {
				throw new IOException(file.getCanonicalPath() + " does not exist");
			}
			
			if(!file.isDirectory()) {
				throw new IOException(file.getCanonicalPath() + " is not a directory");
			}
		}
	}
	
	public Directory(String path) throws IOException {
		this(new java.io.File(path));
	}
	
	@Override
	public Directory getParentFile() {
		try {
			if(super.getParentFile() != null) {
				return new Directory(super.getParentFile());
			}
			return null;
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	@Override
	public Directory getCanonicalFile() throws IOException {
		return new Directory(super.getCanonicalFile());
	}
	
	@Override
	public boolean isFile() {
		return false;
	}
	
	@Override
	public boolean isDirectory() {
		return true;
	}
	
	@Override
	public File[] listFiles() {
		final ArrayList<File> files = new ArrayList<File>();
		try {
			if(super.listFiles() == null) {
				return new File[]{};
			}
			for(java.io.File f : super.listFiles()) {
				if(f.isFile()) {
					files.add(new File(f));
				}
				if(f.isDirectory()) {
					files.addAll(Arrays.asList(new Directory(f).listFiles()));
				}
			}
		}
		catch(IOException e) {
			throw new IOError(e);
		}
		return files.toArray(new File[]{});
	}
	
	public Directory[] listDirectories() {
		final ArrayList<Directory> directories = new ArrayList<Directory>();
		try {
			for(java.io.File f : super.listFiles()) {
				if(f.isDirectory()) {
					directories.add(new Directory(f));
				}
			}
		}
		catch(IOException e) {
			throw new IOError(e);
		}
		return directories.toArray(new Directory[]{});
	}
	
	public boolean isSubDirectory(Directory directory) throws IOException {
		
		Directory parent = getParentFile();
		
		if(parent == null) {
			return false;
		}
		
		if(equals(directory)) {
			return true;
		}
		
		return parent.isSubDirectory(directory);
	}
	
	private static final String ensureNotNull(java.io.File file) throws IOException {
		if(file != null) {
			return file.getCanonicalPath();
		}
		return System.getProperty("user.home");
	}
	
}
