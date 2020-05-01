package com.gamebuster19901.excite.bot.server;

import java.util.HashSet;
import java.util.Set;

import com.gamebuster19901.excite.bot.common.preferences.SetPreference;

import net.dv8tion.jda.api.entities.Role;

public class RolePreference extends SetPreference<Long>{

	private final DiscordServer server;
	
	public RolePreference(DiscordServer server) {
		this.server = server;
	}
	
	public void addRole(Role role) {
		this.value.add(role.getIdLong());
	}
	
	public void addRole(long id) {
		this.value.add(id);
	}
	
	public void removeRole(Role role) {
		this.value.remove(role.getIdLong());
	}
	
	public void removeRole(long id) {
		this.value.remove(id);
	}
	
	@Override
	public Set<Long> convertString(String value) {
		HashSet<Long> roles = new HashSet<Long> ();
		value = value.replaceAll("\"", "");
		String[] values = value.split(",");
		for(String id : values) {
			roles.add(Long.parseLong(id));
		}
		return roles;
	}

	@Override
	public String toString() {
		String value = "\"'";
		for(Long role : getValue()) {
			value += role + ",";
		}
		if(value.length() > 0 && value.charAt(value.length() - 1) == ',') {
			value = value.substring(0, value.length() - 1);
		}
		value += "\"";
		
		return value;
	}

	public void setFromIds(long[] ids) {
		HashSet<Long> roles = new HashSet<Long>();
		for(long id : ids) {
			roles.add(id);
		}
		setValue(roles);
	}

}
