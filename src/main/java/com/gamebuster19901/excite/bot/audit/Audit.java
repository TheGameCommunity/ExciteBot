package com.gamebuster19901.excite.bot.audit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

import com.gamebuster19901.excite.bot.audit.ban.Ban;
import com.gamebuster19901.excite.bot.audit.ban.DiscordBan;
import com.gamebuster19901.excite.bot.audit.ban.Pardon;
import com.gamebuster19901.excite.bot.audit.ban.ProfileBan;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.bot.user.ConsoleUser;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.InstantPreference;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.DataPoint;
import com.gamebuster19901.excite.util.FileUtils;
import com.gamebuster19901.excite.util.Permission;

import static com.gamebuster19901.excite.util.Permission.ANYONE;

public abstract class Audit implements Comparable<Audit>, OutputCSV{

	private static final int DB_VERSION = 0;
	
	private static transient final File AUDIT_DB = new File("./run/verdicts.csv");
	private static transient final File OLD_AUDIT_DB = new File("./run/verdicts.csv.old");
	protected static transient final HashMap<Long, Audit> AUDITS = new HashMap<Long, Audit>();
	public static transient final HashMap<Long, Ban> BANS = new HashMap<Long, Ban>();
	protected static transient final HashMap<Long, ProfileBan> PROFILE_BANS = new HashMap<Long, ProfileBan>();
	protected static transient final HashMap<Long, DiscordBan> DISCORD_BANS = new HashMap<Long, DiscordBan>();
	protected static transient final HashMap<Long, Pardon> PARDONS = new HashMap<Long, Pardon>();
	private static transient final Method PARSE_AUDIT;
	
	static {
		try {
			if(!AUDIT_DB.exists()) {
				AUDIT_DB.getParentFile().mkdirs();
				AUDIT_DB.createNewFile();
			}
			else {
				if(OLD_AUDIT_DB.exists()) {
					if(!FileUtils.contentEquals(AUDIT_DB, OLD_AUDIT_DB)) {
						throw new IOException("File content differs!");
					}
				}
			}
			PARSE_AUDIT = Audit.class.getDeclaredMethod("parseAudit", CSVRecord.class);
			PARSE_AUDIT.setAccessible(true);
			for(Audit audit : getAuditsFromFile()) {
				addAudit(audit);
			}
		}
		catch(IOException e) {
			throw new IOError(e);
		}
		catch (NoSuchMethodException | SecurityException e) {
			throw new AssertionError(e);
		}
	}
	
	protected StringPreference auditType = new StringPreference(getClass().getCanonicalName());
	protected LongPreference auditId;
	protected LongPreference issuerDiscordId;
	protected StringPreference issuerUsername;
	protected StringPreference description;
	protected InstantPreference dateIssued;
	protected transient Permission secrecy = ANYONE;
	
	@SuppressWarnings("rawtypes")
	protected Audit(MessageContext context) {
		this(context, "");
	}
	
	@SuppressWarnings("rawtypes")
	protected Audit(MessageContext context, String description) {
		this(context, description, Instant.now());
	}
	
	@SuppressWarnings("rawtypes")
	protected Audit(MessageContext context, String description, Instant dateIssued) {
		this();
		this.auditId = new LongPreference(generateUniqueId());
		this.issuerDiscordId = new LongPreference(context.getSenderId());
		this.issuerUsername = new StringPreference(context.getTag());
		this.description = new StringPreference(description);
		this.dateIssued = new InstantPreference(dateIssued);
	}
	
	/**
	 * Should only be used when parsing audits from a file
	 */
	protected Audit() {

	}
	
	public static <T extends Audit> T addAudit(T audit) {
		synchronized(AUDITS) {
			long auditId = audit.auditId.getValue();
			AUDITS.put(auditId, audit);
			if(audit instanceof Ban) {
				BANS.put(auditId, (Ban) audit);
				if(audit instanceof DiscordBan) {
					DISCORD_BANS.put(auditId, (DiscordBan) audit);
				}
				else if (audit instanceof ProfileBan) {
					PROFILE_BANS.put(auditId, (ProfileBan) audit);
				}
				else {
					throw new AssertionError(audit.getClass());
				}
			}
			else if (audit instanceof Pardon) {
				PARDONS.put(auditId, (Pardon) audit);
			}
			return audit;
		}
	}
	
	public long getAuditId() {
		return auditId.getValue();
	}
	
	public long getIssuerDiscordId() {
		return issuerDiscordId.getValue();
	}
	
	public String getIssuerUsername() {
		return (String) issuerUsername.getValue();
	}
	
	public String getDescription() {
		return (String) description.getValue();
	}
	
	public Instant getDateIssued() {
		return dateIssued.getValue();
	}
	
	public Audit getAuditById(long id) {
		synchronized(AUDITS) {
			Audit audit = AUDITS.get(id);
			if(audit == null) {
				audit = new UnknownAudit(id);
			}
			return audit;
		}
	}
	
	@Override
	public int hashCode() {
		return auditId.getValue().intValue();
	}
	
	@Override
	public final int compareTo(Audit other) {
		return dateIssued.getValue().compareTo(other.dateIssued.getValue());
	}
	
	public final Class<? extends Audit> getAuditType() {
		return getClass();
	}
	
	protected Audit parseAudit(CSVRecord record) {
		//0 is verdict version
		auditType.setValue(record.get(1));
		auditId = new LongPreference(Long.parseLong(record.get(2).substring(1)));
		issuerDiscordId = new LongPreference(Long.parseLong(record.get(3).substring(1)));
		issuerUsername = new StringPreference(record.get(4));
		description = new StringPreference(record.get(5));
		dateIssued = new InstantPreference(Instant.parse(record.get(6)));
		
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
	
	public List<Object> getParameters() {
		ArrayList<Object> params = new ArrayList<Object>(Arrays.asList(new Object[] {DB_VERSION, this.auditType, "`" + auditId, "`" + issuerDiscordId, issuerUsername, description, dateIssued}));
		return params;
	}
	
	public DiscordUser getIssuerDiscord() {
		if(auditId.getValue() == -1) {
			return ConsoleUser.INSTANCE;
		}
		return DiscordUser.getDiscordUser(issuerDiscordId.getValue());
	}
	
	@Override
	public String toString() {
		String ret = getClass().getSimpleName() + " info: \n";
		Class<?> clazz = getClass();
		try {
			while(clazz != Object.class) {
				fieldLoop:
				for(Field f : clazz.getDeclaredFields()) {
					if(Modifier.isTransient(f.getModifiers())) {
						continue fieldLoop;
					}
					f.setAccessible(true);
					Object val = f.get(this);
					ret = ret + f.getName() + ": " + val + " \n";
				}
				methodLoop:
				for(Method m : clazz.getDeclaredMethods()) {
					if(Modifier.isStatic(m.getModifiers()) || !m.isAnnotationPresent(DataPoint.class)) {
						continue methodLoop;
					}
					if(m.getParameterCount() != 0) {
						throw new Error("Unresolved compilation problem:\n\n@DataPoint can only be placed on methods with no paramaters! \n\nClass: " + m.getDeclaringClass() + "\n\nMethod: " + m);
					}
					if(m.getReturnType() == void.class) {
						throw new Error("Unresolved compilation problem:\n\n@DataPoint cannot be placed on a void method.");
					}
					m.setAccessible(true);
					Object val = m.invoke(this);
					ret = ret + m.getName() + ": " + val + " \n";
				}
				clazz = clazz.getSuperclass();
			}
		}
		catch(IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private static Audit[] getAuditsFromFile() {
		HashSet<Audit> audits = new HashSet<Audit>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(AUDIT_DB));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			
			try {
				for(CSVRecord csvRecord : csvParser) {
					Class<? extends Audit> auditType = (Class<? extends Audit>) Class.forName(csvRecord.get(1));
					Audit audit = auditType.newInstance();
					PARSE_AUDIT.invoke(audit, csvRecord);
					audits.add(audit);
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
		return audits.toArray(new Audit[]{});
	}
	
	private static long generateUniqueId() {
		synchronized(AUDITS) {
			return AUDITS.size();
		}
	}

	public static void updateAuditsFile() {
		BufferedWriter writer = null;
		try {
			if(OLD_AUDIT_DB.exists()) {
				OLD_AUDIT_DB.delete();
			}
			if(!AUDIT_DB.renameTo(OLD_AUDIT_DB)) {
				throw new IOException();
			}
			AUDIT_DB.createNewFile();
			writer = new BufferedWriter(new FileWriter(AUDIT_DB));
			synchronized(AUDITS) {
				for(Entry<Long, Audit> audit : AUDITS.entrySet()) {
					writer.write(audit.getValue().toCSV());
				}
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
