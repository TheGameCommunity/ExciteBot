package com.gamebuster19901.excite.bot.mail;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.bot.user.Wii.InvalidWii;

public interface ElectronicAddress {

	public abstract String getType();
	
	public abstract boolean equals(Object o);
	
	public abstract String toString();
	
	public default String getEmail() {
		return toString();
	}
	
	public default Address toAddress() throws AddressException {
		return new InternetAddress(toString());
	}
	
	public static ElectronicAddress getAddress(Address address) {
		EmailAddress email = new EmailAddress(address);
		Wii wii = Wii.getWii(email);
		if(wii instanceof InvalidWii) {
			return email;
		}
		return wii;
	}
	
	public static ElectronicAddress getAddress(Address[] address) {
		return getAddress(address[0]);
	}
	
}
