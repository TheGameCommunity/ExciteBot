package com.gamebuster19901.excite.util;

import com.gamebuster19901.excite.bot.command.MessageContext;

public enum Permission {

	ANYONE,
	ADMIN_ONLY,
	OPERATOR_ONLY;
	
	@SuppressWarnings("rawtypes")
	public boolean hasPermission(MessageContext context) {
		switch(this) {
			case ANYONE:
				return true;
			case ADMIN_ONLY:
				return context.isAdmin();
			case OPERATOR_ONLY:
				return context.isOperator();
			default:
				throw new AssertionError();
		}
	}
	
}
