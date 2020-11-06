package com.gamebuster19901.excite.bot.audit;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.IntegerPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;

public class NameChangeAudit extends Audit {

	public static transient final int DB_VERSION = 0;
	
	StringPreference oldName;
	StringPreference newName;
	IntegerPreference pid;
	StringPreference fc;
			
	public NameChangeAudit(Player player, String newName) {
		super(getContext(player), getMessage(getContext(player), newName));
		this.oldName = new StringPreference(player.getName());
		this.newName = new StringPreference(newName);
		this.pid = new IntegerPreference(player.getID());
		this.fc = new StringPreference(player.getFriendCode());
	}
	
	public NameChangeAudit() {
		
	}
	
	public static void ChangeName(Player player, String newName) {
		Audit.addAudit(new NameChangeAudit(player, newName));
	}
	
	@Override
	public Audit parseAudit(CSVRecord record) {
		super.parseAudit(record);
		//0-7 is audit
		int i = super.getRecordSize();
		i++; //8 is NameChangeAudit version
		oldName = new StringPreference(record.get(i++));
		newName = new StringPreference(record.get(i++));
		pid = new IntegerPreference(record.get(i++));
		fc = new StringPreference(record.get(i++));
		return this;
	}
	
	@Override
	protected int getRecordSize() {
		return super.getRecordSize() + 5;
	}
	
	@Override
	public List<Object> getParameters() {
		List<Object> params = super.getParameters();
		params.addAll(Arrays.asList(new Object[] {new Integer(DB_VERSION), oldName, newName, pid, fc}));
		return params;
	}
	
	private static String getMessage(MessageContext context, String newName) {
		return context.getPlayerAuthor().getName() + "(" + context.getPlayerAuthor().getID() + ") changed their name to " + newName; 
	}
	
}
