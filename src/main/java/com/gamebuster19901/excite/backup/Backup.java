package com.gamebuster19901.excite.backup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.gamebuster19901.excite.bot.command.MessageContext;

public class Backup {

	public static final String DIR_TO_BACKUP = "./run/";
	public static final String BACKUP_STORAGE = "./backups/";
	static {
		if(new File(DIR_TO_BACKUP).exists()) {
			File BACKUP_DIR = new File(BACKUP_STORAGE);
			if(!BACKUP_DIR.exists()) {
				BACKUP_DIR.mkdirs();
			}
		}
		else {
			throw new AssertionError(new FileNotFoundException());
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static int backup(MessageContext context) {
		if(context.isOperator()) {
			context.sendMessage("Backing up data...");
			backup(DIR_TO_BACKUP);
			context.sendMessage("Backup complete!");
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 0;
	}
	
	private static void backup(String dirPath) {
		final Path sourceDir = Paths.get(dirPath);
		String zipFileName = BACKUP_STORAGE + Instant.now().toString() + ".zip";
		try {
			final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
			Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
					try {
						Path targetFile = sourceDir.relativize(file);
						outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
						byte[] bytes = Files.readAllBytes(file);
						outputStream.write(bytes, 0, bytes.length);
						outputStream.closeEntry();
					} catch (IOException e) {
						throw new IOError(e);
					}
					return FileVisitResult.CONTINUE;
				}
			});
			outputStream.close();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
}
