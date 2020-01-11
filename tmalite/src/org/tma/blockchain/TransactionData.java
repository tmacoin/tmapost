/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.blockchain;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

public class TransactionData implements Serializable {

	private static final long serialVersionUID = -287206246363489154L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	public static final int DATA_LENGTH = 32672;
	public static final int YEAR = 525600;
	
	private String transactionId;
	private String hash;
	private String data;
	private long validForNumberOfBlocks;
	
	public TransactionData(String data, long validForNumberOfBlocks) {
		this.data = StringUtils.substring(data, 0, DATA_LENGTH);
		this.validForNumberOfBlocks = validForNumberOfBlocks < YEAR? validForNumberOfBlocks: YEAR;
		if(data != null) {
			hash = StringUtil.applySha256(getData());
		}
	}
	
	public TransactionData(String data) {
		this(data, YEAR);
	}
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getData() {
		return StringUtils.substring(data, 0, DATA_LENGTH);
	}

	public long getValidForNumberOfBlocks() {
		return validForNumberOfBlocks;
	}

	public String toString() {
		return "{data='" +getData() + "}";
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isValid() {
		
		if(getData() !=null && !getHash().equals(StringUtil.applySha256(getData()))) {
			logger.debug("Expiring data hash is not valid");
			return false;
		}
		
		return true;
	}
	
}
