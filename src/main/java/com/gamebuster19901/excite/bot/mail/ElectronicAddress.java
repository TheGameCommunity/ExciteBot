package com.gamebuster19901.excite.bot.mail;

public interface ElectronicAddress {

	public abstract String getType();
	
	public abstract boolean equals(Object o);
	
	public abstract String toString();
	
	public default String getEmail() {
		return toString();
	}
	
}
