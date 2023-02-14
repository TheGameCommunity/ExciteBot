package com.gamebuster19901.excite.transaction;

import com.gamebuster19901.excite.bot.command.CommandContext;

import net.dv8tion.jda.api.entities.User;

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
	
	final User balanceHolder;
	final TransactionType transactionType;
	final String message;
	final WalletType wallet;
	final CurrencyType currency;
	final long amount;
	
	String failureReason;
	
	public Transaction(User balanceHolder, TransactionType transactionType, String message, WalletType wallet, CurrencyType currency, long amount) {
		this.balanceHolder = balanceHolder;
		this.transactionType = transactionType;
		this.message = message;
		this.wallet = wallet;
		this.currency = currency;
		this.amount = amount;
	}
	
	public User getBalanceHolder() {
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
	
	public abstract String getAuditMessage(CommandContext context);
	
	public abstract boolean isValid();
	
}
