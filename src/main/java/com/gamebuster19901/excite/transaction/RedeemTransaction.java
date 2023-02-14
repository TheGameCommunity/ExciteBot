package com.gamebuster19901.excite.transaction;

import static com.gamebuster19901.excite.transaction.CurrencyType.STARS;
import static com.gamebuster19901.excite.transaction.Transaction.WalletType.WII;

import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import net.dv8tion.jda.api.entities.User;

public class RedeemTransaction extends Transaction {

	public RedeemTransaction(User balanceHolder, String message, long amount) {
		super(balanceHolder, TransactionType.REDEEM, message, WalletType.DISCORD, CurrencyType.STARS, amount);
	}

	@Override
	public String getAuditMessage(CommandContext context) {
		return DiscordUser.toDetailedString(balanceHolder) + " redeemed " + amount + " " + currency;
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
		if(balanceHolder.isBot()) {
			failureReason = DiscordUser.toDetailedString(balanceHolder) + " is a bot.";
			return false;
		}
		if(DiscordUser.isBanned(balanceHolder)) {
			failureReason = DiscordUser.toDetailedString(balanceHolder) + " is banned.";
			return false;
		}
		if(DiscordUser.getBalance(balanceHolder, CurrencyType.STARS) <= amount) {
			failureReason = "Insufficient Funds.";
			return false;
		}
		return true;
	}

}
