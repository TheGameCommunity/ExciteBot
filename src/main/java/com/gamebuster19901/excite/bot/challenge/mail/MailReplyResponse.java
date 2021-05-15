package com.gamebuster19901.excite.bot.challenge.mail;

import com.gamebuster19901.excite.bot.user.Wii;

public abstract class MailReplyResponse {

	protected final Wii wii;
	
	public MailReplyResponse(Wii wii) {
		this.wii = wii;
	}
	
	@Override
	public void respond() {
		send();
	}

	protected final void send() {
		
	}
	
}
