/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import org.tma.blockchain.Transaction;
import org.tma.util.Listeners;

public class SendTransactionRequest extends Request {
	
	private static final long serialVersionUID = -6690749759584489804L;
	//private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Listeners listeners = Listeners.getInstance();
	
	private transient Network clientNetwork;
	private Transaction transaction;
	
	public SendTransactionRequest(Network clientNetwork, Transaction transaction) {
		this.clientNetwork = clientNetwork;
		this.transaction = transaction;
		setNetworkIdentifier(clientNetwork.getNetworkIdentifier());
	}
	
	public void start() {
		if(transaction == null) {
			return;
		}
		//logger.debug("START {}", transaction);
		for(Peer peer: clientNetwork.getCommonPeers(transaction.getSenderShardId())) {
			String myNetworkIdentifier = getNetworkIdentifier();
			String peerNetworkIdentifier = peer.getNetworkIdentifier();
			if(myNetworkIdentifier.equals(peerNetworkIdentifier)) {
				continue;
			}
			peer.send(clientNetwork, this);
		}
		if(clientNetwork.getTmaAddress().equals(transaction.getSenderAddress()) || clientNetwork.getTmaAddress().equals(transaction.getRecipient())) {
			listeners.sendEvent(new SendTransactionEvent(transaction));
		}
	}

	public Response getResponse(Network serverNetwork, Peer peer) throws Exception {
		return new Response();
	}


}
