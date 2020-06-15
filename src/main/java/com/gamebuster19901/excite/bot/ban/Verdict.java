package com.gamebuster19901.excite.bot.ban;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.bot.user.InstantPreference;
import com.gamebuster19901.excite.util.FileUtils;

public abstract class Verdict implements Comparable<Verdict> {

	private static final File VERDICT_DB = new File("./run/verdicts.csv");
	private static final File OLD_VERDICT_DB = new File("./run/verdicts.csv.old");
	protected static final HashMap<Long, Verdict> VERDICTS = new HashMap<Long, Verdict>();
	protected static final HashMap<Long, Ban> BANS = new HashMap<Long, Ban>();
	protected static final HashMap<Long, Pardon> PARDONS = new HashMap<Long, Pardon>();
	private static final Method PARSE_VERDICT;
	
	static {
		try {
			if(!VERDICT_DB.exists()) {
				VERDICT_DB.getParentFile().mkdirs();
				VERDICT_DB.createNewFile();
			}
			else {
				if(OLD_VERDICT_DB.exists()) {
					if(!FileUtils.contentEquals(VERDICT_DB, OLD_VERDICT_DB)) {
						throw new IOException("File content differs!");
					}
				}
			}
			PARSE_VERDICT = Verdict.class.getDeclaredMethod("parseVerdict", CSVRecord.class);
			PARSE_VERDICT.setAccessible(true);
			for(Verdict verdict : getVerdictsFromFile()) {
				addVerdict(verdict);
			}
		}
		catch(IOException e) {
			throw new IOError(e);
		}
		catch (NoSuchMethodException | SecurityException e) {
			throw new AssertionError(e);
		}
	}
	
	protected StringPreference verdictType;
	protected LongPreference verdictId;
	protected LongPreference issuerDiscordId;
	protected StringPreference issuerUsername;
	protected StringPreference verdictReason = new StringPreference("");
	protected InstantPreference dateIssued = new InstantPreference(Instant.now());
	
	@SuppressWarnings("rawtypes")
	protected Verdict(MessageContext banContext) {
		this();
		this.verdictId = new LongPreference(generateUniqueId());
		this.issuerDiscordId = new LongPreference(banContext.getSenderId());
		this.issuerUsername = new StringPreference(banContext.getTag());
	}
	
	
	protected Verdict() {
		this.verdictType = new StringPreference(getClass().getSimpleName());
	}
	
	public static void addVerdict(Verdict verdict) {
		long verdictId = verdict.verdictId.getValue();
		VERDICTS.put(verdictId, verdict);
		if(verdict instanceof Ban) {
			BANS.put(verdictId, (Ban) verdict);
		}
		else if (verdict instanceof Pardon) {
			PARDONS.put(verdictId, (Pardon) verdict);
		}
	}
	
	public long getBannerDiscordId() {
		return issuerDiscordId.getValue();
	}
	
	public Verdict getVerdictById(long id) {
		Verdict verdict = VERDICTS.get(id);
		if(verdict == null) {
			verdict = new UnknownVerdict(id);
		}
		return verdict;
	}
	
	@Override
	public int hashCode() {
		return verdictId.getValue().intValue();
	}
	
	@Override
	public final int compareTo(Verdict other) {
		return dateIssued.getValue().compareTo(other.dateIssued.getValue());
	}
	
	public final Class<? extends Verdict> getVerdictType() {
		return getClass();
	}
	
	protected Verdict parseVerdict(CSVRecord record) {
		
		//0 is verdict type
		verdictId.setValue(Long.parseLong(record.get(1)));
		issuerDiscordId.setValue(Long.parseLong(record.get(2)));
		issuerUsername.setValue(record.get(3));
		verdictReason.setValue(record.get(4));
		dateIssued.setValue(Instant.parse(record.get(5)));
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	private static Verdict[] getVerdictsFromFile() {
		HashSet<Verdict> verdicts = new HashSet<Verdict>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(VERDICT_DB));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			
			try {
				for(CSVRecord csvRecord : csvParser) {
					Class<? extends Verdict> verdictType = (Class<? extends Verdict>) Class.forName(csvRecord.get(0));
					Verdict verdict = verdictType.newInstance();
					PARSE_VERDICT.invoke(verdict, csvRecord);
					verdicts.add(verdict);
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
		catch(IOException | IllegalAccessException | InstantiationException | InvocationTargetException | ClassNotFoundException e) {
			throw new AssertionError(e);
		}
		return verdicts.toArray(new Verdict[]{});
	}
	
	private static long generateUniqueId() {
		return VERDICTS.size();
	}
	
}
