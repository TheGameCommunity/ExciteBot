package com.gamebuster19901.excite.bot.audit;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.BooleanPreference;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.PermissionPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;

import static com.gamebuster19901.excite.util.Permission.ADMIN_ONLY;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

public class CommandAudit extends Audit {

	private static transient final int DB_VERSION = 1;
	
	StringPreference command;
	StringPreference serverName;
	LongPreference serverId;
	StringPreference channelName;
	LongPreference channelId;
	BooleanPreference isGuildMessage;
	BooleanPreference isPrivateMessage;
	BooleanPreference isConsoleMessage;
	BooleanPreference isAdmin;
	BooleanPreference isOperator;
	
	{
		secrecy = new PermissionPreference(ADMIN_ONLY);
	}
	
	@SuppressWarnings("rawtypes")
	public CommandAudit(MessageContext context, String command) {
		super(context, command);
		
		if(context.getServer() != null) {
			this.serverName = new StringPreference(context.getServer().getName());
			this.serverId = new LongPreference(context.getServer().getId());
		}
		else {
			this.serverName = new StringPreference("N/A");
			this.serverId = new LongPreference(-1l);
		}
		
		if(context.getChannel() != null) {
			this.channelName = new StringPreference(context.getChannel().getName());
			this.channelId = new LongPreference(context.getChannel().getIdLong());
		}
		else {
			this.channelName = new StringPreference("CONSOLE");
			this.channelId = new LongPreference(-1l);
		}
		
		this.isGuildMessage= new BooleanPreference(context.isGuildMessage());
		this.isPrivateMessage= new BooleanPreference(context.isPrivateMessage());
		this.isConsoleMessage= new BooleanPreference(context.isConsoleMessage());
		this.isAdmin= new BooleanPreference(context.isAdmin());
		this.isOperator= new BooleanPreference(context.isOperator());
	}
	
	public CommandAudit() {
		super();
	}
	
	@Override
	public Audit parseAudit(CSVRecord record) {
		super.parseAudit(record);
		//0-7 is audit
		int i = super.getRecordSize();
		System.out.println(i);
		i++; //8 is commandAudit version
		System.out.println(i);
		serverName = new StringPreference(record.get(i++));
		System.out.println(i);
		serverId = new LongPreference(Long.parseLong(record.get(i++).substring(1)));
		channelName = new StringPreference(record.get(i++));
		channelId = new LongPreference(Long.parseLong(record.get(i++).substring(1)));
		isGuildMessage = new BooleanPreference(Boolean.parseBoolean(record.get(i++)));
		isPrivateMessage = new BooleanPreference(Boolean.parseBoolean(record.get(i++)));
		isConsoleMessage = new BooleanPreference(Boolean.parseBoolean(record.get(i++)));
		isAdmin = new BooleanPreference(Boolean.parseBoolean(record.get(i++)));
		isOperator = new BooleanPreference(Boolean.parseBoolean(record.get(i++)));
		
		return this;
	}
	
	@Override
	public int getRecordSize() {
		return super.getRecordSize() + 10;
	}
	
	@Override
	public List<Object> getParameters() {
		List<Object> params = super.getParameters();
		params.addAll(Arrays.asList(new Object[] {new Integer(DB_VERSION), serverName, "`" + serverId, channelName, "`" + channelId, isGuildMessage, isPrivateMessage, isConsoleMessage, isAdmin, isOperator}));
		return params;
	}
}
