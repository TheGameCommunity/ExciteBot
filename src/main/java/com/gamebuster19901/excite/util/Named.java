package com.gamebuster19901.excite.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public interface Named<T> extends Identified {

	public String getName();
	
	public default String getIdentifierName() {
		return getName() + "(" + getID() + ")";
	}
	
	public default String getLookingForMatch() {
		return getName() + " " + getID();
	}
	
	public default boolean matches(String lookingFor) {
		return getName().equalsIgnoreCase(lookingFor) || lookingFor.equals(((Long)getID()).toString()) || getIdentifierName().equalsIgnoreCase(lookingFor) 
				|| getLookingForMatch().equalsIgnoreCase(lookingFor);
	}
	
	public T asObj();
	
	public static Named<User> of(User user) {
		return new Named<User>() {
			@Override
			public long getID() {
				return user.getIdLong();
			}

			@Override
			public String getName() {
				return user.getName() + user.getDiscriminator();
			}

			@Override
			public User asObj() {
				return user;
			}
		};
	}
	
	public static Named<User> of(Member member) {
		return of(member.getUser());
	}
	
}
