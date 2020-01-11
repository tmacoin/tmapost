/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer.thin;

import java.util.Map;
import java.util.WeakHashMap;

public class ResponseHolder {
	
	private static final ResponseHolder instance =  new ResponseHolder();
	
	private final Map<Integer, Object> map = new WeakHashMap<Integer, Object>();

	public Object getObject(int correlationId) {
		return map.get(correlationId);
	}

	public void setObject(int correlationId, Object balance) {
		map.put(correlationId, balance);
	}

	public static ResponseHolder getInstance() {
		return instance;
	}

}
