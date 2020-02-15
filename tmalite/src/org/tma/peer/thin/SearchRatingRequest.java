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
import org.tma.peer.PeerLock;
import org.tma.peer.Request;
import org.tma.peer.Response;
import org.tma.util.Constants;
import org.tma.util.TmaLogger;

public class SearchRatingRequest extends Request {

	private static final long serialVersionUID = 7852014295465690974L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final ResponseHolder responseHolder = ResponseHolder.getInstance();

	private transient Network clientNetwork;
	private transient PeerLock peerLock;
	private String account;
	private String transactionId;
	
	public SearchRatingRequest(Network clientNetwork, String account, String transactionId) {
		this.clientNetwork = clientNetwork;
		this.account = account;
		this.transactionId = transactionId;

	}

	public Response getResponse(Network serverNetwork, Peer p) throws Exception {
		return new Response();
	}
	
	public void start() {
		for (Peer peer : clientNetwork.getMyPeers()) {
			peerLock = new PeerLock(peer);
			synchronized (peerLock) {
				peer.send(clientNetwork, this);
				try {
					peerLock.wait(Constants.ONE_MINUTE);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (responseHolder.getObject(getCorrelationId()) != null) {
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

	public String getTransactionId() {
		return transactionId;
	}

	public String getAccount() {
		return account;
	}
	
}
