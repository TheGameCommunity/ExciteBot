package com.gamebuster19901.excite.bot.ban;

import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.user.DurationPreference;
import com.gamebuster19901.excite.bot.user.InstantPreference;

public abstract class Ban extends Verdict{

	private DurationPreference banDuration;
	private InstantPreference banExpire;
	private LongPreference pardon;
	
}
