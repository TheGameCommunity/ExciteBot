package com.gamebuster19901.excite.util;

import java.util.concurrent.ConcurrentHashMap;

public class TypedConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V>{
	private static final long serialVersionUID = 85343870981493928L;
	
	private Class<? extends Object> type;
	
	public TypedConcurrentHashMap(Class<V> type) {
		this.type = type;
	}
	
	public Class<? extends Object> getType() {
		return type;
	}
	
	@Override
	public V put(K key, V value) {
		if(type.isAssignableFrom(value.getClass())) {
			return super.put(key, value);
		}
		throw new IllegalArgumentException(value.getClass().getName() + " is not a " + type.getName());
	}
	
}
