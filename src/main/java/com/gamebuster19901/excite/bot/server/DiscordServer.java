package com.gamebuster19901.excite.bot.server;

import java.io.IOError;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Table;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class DiscordServer {
	public static final String SERVER_ID = "server_id";
	public static final String NAME = "name";
	public static final String PREFIX = "prefix";
	
	public static final String SERVER_ID_EQUALS = SERVER_ID + " =";
	
	protected final long id;
	
	public DiscordServer(ResultSet results) throws SQLException {
		this.id = results.getLong(SERVER_ID);
	}
	
	public DiscordServer(long guildId) {
		this.id = guildId;
	}
	
	public static void addServer(Guild guild) {
		if(getServer(ConsoleContext.INSTANCE, guild.getIdLong()) == null) {
			try {
				addServer(ConsoleContext.INSTANCE, guild.getIdLong(), guild.getName());
			}
			catch(SQLException e) {
				throw new AssertionError("Unable to add new discord server ", e);
			}
		}
	}
	
	public static DiscordServer addServer(MessageContext context, long guildId, String name) throws SQLException {
		PreparedStatement ps = context.getConnection().prepareStatement("INSERT INTO ? (?, ?) VALUES (?, ?)");
		ps.setString(1, Table.DISCORD_SERVERS.toString());
		ps.setString(2, SERVER_ID);
		ps.setString(3, NAME);
		ps.setLong(4, guildId);
		ps.setString(5, name);
		ps.execute();
		return getServer(context, guildId);
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
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, NAME, Table.DISCORD_SERVERS, idEqualsThis());
			if(result.next()) {
				return result.getString(1);
			}
			else {
				throw new AssertionError("Could not find name for server " + getId());
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public void setName(String name) {
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, Table.DISCORD_SERVERS, NAME, name, idEqualsThis());
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public String getPrefix() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, PREFIX, Table.DISCORD_SERVERS, idEqualsThis());
			if(result.next()) {
				return result.getString(1);
			}
			else {
				throw new AssertionError("Could not find prefix for server " + getId());
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public void setPrefix(String prefix) {
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, Table.DISCORD_SERVERS, NAME, prefix, idEqualsThis());
		} catch (SQLException e) {
			throw new IOError(e);
		}
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
	
	public String idEqualsThis() {
		return SERVER_ID_EQUALS + getId();
	}
	
	@SuppressWarnings("rawtypes")
	public static DiscordServer getServer(MessageContext context, long serverId) {
		try {
			ResultSet results = Table.selectAllFromWhere(context, Table.DISCORD_SERVERS, SERVER_ID_EQUALS + serverId);
			
			if(results.next()) {
				return new DiscordServer(results.getLong(SERVER_ID));
			}
			return null;
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static DiscordServer[] getKnownDiscordServers() {
		try {
			ArrayList<DiscordServer> servers = new ArrayList<DiscordServer>();
			ResultSet results = Table.selectAllFrom(ConsoleContext.INSTANCE, Table.DISCORD_SERVERS);
			while(results.next()) {
				servers.add(new DiscordServer(results));
			}
			return servers.toArray(new DiscordServer[] {});
		}
		catch (SQLException e) {
			throw new IOError(e);
		}
	}

}
