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
import org.tma.peer.Request;
import org.tma.peer.Response;
import org.tma.util.TmaLogger;

public class GetFaucetRequest extends Request {

	private static final long serialVersionUID = 7852014295465690974L;
	private static final TmaLogger logger = TmaLogger.getLogger();

	private transient Network clientNetwork;
	private String fromAddress;
	private String toAddress;
	
	public GetFaucetRequest(Network clientNetwork, String fromAddress, String toAddress) {
		this.clientNetwork = clientNetwork;
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
	}

	public Response getResponse(Network serverNetwork, Peer peer) throws Exception {
		return new Response();
	}
	
	public void start() {
		List<Peer> peers = clientNetwork.getMyPeers();
		if(clientNetwork.getMyPeers().isEmpty()) {
			logger.debug("clientNetwork.getMyPeers() is empty");
		}
		for (Peer peer : peers) {
			peer.send(clientNetwork, this);
		}

	}

	public String getFromAddress() {
		return fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}
	
}
