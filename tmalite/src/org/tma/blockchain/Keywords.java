/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tma.util.StringUtil;

public class Keywords implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5838720523392669808L;
	private Map<String, String> map = new HashMap<String, String>();
	private String hash;
	private String transactionId;
	
	private String calculateHash() {
		List<String> keys = new ArrayList<String>(map.keySet());
		List<String> values = new ArrayList<String>(map.values());
		String hash = StringUtil.applySha256(StringUtil.getMerkleTreeRoot(keys) + StringUtil.getMerkleTreeRoot(values));
		return hash;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public String getHash() {
		if(hash == null) {
			hash = calculateHash();
		}
		return hash;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public boolean isValid() {
		int MAX_LENGTH = 64;
		if(getMap().isEmpty()) {
			return false;
		}
		for(String key: getMap().keySet()) {
			if(key.length() > MAX_LENGTH) {
				return false;
			}
			if(getMap().get(key).length() > MAX_LENGTH) {
				return false;
			}
		}
		if(!calculateHash().equals(hash)) {
			return false;
		}
		
		return true;
	}

	
	
}
