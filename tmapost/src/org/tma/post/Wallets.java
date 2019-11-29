/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.tma.blockchain.Wallet;

public class Wallets {
	
	private static final Wallets instance = new Wallets();
	public static final String TMA = "tma";
	public static final String TWITTER = "twitt";
	
	private Map<String, Wallet> wallets = new HashMap<String, Wallet>();
	
	private Wallets() {

	}
	
	public static Wallets getInstance() {
		return instance;
	}

	public Wallet getWallet(String key) {
		return wallets.get(key);
	}
	
	public Wallet getWalletStartsWith(String startsWith) {
		Wallet wallet = null;
		for (String key : wallets.keySet()) {
			if (key.startsWith(startsWith)) {
				wallet = getWallet(key);
				break;
			}
		}
		return wallet;
	}
	
	public void putWallet(String key, Wallet wallet) {
		wallets.put(key, wallet);
	}
	
	public Collection<String> getKeys() {
		return wallets.keySet();
	}

}
