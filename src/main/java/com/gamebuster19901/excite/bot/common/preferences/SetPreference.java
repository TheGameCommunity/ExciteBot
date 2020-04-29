package com.gamebuster19901.excite.bot.common.preferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SetPreference<T> extends Preference<Set<T>>{

	public SetPreference(String name, Set<T> value) {
		super(name, value);
	}
	
	@SafeVarargs
	public SetPreference(String name, T... values) {
		this(name, new HashSet<T>(Arrays.asList(values)));
	}
	
}
