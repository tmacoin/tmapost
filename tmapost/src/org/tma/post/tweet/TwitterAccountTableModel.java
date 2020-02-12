/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.tma.peer.thin.TwitterAccount;

public class TwitterAccountTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1012645969055258357L;
	
	private List<TwitterAccount> list;
	
	public TwitterAccountTableModel(List<TwitterAccount> list) {
		this.list = list;
	}

	public int getRowCount() {
		return list.size();
	}

	public int getColumnCount() {
		return 4;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		TwitterAccount twitterAccount = list.toArray(new TwitterAccount[0])[rowIndex];
        Object value = "";
        switch (columnIndex) {
            case 0:
                value = twitterAccount.getName();
                break;
            case 1:
            	value = twitterAccount.getTmaAddress();
                break;
            case 2:
            	value = new Date(twitterAccount.getTimeStamp()).toString();
    			break;
            case 3:
            	value = twitterAccount.getDescription();
    			break;
        }
        return value;
	}

	public String getColumnName(int columnIndex) {
		String value = null;
		switch (columnIndex) {
		case 0:
			value = "Tmitter Account Name";
			break;
		case 1:
			value = "TMA Address";
			break;
		case 2:
			value = "Created on";
			break;
		case 3:
			value = "Description";
			break;
		}
		return value;
	}

	

}
