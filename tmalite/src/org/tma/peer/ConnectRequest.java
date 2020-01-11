/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import java.util.List;

import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

public class ConnectRequest extends Request {

	private static final long serialVersionUID = -5876937513336029881L;
	private static final TmaLogger logger = TmaLogger.getLogger();

	private transient Network clientNetwork;
	private transient List<Peer> toPeers;
	private transient PeerLock peerLock;

	public ConnectRequest(Network clientNetwork, List<Peer> toPeers) {
		this.clientNetwork = clientNetwork;
		this.toPeers = toPeers;
	}

	public ConnectRequest() {
		
	}

	public Response getResponse(Network serverNetwork, Peer peer) {
		Response response = new Response();
		response.setCorrelationId(getCorrelationId());
		return new Response();
	}
	
	public void start() {
		ThreadExecutor.getInstance().execute(new TmaRunnable("ConnectRequest") {
			public void doRun() {
				for(Peer peer: toPeers) {
					if(peer.getiNetAddress() == null) {
						continue;
					}
					if(!clientNetwork.add(peer)) {
						return;
					}
					if(sendToOnePeer(peer)) {
						return;
					}
				}
			}
		});
	}
	
	private boolean sendToOnePeer(Peer peer) {
		try {
			peerLock = new PeerLock(peer);
			synchronized (peerLock) {
				ConnectRequest request = new ConnectRequest();
				request.peerLock = peerLock;
				peer.send(clientNetwork, request);
				peerLock.wait(Peer.CONNECT_TIMEOUT);
			}
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return clientNetwork.isPeerSetFull();
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
