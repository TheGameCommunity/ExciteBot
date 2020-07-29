package com.gamebuster19901.excite.bot.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.util.FileUtils;
import com.gamebuster19901.excite.util.TimeUtils;
import com.gamebuster19901.excite.game.Bot;
import com.gamebuster19901.excite.game.Course;

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
	
	int raceNumber;
	LocalDate date;
	Course course;
	short stars;
	byte roomSize;
	byte placement;
	Bot bot;
	short videoNumber;
	short firstStars;
	short secondStars;
	short thirdStars;
	short fourthStars;
	short fifthStars;
	short sixthStars;
	short Base;
	int timeLeft;
	short crashes;
	int seconds;
	String firstPlace;
	String secondPlace;
	String thridPlace;
	String fourthPlace;
	String fifthPlace;
	String sixthPlace;
	String imageName;
	URI video;
	String uploader;
	String youtubeDescription;
	String notes;
	String rankHelper;
	int starTimeRank;
	String recordFinder;
	
	public Video(CSVRecord record) {
		this.raceNumber = Integer.parseInt(record.get("#"));
		this.date = TimeUtils.fromString(record.get("Date"));
		this.course = Course.fromString(record.get("Course"));
		this.stars = Short.parseShort(record.get("Stars"));
		this.roomSize = Byte.parseByte(record.get("Rm"));
		this.placement = (byte)(record.get("Place").charAt(0) - '0');
		this.bot = Bot.fromString(record.get("Bot"));
		this.videoNumber = Short.parseShort(record.get("V#"));
		
		
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
