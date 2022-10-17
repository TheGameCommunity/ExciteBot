package com.gamebuster19901.excite.bot.server;

import java.io.IOError;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.gamebuster19901.excite.bot.database.Comparator.EQUALS;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Result;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.util.Named;

import static com.gamebuster19901.excite.bot.database.Column.*;

import static com.gamebuster19901.excite.bot.database.Table.DISCORD_SERVERS;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class DiscordServer implements Named<Long> {
	
	protected final long id;
	
	public DiscordServer(Result results) throws SQLException {
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
		PreparedStatement ps = context.getConnection().prepareStatement("INSERT INTO " + DISCORD_SERVERS + " (" + SERVER_ID + ", " + SERVER_NAME + ") VALUES (?, ?)");
		ps.setLong(1, guildId);
		ps.setString(2, name);
		ps.execute();
		return getServer(context, guildId);
	}

	public Guild getGuild() {
		return Main.discordBot.jda.getGuildById(getID());
	}
	
	public Role getRoleById(long id) {
		return getGuild().getRoleById(id);
	}
	
	public Long getID() {
		return id;
	}
	
	public Role[] getRoles() {
		return getGuild().getRoles().toArray(new Role[]{});
	}
	
	@SuppressWarnings("deprecation")
	public String getName() {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, SERVER_NAME, DISCORD_SERVERS, new Comparison(SERVER_ID, EQUALS, getID()));
			if(result.next()) {
				return result.getString(SERVER_NAME);
			}
			else {
				throw new AssertionError("Could not find name for server " + getID());
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public void setName(String name) {
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_SERVERS, SERVER_NAME, name, new Comparison(SERVER_ID, EQUALS, getID()));
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("deprecation")
	public String getPrefix() {
		try {
			Result result = Table.selectColumnsFromWhere(ConsoleContext.INSTANCE, SERVER_PREFIX, DISCORD_SERVERS, new Comparison(SERVER_ID, EQUALS, getID()));
			if(result.next()) {
				return result.getString(SERVER_PREFIX);
			}
			else {
				throw new AssertionError("Could not find prefix for server " + getID());
			}
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public void setPrefix(String prefix) {
		try {
			Table.updateWhere(ConsoleContext.INSTANCE, DISCORD_SERVERS, SERVER_PREFIX, prefix, new Comparison(SERVER_ID, EQUALS, getID()));
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public boolean isLoaded() {
		return Main.discordBot.jda.getGuildById(getID()) != null;
	}
	
	@Override
	public int hashCode() {
		return Long.valueOf(getID()).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DiscordServer) {
			return ((DiscordServer)o).id == id;
		}
		return false;
	}
	
	public String toString() {
		return getName() + " (" + getID() + ")";
	}
	
	@SuppressWarnings("rawtypes")
	public static DiscordServer getServer(MessageContext context, long serverId) {
		try {
			Result results = Table.selectAllFromWhere(context, DISCORD_SERVERS, new Comparison(SERVER_ID, EQUALS, serverId));
			
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
			Result results = Table.selectAllFrom(ConsoleContext.INSTANCE, DISCORD_SERVERS);
			while(results.next()) {
				servers.add(new DiscordServer(results));
			}
			return servers.toArray(new DiscordServer[] {});
		}
		catch (SQLException e) {
			throw new IOError(e);
		}
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
