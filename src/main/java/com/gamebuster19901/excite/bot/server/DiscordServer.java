package com.gamebuster19901.excite.bot.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.file.FileUtils;

import static com.gamebuster19901.excite.bot.command.Commands.DEFAULT_PREFIX;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class DiscordServer implements OutputCSV {
	private static final int DB_VERSION = 2;
	private static final File SERVER_PREFS = new File("./run/serverPreferences.csv");
	private static final File OLD_SERVER_PREFS = new File("./run/serverPreferences.csv.old");
	private static ConcurrentHashMap<Long, DiscordServer> servers = new ConcurrentHashMap<Long, DiscordServer>();
	
	static {
		try {
			if(!SERVER_PREFS.exists()) {
				SERVER_PREFS.getParentFile().mkdirs();
				SERVER_PREFS.createNewFile();
			}
			else {
				if(OLD_SERVER_PREFS.exists()) {
					if(!FileUtils.contentEquals(SERVER_PREFS, OLD_SERVER_PREFS)) {
						throw new IOException("File content differs!");
					}
				}
			}
			for(DiscordServer server : getEncounteredServersFromFile()) {
				addServer(server);
			}
		}
		catch(IOException e) {
			throw new IOError(e);
		}
	}
	
	protected final long id;
	ServerPreferences preferences;
	
	public DiscordServer(Guild guild) {
		if(guild == null) {
			throw new NullPointerException();
		}
		this.id = guild.getIdLong();
		this.preferences = new ServerPreferences(this);
	}
	
	public DiscordServer(long guildId) {
		this.id = guildId;
		this.preferences = new ServerPreferences(this);
	}

	@Override
	public String toCSV() {
		return preferences.toCSV();
	}

	public Guild getGuild() {
		return Main.discordBot.jda.getGuildById(getId());
	}
	
	public Role getRoleById(long id) {
		return getGuild().getRoleById(id);
	}
	
	public long getId() {
		return id;
	}
	
	public Role[] getRoles() {
		return getGuild().getRoles().toArray(new Role[]{});
	}
	
	public String getName() {
		return preferences.getName();
	}
	
	public String getPrefix() {
		return preferences.getPrefix();
	}
	
	public boolean setPrefix(String prefix) {
		return preferences.setPrefix(prefix);
	}
	
	@Override
	public int hashCode() {
		return Long.valueOf(getId()).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DiscordServer) {
			return ((DiscordServer)o).id == id;
		}
		return false;
	}
	
	public String toString() {
		return getName() + " (" + getId() + ")";
	}
	
	public static void addServer(DiscordServer server) {
		for(Entry<Long, DiscordServer> serverEntry : servers.entrySet()) {
			DiscordServer s = serverEntry.getValue();
			if(server.equals(s)) {
				if(s instanceof UnloadedDiscordServer && !(server instanceof UnloadedDiscordServer)) {
					ServerPreferences preferences = s.preferences;
					server.preferences = preferences;
					servers.put(s.getId(), server);
					System.out.println("Loaded previously unloaded server " + server.getGuild().getName());
				}
				return;
			}
		}
		servers.put(server.getId(), server);
	}
	
	public static DiscordServer getServer(long serverId) {
		DiscordServer discordServer = servers.get(serverId);
		if(discordServer == null || discordServer instanceof UnloadedDiscordServer) {
			return null;
		}
		return discordServer;
	}
	
	public static HashSet<DiscordServer> getLoadedDiscordServers() {
		HashSet<DiscordServer> servers = new HashSet<DiscordServer>();
		for(Entry<Long, DiscordServer> serverEntry : DiscordServer.servers.entrySet()) {
			DiscordServer server = serverEntry.getValue();
			if(!(server instanceof UnloadedDiscordServer)) {
				servers.add(server);
			}
		}
		return servers;
	}
	
	public static HashSet<DiscordServer> getKnownDiscordServers() {
		HashSet<DiscordServer> servers = new HashSet<DiscordServer>();
		for(Entry<Long, DiscordServer> serverEntry : DiscordServer.servers.entrySet()) {
			servers.add(serverEntry.getValue());
		}
		return servers;
	}
	
	public static void updateServerList() {
		for(Guild guild : Main.discordBot.jda.getGuilds()) {
			addServer(new DiscordServer(guild.getIdLong()));
		}
		for(DiscordServer discordServer : DiscordServer.getKnownDiscordServers()) {
			if(discordServer.getClass() == DiscordServer.class && discordServer.getGuild() == null) {
				UnloadedDiscordServer unloadedServer = new UnloadedDiscordServer(discordServer.id);
				unloadedServer.preferences = discordServer.preferences;
				servers.put(discordServer.id, unloadedServer);
				System.out.println("Unloaded previously loaded server with id (" + discordServer.id + ")");
			}
		}
	}
	
	public static void updateServerPreferencesFile() {
		BufferedWriter writer = null;
		try {
			if(OLD_SERVER_PREFS.exists()) {
				OLD_SERVER_PREFS.delete();
			}
			if(!SERVER_PREFS.renameTo(OLD_SERVER_PREFS)) {
				throw new IOException();
			}
			SERVER_PREFS.createNewFile();
			writer = new BufferedWriter(new FileWriter(SERVER_PREFS));
			for(Entry<Long, DiscordServer> discordServer : servers.entrySet()) {
				writer.write(discordServer.getValue().toCSV());
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
	
	private static DiscordServer[] getEncounteredServersFromFile() {
		HashSet<DiscordServer> discordServers = new HashSet<DiscordServer>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(SERVER_PREFS));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			try {
				for(CSVRecord csvRecord : csvParser) {
					
					ServerPreferences preferences = new ServerPreferences();
					
					int version = -1;
					if(csvRecord.size() == 2) {
						version = 1;
					}
					else {
						version = Integer.parseInt(csvRecord.get(0));
					}
					
					String name;
					long guildId;
					String prefix;
					
					if(version == -1) { 
						version = Integer.parseInt(csvRecord.get(0));
					}
					if(version == 1) {
						name = csvRecord.get(0);
						guildId = Long.parseLong(csvRecord.get(1));
						prefix = DEFAULT_PREFIX;
					}	
					else if(version == 2) {
						name = csvRecord.get(1);
						guildId = Long.parseLong(csvRecord.get(2));
						prefix = csvRecord.get(3);
					}
					else {
						throw new IllegalArgumentException("Future database version " + version);
					}
					
					DiscordServer discordServer;
					Guild guild = Main.discordBot.jda.getGuildById(guildId);
					if(guild != null) {
						discordServer = new DiscordServer(guild);
					}
					else {
						discordServer = new UnloadedDiscordServer(guildId);
						System.out.println("Could not find Guild for server " + name + " (" + guildId + ")");
					}
					preferences.parsePreferences(name, guildId, prefix);
					discordServer.preferences = preferences;
					discordServers.add(discordServer);
				}
			}
			catch(Throwable t) {
				t.printStackTrace();
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
		return discordServers.toArray(new DiscordServer[]{});
	}

}
