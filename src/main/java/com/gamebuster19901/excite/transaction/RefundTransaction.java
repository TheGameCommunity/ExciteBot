package com.gamebuster19901.excite.transaction;

import static com.gamebuster19901.excite.transaction.CurrencyType.STARS;
import static com.gamebuster19901.excite.transaction.Transaction.WalletType.WII;

import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import net.dv8tion.jda.api.entities.User;

public class RefundTransaction extends Transaction{

	public RefundTransaction(User balanceHolder, String message, WalletType wallet, CurrencyType currency, long amount) {
		super(balanceHolder, TransactionType.REFUND, message, wallet, currency, amount);
	}

	@Override
	public String getAuditMessage(CommandContext context) {
		final String user = DiscordUser.toDetailedString(balanceHolder);
		if(context.isConsoleMessage()) {
			return user + " was refunded " + amount + " " + currency + " into their " + wallet + " wallet due to " + message;
		}
		else {
			if(message == null || message.isEmpty()) {
				return context.getAuthor().getIdentifierName() + " refunded " + amount + " " + currency + " into the " + wallet + " wallet of " + user;
			}
			return context.getAuthor().getIdentifierName() + " refunded " + amount + " " + currency + " into the " + wallet + " wallet of " + user + " due to " + message;
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
		if(balanceHolder.isBot()) {
			failureReason = DiscordUser.toDetailedString(balanceHolder) + " is a bot.";
			return false;
		}
		if(DiscordUser.isBanned(balanceHolder)) {
			failureReason = DiscordUser.toDetailedString(balanceHolder) + " is banned.";
			return false;
		}
		return true;
	}

}
