package com.gamebuster19901.excite.transaction;

import static com.gamebuster19901.excite.transaction.CurrencyType.STARS;
import static com.gamebuster19901.excite.transaction.Transaction.WalletType.WII;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;

public class RedeemTransaction extends Transaction {

	public RedeemTransaction(DiscordUser balanceHolder, String message, long amount) {
		super(balanceHolder, TransactionType.REDEEM, message, WalletType.DISCORD, CurrencyType.STARS, amount);
	}

	@Override
	public String getAuditMessage(MessageContext context) {
		return balanceHolder.getIdentifierName() + " redeemed " + amount + " " + currency;
	}

	@Override
	public boolean isValid() {
		if(wallet == WII && currency != STARS) {
			failureReason = "Wii wallets cannot receive " + currency;
			return false;
		}
		if(amount < 1) {
			failureReason = "You must redeem 1 or more stars";
			return false;
		}
		if(balanceHolder == null) {
			failureReason = "balanceHolder is null?!";
			return false;
		}
		if(balanceHolder.getJDAUser().isBot()) {
			failureReason = balanceHolder.getIdentifierName() + " is a bot.";
			return false;
		}
		if(balanceHolder.isBanned()) {
			failureReason = balanceHolder.getIdentifierName() + " is banned.";
			return false;
		}
		if(balanceHolder.getBalance(CurrencyType.STARS) <= amount) {
			failureReason = "Insufficient Funds.";
			return false;
		}
		return true;
	}

}
