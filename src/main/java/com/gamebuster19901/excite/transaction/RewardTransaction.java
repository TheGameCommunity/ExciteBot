package com.gamebuster19901.excite.transaction;

import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;

import net.dv8tion.jda.api.entities.User;

import static com.gamebuster19901.excite.transaction.CurrencyType.*;
import static com.gamebuster19901.excite.transaction.Transaction.WalletType.*;

public class RewardTransaction extends Transaction {

	public RewardTransaction(User balanceHolder, String message, WalletType wallet, CurrencyType currency, long amount) {
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

	@Override
	public String getAuditMessage(CommandContext context) {
		String user = DiscordUser.toDetailedString(balanceHolder);
		if(context.isConsoleMessage()) {
			return user + " was rewarded " + amount + " " + currency + " into their " + wallet + " wallet because they " + message;
		}
		else {
			if(message == null || message.isEmpty()) {
				return DiscordUser.toDetailedString(context.getDiscordAuthor()) + " awarded " + amount + " " + currency + " into the " + wallet + " wallet of " + user;
			}
			return DiscordUser.toDetailedString(context.getDiscordAuthor()) + " awarded " + amount + " " + currency + " into the " + wallet + " wallet of " + user + " because they " + message;
		}
	}

}
