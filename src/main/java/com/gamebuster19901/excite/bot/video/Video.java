package com.gamebuster19901.excite.bot.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.util.FileUtils;

public class Video {

	private static final int DB_VERSION = 1;
	private static final File VIDEOS = new File("./run/videos.csv");
	private static final File OLD_VIDEOS = new File("./run/videos.old");
	public static final File VIDEO_DL = new File("./run/videosDL.csv");
	private static ArrayList<Video> videos = new ArrayList<Video>();
	
	static {
		try {
			if(!VIDEOS.exists()) {
				VIDEOS.getParentFile().mkdirs();
				VIDEOS.createNewFile();
			}
			else {
				if(OLD_VIDEOS.exists()) {
					if(!FileUtils.contentEquals(VIDEOS, OLD_VIDEOS)) {
						throw new IOException("File content differs!");
					}
				}
			}
			for(Video video : getVideosFromFile()) {
				addVideo(video);
			}
		}
		catch(IOException e) {
			throw new IOError(e);
		}
	}
	
	String videoLink;
	String bot;
	String roomCount;
	String course;
	
	public Video(String youtubeLink) {
		this.videoLink = youtubeLink;
	}
	
	@Override
	public String toString() {
		return videoLink;
	}
	
	@Override
	public int hashCode() {
		return videoLink.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Video) {
			return videoLink == ((Video)o).videoLink;
		}
		return false;
	}
	
	private static void addVideo(Video video) {
		if(!videos.contains(video)) {
			videos.add(video);
		}
		else {
			throw new IllegalArgumentException("Video " + video + " already exists!");
		}
	}

	private static final Random RAND = new Random();

	
	public static final int getVideoCount() {
		return videos.size();
	}
	
	public static final Video getRandomVideo() {
		return videos.get(RAND.nextInt(getVideoCount()));
	}
	
	public static final Video getVideo(int index) {
		return videos.get(index);
	}
	
	private static final Video[] getVideosFromFile() {
		HashSet<Video> videos = new HashSet<Video>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(VIDEOS));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
			int i = 0;
			try {
				
				for(CSVRecord csvRecord : csvParser) {
					String videoLink;
					videoLink = csvRecord.get("Race Link");
					
					Video video = new Video(videoLink);
					addVideo(video);
				}
				
			}
			finally {
				if(reader != null) {
					reader.close();
				}
				if(csvParser != null) {
					csvParser.close();
				}
			}
		}
		catch(IOException e) {
			throw new AssertionError(e);
		}
		return videos.toArray(new Video[]{});
	}
	
}
