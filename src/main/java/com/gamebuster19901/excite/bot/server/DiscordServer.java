package com.gamebuster19901.excite.bot.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.FileUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class DiscordServer implements OutputCSV{

	private static final File SERVER_PREFS = new File("./run/serverPreferences.csv");
	private static final File OLD_SERVER_PREFS = new File("./run/serverPreferences.csv.old");
	private static HashMap<Long, DiscordServer> servers = new HashMap<Long, DiscordServer>();
	
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
	
	RolePreference adminRoles;
	
	public DiscordServer(long guildId) {
		this.id = guildId;
		this.adminRoles = new RolePreference(this);
	}

	@Override
	public String toCSV() {
		final Guild guild = getGuild();
		try (
			StringWriter writer = new StringWriter();
			CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withTrim(false));
		)
		{
			printer.printRecord(guild.getName(), guild.getId(), adminRoles);
			printer.flush();
			return writer.toString();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	public Guild getGuild() {
		return Main.discordBot.jda.getGuildById(id);
	}
	
	public Role getRoleById(long id) {
		return getGuild().getRoleById(id);
	}
	
	public Role[] getAdminRoles() {
		HashSet<Role> roles = new HashSet<Role>();
		for(Long roleID : adminRoles.getValue()) {
			Role role = getRoleById(roleID);
			if(role != null) {
				roles.add(role);
			}
		}
		return roles.toArray(new Role[]{});
	}
	
	public Role[] getRoles() {
		return getGuild().getRoles().toArray(new Role[]{});
	}
	
	public void addAdminRole(Role role) {
		this.adminRoles.addRole(role);
	}
	
	public void removeAdminRole(Role role) {
		this.adminRoles.removeRole(role);
	}
	
	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DiscordServer) {
			return ((DiscordServer)o).id == id;
		}
		return false;
	}
	
	public static void addServer(DiscordServer server) {
		for(Entry<Long, DiscordServer> serverEntry : servers.entrySet()) {
			DiscordServer s = serverEntry.getValue();
			if(server.equals(s)) {
				if(s instanceof UnloadedDiscordServer && !(server instanceof UnloadedDiscordServer)) {
					RolePreference adminRoles = s.adminRoles;
					server.adminRoles = adminRoles;
					servers.put(s.id, server);
					System.out.println("Loaded previously unloaded server " + server.getGuild().getName());
				}
				return;
			}
		}
		servers.put(server.id, server);
	}
	
	public static DiscordServer getServer(long serverId) {
		DiscordServer discordServer = servers.get(serverId);
		if(discordServer == null || discordServer instanceof UnloadedDiscordServer) {
			return null;
		}
		return discordServer;
	}
	
	public static void updateServerList() {
		for(Guild guild : Main.discordBot.jda.getGuilds()) {
			addServer(new DiscordServer(guild.getIdLong()));
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
					String name = csvRecord.get(0);
					long guildId = Long.parseLong(csvRecord.get(1));
					DiscordServer discordServer;
					if(Main.discordBot.jda.getGuildById(guildId) != null) {
						discordServer = new DiscordServer(guildId);
					}
					else {
						discordServer = new UnloadedDiscordServer(guildId);
						System.out.println("Could not find Guild for server " + name + " (" + guildId + ")");
					}
					String adminRoleString = csvRecord.get(2);
					if(!adminRoleString.isEmpty()) {
						String[] adminRoleIdStrings = csvRecord.get(2).split(",");
						long[] adminRoleIds = new long[adminRoleIdStrings.length];
						for(int i = 0; i < adminRoleIdStrings.length; i++) {
							if(!adminRoleIdStrings[i].isEmpty()) {
								adminRoleIds[i] = Long.parseLong(adminRoleIdStrings[i]);
							}
						}
						discordServer.adminRoles.setFromIds(adminRoleIds);
					}
					discordServers.add(discordServer);
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
		return discordServers.toArray(new DiscordServer[]{});
	}
}
