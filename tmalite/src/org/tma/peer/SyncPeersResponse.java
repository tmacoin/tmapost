/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SyncPeersResponse extends Response {
	
	private static final long serialVersionUID = -7127931305734201702L;
	
	private List<Peer> toPeers;
	private int port;

	public SyncPeersResponse(Set<Peer> toPeers, int port) {
		this.toPeers = new ArrayList<Peer>(toPeers);
		this.port = port;
		setNotCounted(true);
	}

	public Request getRequest(Network clientNetwork, Peer peer) {
		Socket socket = peer.getRawSocket();
		if(port != 0 && socket != null) {
			InetSocketAddress iNetAddress  = new InetSocketAddress(socket.getInetAddress(), port);
			Peer responder = Peer.getInstance(iNetAddress);
			clientNetwork.add(responder);
		}
		ConnectRequest request = new ConnectRequest(clientNetwork, toPeers);
		request.start();
		return request;
	}

	

}
