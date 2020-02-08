/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer.thin;

import org.tma.peer.Network;
import org.tma.peer.Peer;
import org.tma.peer.Request;
import org.tma.peer.Response;
import org.tma.util.Constants;
import org.tma.util.ExpiringMap;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

public class SubscribeToMessagesRequest extends Request {

	private static final long serialVersionUID = 7852014295465690974L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final ExpiringMap<Peer, Peer> subscribedPeers = new ExpiringMap<>(Constants.TIMEOUT * 2, Constants.MAX_SIZE);

	private transient Network clientNetwork;
	private String tmaAddress;
	
	public SubscribeToMessagesRequest(Network clientNetwork, String tmaAddress) {
		this.clientNetwork = clientNetwork;
		this.tmaAddress = tmaAddress;
	}

	public Response getResponse(Network serverNetwork, Peer peer) throws Exception {
		return new Response();
	}
	
	public void start() {
		ThreadExecutor.getInstance().execute(new TmaRunnable("SubscribeToMessagesRequest") {
			public void doRun() {
				for (Peer peer : clientNetwork.getMyPeers()) {
					doPerPeer(peer);
				}
			}
		});
	}
	
	private void doPerPeer(Peer peer) {
		if(subscribedPeers.containsKey(peer)) {
			return;
		}
		logger.debug("Sending subscription request to {}", peer);
		subscribedPeers.put(peer, peer);
		peer.send(clientNetwork, this);
	}

	public String getTmaAddress() {
		
		return tmaAddress;
	}
	
}
