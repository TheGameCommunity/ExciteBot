package com.gamebuster19901.excite.bot.common.preferences;

import com.gamebuster19901.excite.util.Permission;

public class PermissionPreference extends Preference<Permission>{

	public PermissionPreference(Permission value) {
		super(value);
	}
	
	public PermissionPreference(String value) {
		super(Permission.valueOf(value));
	}

	@Override
	public Permission convertString(String value) {
		return Permission.valueOf(value);
	}

	@Override
	public String toString() {
		return getValue().toString();
	}

}
