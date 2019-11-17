/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tma.blockchain.Transaction;

public class TransactionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1012645969055258357L;
	
	private Set<Transaction> set;
	
	public TransactionTableModel(Set<Transaction> set) {
		this.set = set;
	}

	public int getRowCount() {
		return set.size();
	}

	public int getColumnCount() {
		return 8;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Transaction transaction = set.toArray(new Transaction[0])[rowIndex];
        Object value = null;
        switch (columnIndex) {
            case 0:
                value = transaction.getTransactionId();
                break;
            case 1:
                value = transaction.getSenderAddress();
                break;
            case 2:
                value = transaction.getRecipient();
                break;
            case 3:
                value = transaction.getValue().toNumberOfCoins();
                break;
            case 4:
                value = transaction.getFee().toNumberOfCoins();
                break;
            case 5:
                value = transaction.getBlockHash();
                break;
            case 6:
                value = transaction.getData() == null? "": transaction.getData();
                break;
            case 7:
                value = transaction.getExpiringData() == null? "": transaction.getExpiringData().getData() == null? "": transaction.getExpiringData().getData();
                break;
        }
        return value;
	}

	public String getColumnName(int columnIndex) {
		String value = null;
        switch (columnIndex) {
        case 0:
            value = "Transaction Id";
            break;
        case 1:
            value = "Sender";
            break;
        case 2:
            value = "Recipient";
            break;
        case 3:
            value = "Value";
            break;
        case 4:
            value = "Fee";
            break;
        case 5:
            value = "Block Hash";
            break;
        case 6:
            value = "Data";
            break;
        case 7:
            value = "Expiring Data";
            break;
    }
    return value;
	}

	

}
