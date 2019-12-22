/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.rating;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.tma.peer.thin.Ratee;

public class RateeTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1012645969055258357L;
	
	private List<Ratee> list;
	
	public RateeTableModel(List<Ratee> list) {
		this.list = list;
	}

	public int getRowCount() {
		return list.size();
	}

	public int getColumnCount() {
		return 4;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Ratee ratee = list.toArray(new Ratee[0])[rowIndex];
        Object value = "";
        switch (columnIndex) {
            case 0:
                value = ratee.getName();
                break;
            case 1:
            	value = ratee.getDescription().length() > 50? ratee.getDescription().substring(0, 50) + " ...": ratee.getDescription();
    			break;
            case 2:
            	value = new Date(ratee.getTimeStamp()).toString();
    			break;
            case 3:
            	value = ratee.getTotalRating();
    			break;
        }
        return value;
	}

	public String getColumnName(int columnIndex) {
		String value = null;
		switch (columnIndex) {
		case 0:
			value = "Post";
			break;
		case 1:
			value = "Description";
			break;
		case 2:
			value = "Created on";
			break;
		case 3:
			value = "Rating";
			break;
		}
		return value;
	}

	

}
