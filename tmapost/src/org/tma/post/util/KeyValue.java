/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.util;

public class KeyValue {
	private String key;
	private String value;

	public KeyValue(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

	public String toString() {
		return key;
	}

	public boolean equals(Object obj) {
		if (obj instanceof KeyValue) {
			KeyValue kv = (KeyValue) obj;
			return (kv.value.equals(this.value));
		}
		return false;
	}

	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
		return hash;
	}
}
