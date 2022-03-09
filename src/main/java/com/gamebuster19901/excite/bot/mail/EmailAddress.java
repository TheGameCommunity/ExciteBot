package com.gamebuster19901.excite.bot.mail;

import javax.mail.Address;

public class EmailAddress implements ElectronicAddress {

	private final String email;
	
	public EmailAddress(String address) {
		this.email = address;
	}
	
	public EmailAddress(Address address) {
		this(address.toString());
	}

	@Override
	public String getType() {
		return "rfc822";
	}
	
	@Override
	public String toString() {
		return email;
	}

}
