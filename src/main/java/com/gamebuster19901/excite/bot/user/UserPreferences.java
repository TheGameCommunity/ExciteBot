package com.gamebuster19901.excite.bot.user;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.common.preferences.IntegerPreference;
import com.gamebuster19901.excite.output.OutputCSV;

public class UserPreferences implements OutputCSV{

	private IntegerPreference notify = new IntegerPreference("notifyThreshold", -1);
	private ProfilePreference profiles = new ProfilePreference("profiles", new Player[]{});

	@Override
	public String toCSV() {
		// TODO Auto-generated method stub
		return null;
	}

}
