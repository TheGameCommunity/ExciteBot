package com.gamebuster19901.excite.bot.server;

import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.output.OutputCSV;

public class ServerPreferences implements OutputCSV {
	private static final int DB_VERSION = 2;
	
	private StringPreference name;
	private LongPreference id;
	private StringPreference prefix = new StringPreference(Commands.DEFAULT_PREFIX);
	
	public ServerPreferences(DiscordServer discordServer) {
		if(discordServer instanceof UnloadedDiscordServer) {
			name = new StringPreference("UNLOADED_DISCORD_SERVER");
		}
		else {
			name = new StringPreference(discordServer.getGuild().getName());
		}
		id = new LongPreference(discordServer.getId());
	}
	
	public ServerPreferences() {
		
	}
	
	public void parsePreferences(String name, long id, String prefix) {
		this.name = new StringPreference(name);
		this.id = new LongPreference(id);
		this.prefix = new StringPreference(prefix);
	}

	@Override
	public String toCSV() {
		try (
			StringWriter writer = new StringWriter();
			CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withTrim(false));	
		)
		{
			printer.printRecord(DB_VERSION, name, id, prefix);
			printer.flush();
			return writer.toString();
		}
		catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	public String getName() {
		return (String) name.getValue();
	}
	
	public void setName(String name) {
		this.name = new StringPreference(name);
	}
	
	public long getId() {
		return id.getValue();
	}
	
	public String getPrefix() {
		return (String) prefix.getValue();
	}
	
	public boolean setPrefix(String prefix) {
		if(Commands.isValidPrefix(prefix)) {
			this.prefix = new StringPreference(prefix);
			return true;
		}
		return false;
	}
}
