package com.gamebuster19901.excite.util;

public interface Owned<O extends Named<?>, ID> extends Named<ID> {

	public O getOwner();
	
	public default String getOwnershipString() {
		if(getOwner() != null && getOwner().isValid() && getOwner().isKnown()) {
			return getName() + "(" + getOwner().getName() + ")";
		}
		return getName();
	}
	
}
