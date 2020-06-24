package com.gamebuster19901.excite.bot.ban;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.InstantPreference;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.FileUtils;

public abstract class Verdict implements Comparable<Verdict>, OutputCSV{

	private static final int DB_VERSION = 0;
	
	private static final File VERDICT_DB = new File("./run/verdicts.csv");
	private static final File OLD_VERDICT_DB = new File("./run/verdicts.csv.old");
	protected static final HashMap<Long, Verdict> VERDICTS = new HashMap<Long, Verdict>();
	public static final HashMap<Long, Ban> BANS = new HashMap<Long, Ban>();
	protected static final HashMap<Long, ProfileBan> PROFILE_BANS = new HashMap<Long, ProfileBan>();
	protected static final HashMap<Long, DiscordBan> DISCORD_BANS = new HashMap<Long, DiscordBan>();
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
	protected StringPreference verdictReason;
	protected InstantPreference dateIssued;
	
	@SuppressWarnings("rawtypes")
	protected Verdict(MessageContext context) {
		this(context, "");
	}
	
	@SuppressWarnings("rawtypes")
	protected Verdict(MessageContext context, String reason) {
		this(context, reason, Instant.now());
	}
	
	@SuppressWarnings("rawtypes")
	protected Verdict(MessageContext context, String reason, Instant dateIssued) {
		this();
		this.verdictId = new LongPreference(generateUniqueId());
		this.issuerDiscordId = new LongPreference(context.getSenderId());
		this.issuerUsername = new StringPreference(context.getTag());
		this.verdictReason = new StringPreference(reason);
		this.dateIssued = new InstantPreference(dateIssued);
	}
	
	/**
	 * Should only be used when parsing verdicts from a file
	 */
	protected Verdict() {
		this.verdictType = new StringPreference(getClass().getSimpleName());
	}
	
	public static void addVerdict(Verdict verdict) {
		long verdictId = verdict.verdictId.getValue();
		VERDICTS.put(verdictId, verdict);
		if(verdict instanceof Ban) {
			BANS.put(verdictId, (Ban) verdict);
			if(verdict instanceof DiscordBan) {
				DISCORD_BANS.put(verdictId, (DiscordBan) verdict);
			}
			else if (verdict instanceof ProfileBan) {
				PROFILE_BANS.put(verdictId, (ProfileBan) verdict);
			}
			else {
				throw new AssertionError(verdict.getClass());
			}
		}
		else if (verdict instanceof Pardon) {
			PARDONS.put(verdictId, (Pardon) verdict);
		}
	}
	
	public long getVerdictId() {
		return verdictId.getValue();
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
		//0 is verdict version
		//1 is verdict type
		verdictId.setValue(Long.parseLong(record.get(2)));
		issuerDiscordId.setValue(Long.parseLong(record.get(3)));
		issuerUsername.setValue(record.get(4));
		verdictReason.setValue(record.get(5));
		dateIssued.setValue(Instant.parse(record.get(6)));
		
		return this;
	}
	
	@Override
	public final String toCSV() {
		try (
			StringWriter writer = new StringWriter();
			CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withTrim(false));
		)
		{
			printer.printRecord(getParameters());
			printer.flush();
			return writer.toString();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	protected List<Object> getParameters() {
		ArrayList<Object> params = new ArrayList<Object>(Arrays.asList(new Object[] {DB_VERSION, verdictId, issuerDiscordId, issuerUsername, verdictReason, dateIssued}));
		return params;
	}
	
	public DiscordUser getIssuerDiscord() {
		if(verdictId.getValue() == -1) {
			return ConsoleUser.INSTANCE;
		}
		return DiscordUser.getDiscordUser(issuerDiscordId.getValue());
	}
	
	@SuppressWarnings("unchecked")
	private static Verdict[] getVerdictsFromFile() {
		HashSet<Verdict> verdicts = new HashSet<Verdict>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(VERDICT_DB));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			
			try {
				for(CSVRecord csvRecord : csvParser) {
					Class<? extends Verdict> verdictType = (Class<? extends Verdict>) Class.forName(csvRecord.get(1));
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

	public static void updateVerdictsFile() {
		BufferedWriter writer = null;
		try {
			if(OLD_VERDICT_DB.exists()) {
				OLD_VERDICT_DB.delete();
			}
			if(!VERDICT_DB.renameTo(OLD_VERDICT_DB)) {
				throw new IOException();
			}
			VERDICT_DB.createNewFile();
			writer = new BufferedWriter(new FileWriter(VERDICT_DB));
			for(Entry<Long, Verdict> verdict : VERDICTS.entrySet()) {
				writer.write(verdict.getValue().toCSV());
			}
		}
		catch(IOException e) {
			throw new AssertionError(e);
		}
		finally {
			try {
				if(writer != null) {
					writer.close();
				}
			}
			catch(IOException e) {
				throw new IOError(e);
			}
		}
	}
	
}
