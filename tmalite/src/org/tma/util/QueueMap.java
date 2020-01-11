/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.util.LinkedHashMap;

public class QueueMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 499472338264655224L;
	private int limit;
	
	public QueueMap(int limit) {
		this.limit = limit;
	}

	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> arg0) {
		return size() > limit;
	}

}
