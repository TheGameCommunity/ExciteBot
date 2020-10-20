package com.gamebuster19901.excite.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class DataPoint<T extends Member> {

	private final Object obj;
	private final Member member;
	
	public DataPoint(Object o, T e) {
		if(e instanceof Method || e instanceof Field) {
			obj = o;
			member = e;
		}
		else {
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	public Object getValue() {
		try {
			if(member instanceof Field) {
				return ((Field) member).get(obj);
			}
			if(member instanceof Method) {
				return ((Method)member).invoke(obj);
			}
			throw new AssertionError(member.getClass());
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}
	
	public String getName() {
		return member.getName();
	}
	
}
