/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import java.util.Iterator;
import java.util.List;

public class BootstrapResponse extends Response {

	private static final long serialVersionUID = 9123534275344544318L;
	private List<Peer> peers;
	
	public BootstrapResponse(List<Peer> peers) {
		this.peers = peers;
		setNotCounted(true);
	}
	
	public Request getRequest(Network clientNetwork, Peer peer) {
		
		Iterator<Peer> i = peers.iterator();
		 
		while(i.hasNext()) {
			Peer p = i.next();
		    if (p.getiNetAddress().getAddress() == null) {
		        i.remove();
		    }
		}

		ConnectRequest request = new ConnectRequest(clientNetwork, peers);
		request.start();
		return request;
	}

}
