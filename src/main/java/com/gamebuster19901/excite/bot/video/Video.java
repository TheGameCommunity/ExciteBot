package com.gamebuster19901.excite.bot.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.util.CSVHelper;
import com.gamebuster19901.excite.util.FileUtils;
import com.gamebuster19901.excite.game.Bot;
import com.gamebuster19901.excite.game.Course;
import com.gamebuster19901.excite.game.Placement;
import com.gamebuster19901.excite.game.Stars;

public class Video {

	private static final int DB_VERSION = 1;
	private static final File VIDEOS = new File("./run/videos/");
	private static final File OLD_VIDEOS = new File("./run/videos.old");
	public static final File VIDEO_DL = new File("./run/videosDL.csv");
	private static ArrayList<Video> videos = new ArrayList<Video>();
	
	static {
		try {
			if(!VIDEOS.exists()) {
				VIDEOS.mkdirs();
				VIDEOS.createNewFile();
			}
			else {
				if(OLD_VIDEOS.exists()) {
					if(!FileUtils.contentEquals(VIDEOS, OLD_VIDEOS)) {
						throw new IOException("File content differs!");
					}
				}
			}
			for(Video video : getVideosFromDirectory()) {
				addVideo(video);
			}
		}
		catch(IOException e) {
			throw new IOError(e);
		}
	}
	
	int raceNumber = -1;
	String date = null;
	Course course = null;
	short stars;
	byte roomSize;
	Placement placement = null;
	Bot bot = null;
	short videoNumber;
	Stars firstStars = null;
	Stars secondStars = null;
	Stars thirdStars = null;
	Stars fourthStars = null;
	Stars fifthStars = null;
	Stars sixthStars = null;
	Stars base = null;
	String timeLeft;
	String crashes;
	String timestamp = null;
	int seconds;
	String firstPlace = null;
	String secondPlace = "";
	String thirdPlace = "";
	String fourthPlace = "";
	String fifthPlace = "";
	String sixthPlace = "";
	String imageName = "";
	URI video = null;
	String uploader = null;
	String youtubeDescription = null;
	String notes = null;
	String rankHelper = null;
	int starTimeRank;
	String recordFinder = null;
	
	Object[] nonNull = new Object[] {date, course, placement, bot, firstStars, secondStars, thirdStars, fourthStars, fifthStars, sixthStars, base, timestamp, firstPlace, secondPlace, thirdPlace, fourthPlace, fifthPlace, sixthPlace, imageName, video, uploader, youtubeDescription, notes, rankHelper, recordFinder}; 
	
	public Video(CSVHelper record) {
		try {
			this.raceNumber = record.getInt("#");
			this.date = record.getNonNull("Date");
			this.course = Course.fromString(record.getNonNull("Course"));
			this.stars = record.getShort("Stars");
			this.roomSize = record.getByte("Rm");
			this.placement = Placement.fromString(record.getNonNull("Place"));
			this.bot = Bot.fromString(record.getNonNull("Bot"));
			this.videoNumber = record.getShort("V#");
			this.firstStars = new Stars(record.getNonNull("1st"));
			this.secondStars = new Stars(record.getNull("2nd"));
			this.thirdStars = new Stars(record.getNull("3rd"));
			this.fourthStars = new Stars(record.getNull("4th"));
			this.fifthStars = new Stars(record.getNull("5th"));
			this.sixthStars = new Stars(record.getNull("6th"));
			this.base = new Stars(record.getNonNull("Base"));
			this.timeLeft = record.getNonNull("TL");
			this.crashes = record.get("CC");
			this.timestamp = record.getNonNull("Tstamp");
			this.seconds = record.getInt("Secs");
			this.firstPlace = record.getNonNull("P1");
			this.secondPlace = record.getNull("P2");
			this.thirdPlace = record.getNull("P3");
			this.fourthPlace = record.getNull("P4");
			this.fifthPlace = record.getNull("P5");
			this.sixthPlace = record.getNull("P6");
			this.imageName = record.getNull("Image Name");
			this.video = new URI(record.getNonNull("Race Link"));
			this.uploader = record.getNonNull("Uploader");
			this.youtubeDescription = record.getNull("YouTube Description Timestamp C/P");
			this.notes = record.getNull("Notes");
			this.rankHelper = record.getNonNull("Rank Helper");
			this.starTimeRank = record.getInt("Star-Time Rank");
			this.recordFinder = record.getNonNull("Bot Record Finder");
			
			
		}
		catch(Throwable t) {
			throw new RuntimeException("Unable to parse video record " + record.getRecordNumber(), t);
		}
	}
	
	@Override
	public String toString() {
		return video.toString();
	}
	
	@Override
	public int hashCode() {
		return video.toString().hashCode();
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
			try {
				
				for(CSVRecord csvRecord : csvParser) {
					Video video = new Video(new CSVHelper(csvRecord));
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
