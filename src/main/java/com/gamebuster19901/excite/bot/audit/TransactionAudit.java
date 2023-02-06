package com.gamebuster19901.excite.bot.audit;

import java.io.IOError;
import java.sql.SQLException;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.CommandContext;
import com.gamebuster19901.excite.bot.database.Comparison;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.database.Row;
import com.gamebuster19901.excite.bot.database.Table;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.transaction.Transaction;

import static com.gamebuster19901.excite.bot.database.Column.*;
import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Comparator.*;

public class TransactionAudit extends Audit {

	Audit parentData;
	
	protected TransactionAudit(Row row) {
		super(row, AuditType.TRANSACTION_AUDIT);
	}
	
	public static TransactionAudit addTransactionAudit(CommandContext context, Transaction transaction) {
		Audit parent = Audit.addAudit(context, AuditType.TRANSACTION_AUDIT, getMessage(context, transaction));
		
		PreparedStatement st;
		
		try {
			st = Insertion.insertInto(AUDIT_TRANSACTIONS)
			.setColumns(AUDIT_ID, RECIPIENT, TRANSACTION_TYPE, AMOUNT, CURRENCY, WALLET)
			.to(parent.getID(), transaction.getBalanceHolder(), transaction.getTransactionType(), transaction.getAmount(), transaction.getCurrency(), transaction.getWallet())
			.prepare(context, true);
			
			st.execute();
			
			TransactionAudit ret = getTransactionAuditByAuditID(ConsoleContext.INSTANCE, parent.getID());
			ret.parentData = parent;
			return ret;
		}
		catch(SQLException e) {
			throw new IOError(e);
		}
		
	}

	public static TransactionAudit getTransactionAuditByAuditID(CommandContext context, long auditID) {
		return new TransactionAudit(Table.selectAllFromJoinedUsingWhere(context, AUDITS, AUDIT_TRANSACTIONS, AUDIT_ID, new Comparison(AUDIT_ID, EQUALS, auditID)).getRow(true));
	}
	
	private static String getMessage(CommandContext context, Transaction transaction) {
		return transaction.getAuditMessage(context);
	}
}
