/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

public abstract class TmaRunnable implements Runnable {
	
	private static final TmaLogger logger = TmaLogger.getLogger();
	private String newName;
	
	public TmaRunnable(String newName) {
		this.newName = newName;
	}

	public void run() {
		Thread currentThread = Thread.currentThread();
		String oldName = currentThread.getName();
		try {
			currentThread.setName(newName);
			doRun();
		} catch(Throwable e) {
			logger.error(e.getMessage(), e);
		} finally {
			currentThread.setName(oldName);
		}
	}

	public abstract void doRun();

}
