package com.gamebuster19901.excite.bot.server;

import java.util.HashSet;
import java.util.Set;

import com.gamebuster19901.excite.bot.common.preferences.SetPreference;

import net.dv8tion.jda.api.entities.Role;

public class RolePreference extends SetPreference<Role>{

	private final DiscordServer server;
	
	public RolePreference(DiscordServer server) {
		this.server = server;
	}
	
	@Override
	public Set<Role> convertString(String value) {
		HashSet<Role> roles = new HashSet<Role> ();
		value = value.replaceAll("\"", "");
		String[] values = value.split(",");
		for(String id : values) {
			Role role = server.getRoleById(Long.parseLong(id));
			if(role != null) {
				roles.add(server.getRoleById(Long.parseLong(id)));
			}
			else {
				System.out.println("Could not find role (" + role + ") in server (" + server.getGuild().getIdLong() + ")");
			}
		}
		return roles;
	}

	@Override
	public String toString() {
		String value = "\"'";
		for(Role role : getValue()) {
			value += role.getIdLong() + ",";
		}
		if(value.length() > 0 && value.charAt(value.length() - 1) == ',') {
			value = value.substring(0, value.length() - 1);
		}
		value += "\"";
		
		return value;
	}

	public void setFromIds(long[] ids) {
		HashSet<Role> roles = new HashSet<Role>();
		for(long id : ids) {
			Role role = this.server.getGuild().getRoleById(id);
			if(role != null) {
				roles.add(role);
			}
			else {
				System.out.println("Could not find role (" + id + ") in " + server.getGuild().getName());
			}
		}
		setValue(roles);
	}

}
