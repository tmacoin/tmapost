/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

public class GetBootstrapPowerResponse extends Response {

	private static final long serialVersionUID = -5254489119832326152L;
	
	private int shardingPower;
	
	public GetBootstrapPowerResponse(int shardingPower) {
		this.shardingPower = shardingPower;
		setNotCounted(true);
	}

	public Request getRequest(Network clientNetwork, Peer peer) throws Exception {
		clientNetwork.setBootstrapShardingPower(shardingPower);
		clientNetwork.setBootstrapShardingPowerUpdated(true);
		Object lock = GetBootstrapPowerRequest.lock;
		synchronized (lock) {
			lock.notify();
		}
		return new EmptyRequest();
	}
}
