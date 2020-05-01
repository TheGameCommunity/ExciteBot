package com.gamebuster19901.excite.bot.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.output.OutputCSV;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class DiscordServer implements OutputCSV{

	private static final File SERVER_PREFS = new File("./run/serverPreferences.csv");
	private static final File OLD_SERVER_PREFS = new File("./run/serverPreferences.csv.old");
	private static HashSet<DiscordServer> servers;
	
	static {
		try {
			if(!SERVER_PREFS.exists()) {
				SERVER_PREFS.getParentFile().mkdirs();
				SERVER_PREFS.createNewFile();
			}
			servers = new HashSet<DiscordServer>(Arrays.asList(getEncounteredServersFromFile()));
		}
		catch(IOException e) {
			throw new IOError(e);
		}
	}
	
	private final Guild server;
	private final long id;
	
	RolePreference adminRoles;
	
	public DiscordServer(Guild guild) {
		this.server = guild;
		this.id = guild.getIdLong();
		this.adminRoles = new RolePreference(this);
	}

	@Override
	public String toCSV() {
		return server.getName() + "," + server.getId() + "," + adminRoles;
	}

	public Guild getGuild() {
		return server;
	}
	
	public Role getRoleById(long id) {
		return server.getRoleById(id);
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
		if(!servers.contains(server)) {
			servers.add(server);
			System.out.println("New server found! " + server.getGuild().getName());
		}
	}
	
	public static void updateServerList() {
		for(Guild guild : Main.discordBot.jda.getGuilds()) {
			addServer(new DiscordServer(guild));
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
			for(DiscordServer discordServer : servers) {
				writer.write(discordServer.toCSV());
				writer.newLine();
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
					long guildId = Long.parseLong(csvRecord.get(1));
					Guild guild = Main.discordBot.jda.getGuildById(guildId);
					if(guild != null) {
						DiscordServer discordServer = new DiscordServer(Main.discordBot.jda.getGuildById(csvRecord.get(1)));
						String adminRoleString = csvRecord.get(2);
						if(!adminRoleString.isEmpty()) {
							String[] adminRoleIdStrings = csvRecord.get(2).replaceAll("\"", "").split(",");
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
					else {
						System.out.println("Could not find server (" + guildId + ")");
					}
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
