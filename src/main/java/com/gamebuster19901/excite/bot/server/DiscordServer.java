package com.gamebuster19901.excite.bot.server;

import java.io.IOError;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.gamebuster19901.excite.bot.database.Comparator.EQUALS;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.database.sql.ResultSet;

import static com.gamebuster19901.excite.bot.database.Column.*;

import static com.gamebuster19901.excite.bot.database.Table.DISCORD_SERVERS;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class DiscordServer {
	
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
	
	@SuppressWarnings({ "rawtypes", "resource" })
	public static DiscordServer addServer(MessageContext context, long guildId, String name) throws SQLException {
		PreparedStatement ps = context.getConnection().prepareStatement("INSERT INTO " + DISCORD_SERVERS + " (?, ?) VALUES (?, ?)");
		ps.setString(1, SERVER_ID);
		ps.setString(2, SERVER_NAME);
		ps.setLong(3, guildId);
		ps.setString(4, name);
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
	
	@SuppressWarnings("deprecation")
	public String getName() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, SERVER_NAME, DISCORD_SERVERS, SERVER_ID, EQUALS, getId());
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
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_SERVERS, SERVER_NAME, name, SERVER_ID, EQUALS, getId());
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("deprecation")
	public String getPrefix() {
		try {
			ResultSet result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, SERVER_PREFIX, DISCORD_SERVERS, SERVER_ID, EQUALS, getId());
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
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_SERVERS, SERVER_NAME, prefix, SERVER_ID, EQUALS, getId());
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public boolean isLoaded() {
		return Main.discordBot.jda.getGuildById(getId()) != null;
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
	
	@SuppressWarnings("rawtypes")
	public static DiscordServer getServer(MessageContext context, long serverId) {
		try {
			ResultSet results = Table.selectAllFromWhere(context, DISCORD_SERVERS, SERVER_ID, EQUALS, serverId);
			
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
			ResultSet results = Table.selectAllFrom(ConsoleContext.INSTANCE, DISCORD_SERVERS);
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
