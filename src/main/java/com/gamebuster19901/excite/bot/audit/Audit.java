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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.audit.ban.Ban;
import com.gamebuster19901.excite.bot.audit.ban.DiscordBan;
import com.gamebuster19901.excite.bot.audit.ban.Pardon;
import com.gamebuster19901.excite.bot.audit.ban.ProfileBan;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.PermissionPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.InstantPreference;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.DataMethod;
import com.gamebuster19901.excite.util.DataPoint;
import com.gamebuster19901.excite.util.Permission;
import com.gamebuster19901.excite.util.ThreadService;
import com.gamebuster19901.excite.util.TypedConcurrentHashMap;

import static com.gamebuster19901.excite.util.Permission.*;

public abstract class Audit implements Comparable<Audit>, OutputCSV{

	static transient final ReentrantLock MAP_LOCK = new ReentrantLock();
	
	private static final int DB_VERSION = 2;
	
	private static transient final File AUDIT_DB = new File("./run/verdicts.csv");
	private static transient final ConcurrentHashMap<Class<? extends Audit>, TypedConcurrentHashMap<Long, ? extends Audit>>  AUDIT_MAPS = new ConcurrentHashMap();
	private static transient final TypedConcurrentHashMap<Long, Audit> AUDITS = new TypedConcurrentHashMap<Long, Audit>(Audit.class);
	private static transient final TypedConcurrentHashMap<Long, Ban> BANS = new TypedConcurrentHashMap<Long, Ban>(Ban.class);
	private static transient final TypedConcurrentHashMap<Long, ProfileBan> PROFILE_BANS = new TypedConcurrentHashMap<Long, ProfileBan>(ProfileBan.class);
	private static transient final TypedConcurrentHashMap<Long, DiscordBan> DISCORD_BANS = new TypedConcurrentHashMap<Long, DiscordBan>(DiscordBan.class);
	private static transient final TypedConcurrentHashMap<Long, Pardon> PARDONS = new TypedConcurrentHashMap<Long, Pardon>(Pardon.class);
	private static transient final TypedConcurrentHashMap<Long, CommandAudit> COMMANDS = new TypedConcurrentHashMap<Long, CommandAudit>(CommandAudit.class);
	private static transient final TypedConcurrentHashMap<Long, ProfileDiscoveryAudit> PROFILE_DISCOVERIES = new TypedConcurrentHashMap<Long, ProfileDiscoveryAudit>(ProfileDiscoveryAudit.class);
	private static transient final TypedConcurrentHashMap<Long, NameChangeAudit> NAME_CHANGES = new TypedConcurrentHashMap<Long, NameChangeAudit>(NameChangeAudit.class);
	private static transient final TypedConcurrentHashMap<Long, RankChangeAudit> RANK_CHANGES = new TypedConcurrentHashMap<Long, RankChangeAudit>(RankChangeAudit.class);
	static {
		AUDIT_MAPS.put(Audit.class, AUDITS);
		AUDIT_MAPS.put(Ban.class, BANS);
		AUDIT_MAPS.put(ProfileBan.class, PROFILE_BANS);
		AUDIT_MAPS.put(DiscordBan.class, DISCORD_BANS);
		AUDIT_MAPS.put(Pardon.class, PARDONS);
		AUDIT_MAPS.put(CommandAudit.class, COMMANDS);
		AUDIT_MAPS.put(ProfileDiscoveryAudit.class, PROFILE_DISCOVERIES);
		AUDIT_MAPS.put(NameChangeAudit.class, NAME_CHANGES);
		AUDIT_MAPS.put(RankChangeAudit.class, RANK_CHANGES);
	}
	private static transient final Method PARSE_AUDIT;
	
	static {
		try {
			if(!AUDIT_DB.exists()) {
				AUDIT_DB.getParentFile().mkdirs();
				AUDIT_DB.createNewFile();
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
	protected PermissionPreference secrecy = new PermissionPreference(ANYONE);
	
	@SuppressWarnings("rawtypes")
	private transient HashMap<String, DataPoint> dataPoints = new HashMap<String, DataPoint>();
	
	public static void init() {}
	
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
		this.issuerDiscordId = new LongPreference(context.getSenderId());
		this.issuerUsername = new StringPreference(context.getTag());
		this.description = new StringPreference(description);
		this.dateIssued = new InstantPreference(dateIssued);
	}
	
	/**
	 * Should only be used when parsing audits from a file
	 */
	protected Audit() {
		setDataPoints();
	}
	
	private void setDataPoints() {
		Class<?> clazz = getClass();
		while(Audit.class.isAssignableFrom(clazz)) {
			fieldLoop:
			for(Field f : clazz.getDeclaredFields()) {
				if(Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers())) {
					continue fieldLoop;
				}
				f.setAccessible(true);
				if(dataPoints.put(f.getName(), new DataPoint<Field>(this, f)) != null) {
					throw new Error("Unresolved compilation problem:\n\nMultiple data points with same name: " + f.getName());
				}
			}
			methodLoop:
			for(Method m : clazz.getDeclaredMethods()) {
				if(Modifier.isStatic(m.getModifiers()) || !m.isAnnotationPresent(DataMethod.class)) {
					continue methodLoop;
				}
				if(m.getParameterCount() != 0) {
					throw new Error("Unresolved compilation problem:\n\n@DataPoint can only be placed on methods with no paramaters! \n\nClass: " + m.getDeclaringClass() + "\n\nMethod: " + m);
				}
				if(m.getReturnType() == void.class) {
					throw new Error("Unresolved compilation problem:\n\n@DataPoint cannot be placed on a void method.");
				}
				m.setAccessible(true);
				if(dataPoints.put(m.getName(), new DataPoint<Method>(this, m)) != null) {
					throw new Error("Unresolved compilation problem:\n\nMultiple data points with same name: " + m.getName());
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Audit> T addAudit(T audit) {
		Thread addThread = new Thread() {
			@Override
			public void run() {
				List<Class<? extends Audit>> types = new ArrayList<>();
				try {
					MAP_LOCK.lock();
					if(audit.auditId == null) {
						long auditId = generateUniqueId();
						audit.auditId = new LongPreference(auditId);
					}
					long auditId = audit.auditId.getValue();
					for(TypedConcurrentHashMap<Long, ? extends Audit> auditMap : AUDIT_MAPS.values()) {
						if(auditMap.getType().isAssignableFrom(audit.getClass())) {
							TypedConcurrentHashMap<Long, Audit> realAuditMap = (TypedConcurrentHashMap<Long, Audit>) auditMap;
							realAuditMap.put(auditId, audit);
							types.add((Class<? extends Audit>) realAuditMap.getType());
						}
					}
					if(types.size() == 0) {
						throw new AssertionError("Audit map missing?!");
					}
					if(!types.contains(audit.getClass())) {
						throw new IllegalStateException("Missing dedicated audit map for type " + audit.getClass());
					}
				}
				finally {
					MAP_LOCK.unlock();
				}
			}
		};
		ThreadService.run(addThread);
		return audit;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Audit> HashMap<Long, T> getAuditsOfType(Class<T> type) {
		MAP_LOCK.lock();
		for(TypedConcurrentHashMap<Long, ? extends Audit> auditMap : AUDIT_MAPS.values()) {
			if(auditMap.getType().equals(type)) {
				HashMap<Long, T> ret = new HashMap<Long, T>();
				ret.putAll((Map<? extends Long, ? extends T>) auditMap);
				MAP_LOCK.unlock();
				return ret;
			}
		}
		MAP_LOCK.unlock();
		throw new IllegalArgumentException("There is no audit of type " + type);
	}
	
	public long getAuditId() {
		return auditId.getValue();
	}
	
	public long getIssuerId() {
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
	
	public Permission getSecrecy() {
		return secrecy.getValue();
	}
	
	@SuppressWarnings("rawtypes")
	public final boolean isSecret(MessageContext context) {
		return getSecrecy().hasPermission(context);
	}
	
	public static Audit getAuditById(long id) {
		try {
			MAP_LOCK.lock();
			Audit audit = AUDITS.get(id);
			if(audit == null) {
				audit = new UnknownAudit(id);
			}
			return audit;
		}
		finally {
			MAP_LOCK.unlock();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static MessageContext getContext(Player player) {
		return new MessageContext(player);
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
		//0 is audit version
		int auditVersion = Integer.parseInt(record.get(0));
		auditType.setValue(record.get(1));
		auditId = new LongPreference(Long.parseLong(record.get(2).substring(1)));
		issuerDiscordId = new LongPreference(Long.parseLong(record.get(3).substring(1)));
		issuerUsername = new StringPreference(record.get(4));
		description = new StringPreference(record.get(5));
		dateIssued = new InstantPreference(Instant.parse(record.get(6)));
		if(auditVersion < 2) {
			if(this.getClass() == NameChangeAudit.class) {
				secrecy = new PermissionPreference(ANYONE);
			}
			else {
				secrecy = new PermissionPreference(ADMIN_ONLY);
			}
		}
		else {
			secrecy = new PermissionPreference(record.get(7));
		}
		
		return this;
	}
	
	protected int getRecordSize() {
		return 8;
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
		ArrayList<Object> params = new ArrayList<Object>(Arrays.asList(new Object[] {DB_VERSION, this.auditType, "`" + auditId, "`" + issuerDiscordId, issuerUsername, description, dateIssued, secrecy}));
		return params;
	}
	
	public Object getParamater(String name) {
		Object o = dataPoints.get(name);
		if(o == null) {
			throw new IllegalArgumentException(getClass().getName() + " has no parameter called \"" + name + "\"");
		}
		return o;
	}
	
	public DiscordUser getIssuerDiscord() {
		if(auditId.getValue() == -1) {
			return Main.CONSOLE;
		}
		return DiscordUser.getDiscordUser(issuerDiscordId.getValue());
	}
	
	@Override
	public String toString() {
		String ret = getClass().getSimpleName() + " info: \n";
		for(DataPoint<?> data : dataPoints.values()) {
			ret = ret + data.getName() + ": " + data.getValue() + "\n";
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
		try {
			MAP_LOCK.lock();
			Enumeration <Long> keys = AUDITS.keys();
			Long last = 0l;
			while(keys.hasMoreElements()) {
				Long next = keys.nextElement();
				if(next > last) {
					last = next + 1;
				}
			}
			System.out.println(last);
			return last;
		}
		finally {
			MAP_LOCK.unlock();
		}
	}

	public static void updateAuditsFile() {
		BufferedWriter writer = null;
		try {
			MAP_LOCK.lock();
			AUDIT_DB.createNewFile();
			writer = new BufferedWriter(new FileWriter(AUDIT_DB));
			for(Entry<Long, Audit> audit : AUDITS.entrySet()) {
				writer.write(audit.getValue().toCSV());
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
			finally {
				MAP_LOCK.unlock();
			}
		}
	}
	
}
