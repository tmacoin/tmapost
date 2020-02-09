/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ExpiringMap<K, V> {
	
	private Queue<WaitObject<K>> queue = new ConcurrentLinkedQueue<WaitObject<K>>();
	private Map<K, V> map =  new HashMap<K, V>(); 
	private long wait;
	private int maxSize;
	
	public ExpiringMap(long wait, int maxSize) {
		this.wait = wait;
		this.maxSize = maxSize;
	}
	
	public synchronized V get(K key) {
		clean();
		return map.get(key);
	}
	public synchronized V put(K key, V value) {
		clean();
		WaitObject<K> waitObject = new WaitObject<K>(key);
		queue.add(waitObject);
		return map.put(key, value);
	}
	
	private void clean() {
		while (true) {
			WaitObject<K> waitObject = queue.peek();
			if(
					map.size() < maxSize &&
					(waitObject ==  null || waitObject.getTimestamp() > System.currentTimeMillis() - wait)
			) {
				return;
			}
			waitObject = queue.poll();
			K key = waitObject.getObject();
			map.remove(key);
		}
	}

	public synchronized boolean containsKey(K key) {
		clean();
		return map.containsKey(key);
	}

	public synchronized Set<K> keySet() {
		clean();
		return map.keySet();
	}

	public Collection<V> values() {
		clean();
		return map.values();
	}
	
	public synchronized void clear() {
		queue.clear();
		map.clear();
	}

	public synchronized V remove(K key) {
		WaitObject<K> waitObject = new WaitObject<K>(key);
		queue.remove(waitObject);
		return map.remove(key);
	}

}
