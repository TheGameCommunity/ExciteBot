package com.gamebuster19901.excite.util;

public interface Named extends Identified {

	public String getName();
	
	public default String getIdentifierName() {
		return getName() + "(" + getID() + ")";
	}
	
}
