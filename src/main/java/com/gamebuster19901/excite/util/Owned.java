package com.gamebuster19901.excite.util;

public interface Owned<O, T> extends Named<T> {

	public O getOwner();
	
	public default String getOwnershipString() {
		return getName() + "(" + getOwner().toString() + ")";
	}
	
}
