package com.gamebuster19901.excite.bot.common.preferences;

public abstract class Preference<T> {
	protected final Class<T> type;
	protected T value;
	
	@SuppressWarnings("unchecked")
	public Preference(T value) {
		this.type = (Class<T>) value.getClass();
		this.value = value;
	}
	
	public T setValue(T value) {
		this.value = value;
		return this.value;
	}
	
	public T setValue(String value) {
		this.value = convertString(value);
		return this.value;
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
