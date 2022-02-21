package com.gamebuster19901.excite.transaction;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;

public abstract class Transaction {

	public static enum TransactionType {
		REWARD,
		REFUND,
		REDEEM,
		DEDUCTION,
		;
	}
	
	public static enum WalletType {

		DISCORD,
		WII;
		
	}
	
	final DiscordUser balanceHolder;
	final TransactionType transactionType;
	final String message;
	final WalletType wallet;
	final CurrencyType currency;
	final long amount;
	
	String failureReason;
	
	public Transaction(DiscordUser balanceHolder, TransactionType transactionType, String message, WalletType wallet, CurrencyType currency, long amount) {
		this.balanceHolder = balanceHolder;
		this.transactionType = transactionType;
		this.message = message;
		this.wallet = wallet;
		this.currency = currency;
		this.amount = amount;
	}
	
	public DiscordUser getBalanceHolder() {
		return balanceHolder;
	}
	
	public TransactionType getTransactionType() {
		return transactionType;
	}
	
	public String getMessage() {
		return message;
	}
	
	public CurrencyType getCurrency() {
		return currency;
	}
	
	public WalletType getWallet() {
		return wallet;
	}
	
	public long getAmount() {
		return amount;
	}
	
	public final String getFailureReason() {
		return failureReason;
	}
	
	public abstract String getAuditMessage(MessageContext context);
	
	public abstract boolean isValid();
	
}
