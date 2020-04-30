package com.gamebuster19901.excite.bot.user;

import java.util.HashSet;
import java.util.Set;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.common.preferences.SetPreference;

public class ProfilePreference extends SetPreference<Player>{

	public ProfilePreference() {
		this(new Player[]{});
	}
	
	public ProfilePreference(Player... profiles) {
		super(profiles);
	}
	
	public ProfilePreference(int... profileIDs) {
		this(getAllProfiles(profileIDs));
	}
	
	public void addProfile(int profileID) {
		value.add(Player.getPlayerByID(profileID));
	}
	
	public void addProfile(Player player) {
		value.add(player);
	}

	@Override
	public Set<Player> convertString(String value) {
		HashSet<Player> profiles = new HashSet<Player>();
		value = value.replaceAll("\"", "");
		String[] values = value.split(",");
		for(String pid : values) {
			profiles.add(Player.getPlayerByID(Integer.parseInt(pid)));
		}
		return profiles;
	}

	@Override
	public String toString() {
		String value = "\"";
		for(Player profile : getValue()) {
			value += profile.getPlayerID() + ",";
		}
		value += "\"";
		
		return value;
	}
	
	private static Player[] getAllProfiles(int[] profileIDs) {
		HashSet<Player> players = new HashSet<Player>();
		playerLoop:
		for(Player player : Wiimmfi.getKnownPlayers()) {
			for(int id : profileIDs) {
				if(player.getPlayerID() == id) {
					players.add(player);
					continue playerLoop;
				}
			}
		}
		return players.toArray(new Player[]{});
	}
	
}
