/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer.thin;

import java.util.List;

import org.tma.peer.Network;
import org.tma.peer.Peer;
import org.tma.peer.PeerLock;
import org.tma.peer.Request;
import org.tma.peer.Response;
import org.tma.util.Constants;
import org.tma.util.ShardUtil;
import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

public class GetBalanceRequest extends Request {

	private static final long serialVersionUID = 7852014295465690974L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final ResponseHolder responseHolder = ResponseHolder.getInstance();

	private transient Network clientNetwork;
	private transient PeerLock peerLock;
	private String tmaAddress;
	
	public GetBalanceRequest(Network clientNetwork, String tmaAddress) {
		this.clientNetwork = clientNetwork;
		this.tmaAddress = tmaAddress;
	}

	public Response getResponse(Network serverNetwork, Peer p) throws Exception {
		return new Response();
	}
	
	public void start() {
		int shardId = StringUtil.getShard(tmaAddress, clientNetwork.getBootstrapShardingPower());
		int nextShardId = ShardUtil.getNext(clientNetwork.getBootstrapBlockchainId(), shardId, clientNetwork.getBootstrapShardingPower());
		List<Peer> peers = clientNetwork.getPeersByShardId(nextShardId);
		for (Peer peer : peers) {
			if(!peer.isConnected()) {
				continue;
			}
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
	
}
