package com.gamebuster19901.excite.transaction;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import static com.gamebuster19901.excite.transaction.CurrencyType.*;
import static com.gamebuster19901.excite.transaction.Transaction.WalletType.*;

public class RewardTransaction extends Transaction {

	public RewardTransaction(DiscordUser balanceHolder, String message, WalletType wallet, CurrencyType currency, long amount) {
		super(balanceHolder, TransactionType.REWARD, message, wallet, currency, amount);
	}

	@Override
	public boolean isValid() {
		if(wallet == WII && currency != STARS) {
			failureReason = "Wii wallets cannot receive " + currency;
			return false;
		}
		if(amount < 1) {
			failureReason = "Rewards must award 1 or more stars";
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

	@Override
	public String getAuditMessage(MessageContext context) {
		if(context.isConsoleMessage()) {
			return balanceHolder.getIdentifierName() + " was rewarded " + amount + " " + currency + " into their " + wallet + " wallet because they " + message;
		}
		else {
			if(message == null || message.isEmpty()) {
				return context.getAuthor().getIdentifierName() + " awarded " + amount + " " + currency + " into the " + wallet + " wallet of " + balanceHolder.getIdentifierName();
			}
			return context.getAuthor().getIdentifierName() + " awarded " + amount + " " + currency + " into the " + wallet + " wallet of " + balanceHolder.getIdentifierName() + " because they " + message;
		}
	}

}
