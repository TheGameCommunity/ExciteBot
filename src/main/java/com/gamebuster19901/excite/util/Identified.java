package com.gamebuster19901.excite.util;

public interface Identified<ID> {

	public ID getID();
	
	public default boolean isKnown() {
		return true;
	}
	
}
