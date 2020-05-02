package com.gamebuster19901.excite.bot.server;

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
		return "UNKNOWN_GUILD" + "," + id + "," + adminRoles;
	}

}
