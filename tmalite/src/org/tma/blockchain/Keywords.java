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
import java.util.Set;

import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

public class Keywords implements Serializable {

	private static final long serialVersionUID = 5838720523392669808L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	public static final int KEYWORD_MAX_LENGTH = 64;
	
	
	private Map<String, String> map = new HashMap<String, String>();
	private String hash;
	private String transactionId;
	
	private String calculateHash() {
		List<String> keys = new ArrayList<String>(map.keySet());
		List<String> values = new ArrayList<String>(map.values());
		String hash = StringUtil.applySha256(StringUtil.getMerkleTreeRoot(keys) + StringUtil.getMerkleTreeRoot(values));
		return hash;
	}

	private Map<String, String> getMap() {
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
		if(getMap().isEmpty()) {
			logger.debug("map is empty");
			return false;
		}
		for(String key: getMap().keySet()) {
			if(key.length() > KEYWORD_MAX_LENGTH) {
				logger.debug("key is too long");
				return false;
			}
			if(getMap().get(key).length() > KEYWORD_MAX_LENGTH) {
				logger.debug("value is too long");
				return false;
			}
		}
		if(!calculateHash().equals(getHash())) {
			logger.debug("calculateHash()={}, hash={}", calculateHash(), hash);
			return false;
		}
		
		return true;
	}
	
	public void put(String key, String value) {
		if(key == null) {
			throw new RuntimeException("key cannot be null");
		}
		if(key.length() > KEYWORD_MAX_LENGTH) {
			throw new RuntimeException("key cannot be longer than " + KEYWORD_MAX_LENGTH + " characters");
		}
		if(value == null) {
			throw new RuntimeException("value cannot be null");
		}
		if(value.length() > KEYWORD_MAX_LENGTH) {
			throw new RuntimeException("value cannot be longer than " + KEYWORD_MAX_LENGTH + " characters");
		}
		getMap().put(key, value);
		
	}
	
	public void putAll(Map<String, String> map) {
		for(String key: map.keySet()) {
			put(key, map.get(key));
		}
	}
	
	public void putAll(Keywords keywords) {
		putAll(keywords.getMap());
	}
	
	public boolean isEmpty() {
		return getMap().isEmpty();
	}

	public String get(String key) {
		return getMap().get(key);
	}
	
	public Set<String> keySet() {
		return getMap().keySet();
	}

	@Override
	public String toString() {
		return getMap().toString();
	}
	
	public Keywords copy() {
		Keywords keywords = new Keywords();
		keywords.putAll(getMap());
		return keywords;
	}
	
	public String remove(String key) {
		return getMap().remove(key);
	}
	
}
