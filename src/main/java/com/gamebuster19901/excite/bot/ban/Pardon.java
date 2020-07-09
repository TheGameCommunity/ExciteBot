package com.gamebuster19901.excite.bot.ban;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.bot.command.MessageContext;

public class Pardon extends Audit{

	@SuppressWarnings("rawtypes")
	public Pardon(MessageContext pardonContext, long banId) {
		super(pardonContext);
	}
	
	@SuppressWarnings("rawtypes")
	public Pardon(MessageContext pardonContext, Audit audit) {
		super(pardonContext);
		String errMessage;
		if(audit == null || audit instanceof Pardon) {
			throw new IllegalArgumentException(errMessage = audit != null ? audit.auditId.getValue() + "" : "null");
		}
	}

	@Override
	public Audit parseAudit(CSVRecord csv) {
		super.parseAudit(csv);
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static void pardon(MessageContext context, long banId, String reason) {
		if(Audit.BANS.containsKey(banId)) {
			Ban ban = Audit.BANS.get(banId);
			ban.pardon(context);
		}
		else {
			context.sendMessage("Could not find ban #" + banId);
		}
	}
	
}
