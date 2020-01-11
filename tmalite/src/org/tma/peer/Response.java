/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

public class Response extends Message {

	private static final long serialVersionUID = -6919004875201569091L;

	private boolean next;
	private int correlationId;
	private boolean success;
	private String networkIdentifier;
	private boolean doDisconnect;
	private boolean notCounted;

	/**
	 * @param clientNetwork - network on the client side
	 * @param peer - peer that is processing request
	 * @throws Exception - generic exception that might be thrown by subclasses
	 */
	public Request getRequest(Network clientNetwork, Peer peer) throws Exception {
		return null;
	}

	public boolean hasNext() {
		return next;
	}

	public void setNext(boolean next) {
		this.next = next;
	}

	public int getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(int correlationId) {
		this.correlationId = correlationId;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getNetworkIdentifier() {
		return networkIdentifier;
	}

	public void setNetworkIdentifier(String networkIdentifier) {
		this.networkIdentifier = networkIdentifier;
	}

	public boolean isDoDisconnect() {
		return doDisconnect;
	}

	public void setDoDisconnect(boolean doDisconnect) {
		this.doDisconnect = doDisconnect;
	}

	public boolean isNotCounted() {
		return notCounted;
	}

	public void setNotCounted(boolean notCounted) {
		this.notCounted = notCounted;
	}
	
	public long getIndexInBlockchain() {
		return 0;
	}


}
