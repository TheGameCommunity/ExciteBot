package com.gamebuster19901.excite.bot.common.preferences;

public abstract class Preference<T> {
	
	protected final String name;
	protected final Class<T> type;
	protected T value;
	
	@SuppressWarnings("unchecked")
	public Preference(String name, T value) {
		this.name = name;
		this.type = (Class<T>) value.getClass();
		this.value = value;
	}
	
	public final String getName() {
		return name;
	}
	
	public final void setValue(T value) {
		this.value = value;
	}
	
	public final void setValue(String value) {
		this.value = convertString(value);
	}
	
	public abstract T convertString(String value);
	
	public final T getValue() {
		return value;
	}
	
	public final Class<T> getType() {
		return type;
	}
	
	public abstract String toString();
	
}
