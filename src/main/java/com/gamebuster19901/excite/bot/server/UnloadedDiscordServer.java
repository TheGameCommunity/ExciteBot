package com.gamebuster19901.excite.bot.server;

import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class UnloadedDiscordServer extends DiscordServer{

	public UnloadedDiscordServer(long guildId) {
		super(guildId);
	}
	
	@Override
	public Guild getGuild() {
		throw new AssertionError();
	}
	
	@Override
	public Role getRoleById(long id) {
		throw new AssertionError();
	}
	
	@Override
	public Role[] getRoles() {
		throw new AssertionError();
	}
	
	@Override
	public String toCSV() {
		try (
			StringWriter writer = new StringWriter();
			CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withTrim(false));
		)
		{
			printer.printRecord("UNLOADED_DISCORD_SERVER", id, adminRoles);
			printer.flush();
			return writer.toString();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

}
