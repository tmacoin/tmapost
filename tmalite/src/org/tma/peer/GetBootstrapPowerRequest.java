/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import org.tma.util.Bootstrap;
import org.tma.util.Configurator;
import org.tma.util.Constants;
import org.tma.util.TmaLogger;

public class GetBootstrapPowerRequest extends Request {

	private static final long serialVersionUID = 7852014295465690974L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Bootstrap bootstrap = new Bootstrap();
	public static final Object lock = new Object();

	private transient Network clientNetwork;
	
	public GetBootstrapPowerRequest(Network clientNetwork) {
		this.clientNetwork = clientNetwork;
	}

	public Response getResponse(Network serverNetwork, Peer peer) throws Exception {
		return new Response();
	}
	
	public void start() {
		while (true) {
			bootstrap.addPeers(clientNetwork);
			try {
				synchronized (lock) {
					for (Peer peer : clientNetwork.getAllPeers()) {
						GetBootstrapPowerRequest request = new GetBootstrapPowerRequest(clientNetwork);
						peer.send(clientNetwork, request);
					}
					lock.wait(Constants.ONE_MINUTE);
				}
				if (clientNetwork.isBootstrapShardingPowerUpdated()) {
					return;
				}
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			if (Configurator.getInstance().getBooleanProperty("org.tma.network.bootstrap.nowait")) {
				break;
			}
		}
	}
	
}
