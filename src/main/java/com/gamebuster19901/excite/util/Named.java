package com.gamebuster19901.excite.util;

import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.bot.user.NamedDiscordUser;

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
	
	public default String toDetailedString() {
		return getIdentifierName();
	}
	
	public default String getAsMention() {
		return "";
	}
	
	public static String toDetailedString(Object object) {
		if(object instanceof Named) {
			return ((Named<?>) object).toDetailedString();
		}
		else if(object instanceof User) {
			return DiscordUser.toDetailedString((User) object);
		}
		else {
			return object.toString();
		}
	}
	
	public default boolean matches(String lookingFor) {
		return getName().equalsIgnoreCase(lookingFor) || lookingFor.equals(((Long)getID()).toString()) || getIdentifierName().equalsIgnoreCase(lookingFor) 
				|| getLookingForMatch().equalsIgnoreCase(lookingFor);
	}
	
	public T asObj();
	
	public static Named<? extends User> of(User user) {
		return NamedDiscordUser.of(user);
	}
	
	public static Named<? extends User> of(Member member) {
		return of(member.getUser());
	}
	
}
