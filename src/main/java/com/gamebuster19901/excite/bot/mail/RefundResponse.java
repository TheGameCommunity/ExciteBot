package com.gamebuster19901.excite.bot.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.game.challenge.Rewardable;

public class RefundResponse extends MailReplyResponse<Wii> implements Rewardable {

	private final Rewardable rewardable;
	
	public RefundResponse(MimeMessage message, Rewardable rewardable) throws MessagingException {
		super(message);
		this.rewardable = rewardable;
	}
	
	public RefundResponse(Wii wii, Rewardable rewardable) {
		super(wii);
		this.rewardable = rewardable;
	}

	@Override
	public int getReward() {
		return rewardable.getReward();
	}

	@Override
	protected String getResponseTemplate() {
		throw new AssertionError("Not implemented");
	}
	
	@Override
	public void initVars() {
		super.initVars();
	}

}
