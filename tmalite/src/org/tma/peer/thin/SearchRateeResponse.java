/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer.thin;

import org.tma.peer.EmptyRequest;
import org.tma.peer.Network;
import org.tma.peer.Peer;
import org.tma.peer.Request;
import org.tma.peer.Response;

public class SearchRateeResponse extends Response {

	private static final long serialVersionUID = -5254489119832326152L;
	
	private Ratee ratee;

	public SearchRateeResponse(Ratee ratee) {
		this.ratee = ratee;
	}

	public Ratee getRatee() {
		return ratee;
	}

	public Request getRequest(Network clientNetwork, Peer peer) throws Exception {
		ResponseHolder.getInstance().setObject(getCorrelationId(), ratee);
		return new EmptyRequest();
	}



}
