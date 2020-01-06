package org.tma.web.action;
/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.tma.blockchain.Wallet;

public class Wallets {
	
	private static final Wallets instance = new Wallets();
	public static final String TMA = "tma";
	public static final String TWITTER = "twitt";
	public static String WALLET_NAME;
	
	private Map<String, Map<String, Wallet>> wallets = new HashMap<String, Map<String, Wallet>>();
	
	private Wallets() {

	}
	
	public static Wallets getInstance() {
		return instance;
	}

	public Wallet getWallet(String application, String name) {
		return wallets.get(application).get(name);
	}
	
	public void putWallet(String application, String name, Wallet wallet) {
		Map<String, Wallet> applicationWallets = wallets.get(application);
		if(applicationWallets == null) {
			applicationWallets = new HashMap<String, Wallet>();
			wallets.put(application, applicationWallets);
		}
		applicationWallets.put(name, wallet);
	}
	
	public Collection<String> getApplications() {
		return wallets.keySet();
	}
	
	public Collection<String> getNames(String application) {
		Map<String, Wallet> applicationWallets = wallets.get(application);
		if(applicationWallets == null) {
			return new HashSet<String>();
		}
		return applicationWallets.keySet();
	}

}
