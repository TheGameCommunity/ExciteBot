package com.gamebuster19901.excite.bot.user;

import java.util.HashSet;
import java.util.Set;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.common.preferences.SetPreference;

public class ProfilePreference extends SetPreference<Player>{

	public ProfilePreference(String name, Player... profiles) {
		super(name, profiles);
	}

	@Override
	public Set<Player> convertString(String value) {
		HashSet<Player> profiles = new HashSet<Player>();
		String[] values = value.split(":");
		for(String pid : values) {
			profiles.add(Player.getPlayerByID(Integer.parseInt(pid)));
		}
		return profiles;
	}

	@Override
	public String toString() {
		String value = "profiles:";
		for(Player profile : getValue()) {
			value += profile.getPlayerID() + ";";
		}
		if(value.indexOf(';') != -1) {
			value = value.substring(0, value.length() - 1);
		}
		
		return value;
	}

}
