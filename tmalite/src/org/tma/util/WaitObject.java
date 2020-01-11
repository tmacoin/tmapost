/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

public class WaitObject<K> {
	
	private K object;
	private long timestamp;
	
	public WaitObject(K object) {
		this.object = object;
		timestamp = System.currentTimeMillis();
	}

	public K getObject() {
		return object;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof WaitObject<?>)) {
			return false;
		}
		WaitObject<K> waitObject = (WaitObject<K>)obj;
		if(object == null) {
			return waitObject.getObject() == null;
		}
		return object.equals(waitObject.getObject());
	}

}
