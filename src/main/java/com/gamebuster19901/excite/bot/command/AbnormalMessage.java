package com.gamebuster19901.excite.bot.command;

import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.internal.entities.AbstractMessage;

public class AbnormalMessage extends AbstractMessage {

	private final long messageID;
	
	public AbnormalMessage(String content) {
		this(content, 0);
	}
	
	public AbnormalMessage(String content, long messageID) {
		super(content, null, false);
		this.messageID = messageID;
	}

	@Override
	public MessageActivity getActivity() {
		unsupported();
		return null;
	}

	@Override
	public long getIdLong() {
		return messageID;
	}

	@Override
	protected void unsupported() {
		throw new UnsupportedOperationException();
	}

}
