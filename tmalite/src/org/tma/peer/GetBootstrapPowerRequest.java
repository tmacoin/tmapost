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
import org.tma.util.TmaLogger;

public class GetBootstrapPowerRequest extends Request {

	private static final long serialVersionUID = 7852014295465690974L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Bootstrap bootstrap = new Bootstrap();

	private transient Network clientNetwork;
	private transient PeerLock peerLock;
	
	public GetBootstrapPowerRequest(Network clientNetwork) {
		this.clientNetwork = clientNetwork;
	}

	public Response getResponse(Network serverNetwork, Peer peer) throws Exception {
		if(!serverNetwork.isNetworkStarted()) {
			return new Response();
		}
		return new GetBootstrapPowerResponse(serverNetwork.getShardingPower());
	}
	
	public void start() {
		while (true) {
			bootstrap.addPeers(clientNetwork);
			for (Peer peer : clientNetwork.getAllPeers()) {
				try {
					peerLock = new PeerLock(peer);
					synchronized (peerLock) {
						peer.send(clientNetwork, this);
						peerLock.wait(Peer.CONNECT_TIMEOUT);
					}
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
				if(clientNetwork.isBootstrapShardingPowerUpdated()) {
					return;
				}
			}
			if(Configurator.getInstance().getBooleanProperty("org.tma.network.bootstrap.nowait")) {
				break;
			}
			
		}

	}
	
	public void onSendComplete(Peer peer) {
		PeerLock peerLock = this.peerLock;
		if(!peerLock.isForPeer(peer)) {
			return;
		}
		synchronized (peerLock) {
			peerLock.notify();
		}
	}
	
}
