/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import java.util.HashMap;
import java.util.Map;

public abstract class Request extends Message {

	private static final long serialVersionUID = -2868505773945001715L;
	private static Map<Integer, Response> firstResponses = new HashMap<Integer, Response>();
	
	private String networkIdentifier;
	private int correlationId = System.identityHashCode(this);
	
	public abstract Response getResponse(Network serverNetwork, Peer peer) throws Exception;

	public String getNetworkIdentifier() {
		return networkIdentifier;
	}

	public void setNetworkIdentifier(String networkIdentifier) {
		this.networkIdentifier = networkIdentifier;
	}
	
	public int getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(int correlationId) {
		this.correlationId = correlationId;
	}

	private Map<Integer, Response> getFirstResponses() {
		return firstResponses;
	}
	
	public Response getFirstResponse() {
		return getFirstResponses().get(getCorrelationId());
	}
	
	public Response putFirstResponse(Response response) {
		return getFirstResponses().put(getCorrelationId(), response);
	}
	
	public Response removeFirstResponse() {
		return getFirstResponses().remove(getCorrelationId());
	}

	/**
	 * @param peer - peer instance that is processing this requets 
	 */
	public void onSendComplete(Peer peer) {
		
	}

	public long getIndexInBlockchain() {
		return 0;
	}

	

}
