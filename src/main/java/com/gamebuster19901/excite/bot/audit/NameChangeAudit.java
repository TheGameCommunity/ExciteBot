package com.gamebuster19901.excite.bot.audit;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.common.preferences.IntegerPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;

public class NameChangeAudit extends Audit {

	public static transient final int DB_VERSION = 0;
	
	private StringPreference oldName;
	private StringPreference newName;
	private IntegerPreference pid;
	private StringPreference fc;
			
	public NameChangeAudit(Player player, String newName) {
		super();
		this.oldName = new StringPreference(player.getName());
		this.newName = new StringPreference(newName);
		this.pid = new IntegerPreference(player.getPlayerID());
		this.fc = new StringPreference(player.getFriendCode());
	}
	
	public static void ChangeName(Player player, String newName) {
		Audit.addAudit(new NameChangeAudit(player, newName));
	}
	
}
