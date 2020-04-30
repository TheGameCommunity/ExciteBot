package com.gamebuster19901.excite.bot.common.preferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SetPreference<T> extends Preference<Set<T>>{

	public SetPreference(Set<T> value) {
		super(value);
	}
	
	@SafeVarargs
	public SetPreference(T... values) {
		this(new HashSet<T>(Arrays.asList(values)));
	}
	
}
