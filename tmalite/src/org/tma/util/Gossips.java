/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Gossips<K> {
	
	private Queue<WaitObject<K>> queue = new ConcurrentLinkedQueue<WaitObject<K>>();
	private long wait;
	private int maxSize;
	
	public synchronized boolean has(K object) {
		WaitObject<K> waitObject = new WaitObject<K>(object);
		boolean result = queue.contains(waitObject);
		clean();
		return result;
	}
	
	public synchronized void add(K object) {
		WaitObject<K> waitObject = new WaitObject<K>(object);
		queue.add(waitObject);
		clean();
	}
	
	public synchronized boolean contains(K object) {
		WaitObject<K> waitObject = new WaitObject<K>(object);
		boolean result = queue.contains(waitObject);
		if(!result) {
			queue.add(waitObject);
		}
		clean();
		return result;
	}

	public Gossips(long wait, int maxSize) {
		this.wait = wait;
		this.maxSize = maxSize;
	}
	
	private void clean() {
		while (true) {
			WaitObject<K> waitObject = queue.peek();
			if(
					queue.size() < maxSize &&
					(waitObject ==  null || waitObject.getTimestamp() > System.currentTimeMillis() - wait)
			) {
				return;
			}
			queue.poll();
		}
	}

}
