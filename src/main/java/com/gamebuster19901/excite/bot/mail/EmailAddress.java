package com.gamebuster19901.excite.bot.mail;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public interface EmailAddress {

	public abstract String getType();
	
	public abstract boolean equals(Object o);
	
	public abstract String toString();
	
	public default String getEmail() {
		return toString();
	}
	
	public abstract String getNamePart();
	
	public abstract String getDomainPart();
	
	public default Address toAddress() throws AddressException {
		return new InternetAddress(getEmail());
	}
}
