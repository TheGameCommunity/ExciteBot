package com.gamebuster19901.excite.transaction;

import static com.gamebuster19901.excite.transaction.CurrencyType.STARS;
import static com.gamebuster19901.excite.transaction.Transaction.WalletType.WII;

import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;

public class RefundTransaction extends Transaction{

	public RefundTransaction(DiscordUser balanceHolder, String message, WalletType wallet, CurrencyType currency, long amount) {
		super(balanceHolder, TransactionType.REFUND, message, wallet, currency, amount);
	}

	@Override
	public String getAuditMessage(CommandContext context) {
		if(context.isConsoleMessage()) {
			return balanceHolder.getIdentifierName() + " was refunded " + amount + " " + currency + " into their " + wallet + " wallet due to " + message;
		}
		else {
			if(message == null || message.isEmpty()) {
				return context.getAuthor().getIdentifierName() + " refunded " + amount + " " + currency + " into the " + wallet + " wallet of " + balanceHolder.getIdentifierName();
			}
			return context.getAuthor().getIdentifierName() + " refunded " + amount + " " + currency + " into the " + wallet + " wallet of " + balanceHolder.getIdentifierName() + " due to " + message;
		}
	}

	@Override
	public boolean isValid() {
		if(wallet == WII && currency != STARS) {
			failureReason = "Wii wallets cannot receive " + currency;
			return false;
		}
		if(amount < 1) {
			failureReason = "Refunds must return 1 or more stars";
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
		return true;
	}

}
