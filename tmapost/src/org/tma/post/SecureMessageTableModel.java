/*******************************************************************************
 * Copyright � 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.blockchain.SecureMessage;
import org.tma.persistance.Encryptor;
import org.tma.util.Base58;
import org.tma.util.StringUtil;

public class SecureMessageTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1012645969055258357L;
	private static final Encryptor encryptor = new Encryptor();
	private static final Logger logger = LogManager.getLogger();
	
	private List<SecureMessage> list;
	private PrivateKey privateKey;
	
	public SecureMessageTableModel(List<SecureMessage> list, PrivateKey privateKey) {
		this.list = list;
	}

	public int getRowCount() {
		return list.size();
	}

	public int getColumnCount() {
		return 2;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		SecureMessage message = list.toArray(new SecureMessage[0])[rowIndex];
        Object value = null;
        switch (columnIndex) {
            case 0:
                value = StringUtil.getStringFromKey(message.getSender());
                break;
            case 1:
				try {
					value = new String(encryptor.decryptAsymm(Base58.decode(message.getText()), privateKey));
				} catch (IOException | GeneralSecurityException e) {
					logger.error(e.getMessage(), e);
				}
                break;
        }
        return value;
	}

	public String getColumnName(int columnIndex) {
		String value = null;
        switch (columnIndex) {
        case 0:
            value = "Sender";
            break;
        case 1:
            value = "Text";
            break;
    }
    return value;
	}

	

}
