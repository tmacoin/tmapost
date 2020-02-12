/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import java.io.Serializable;

import org.tma.util.StringUtil;

public abstract class Message implements Serializable {

	private static final long serialVersionUID = 3798515363997911833L;
	public static final String VERSION = "0.0.19";
	public static final long VALID_PERIOD = 1000 * 60 * 10;
	
	private String version = VERSION;
	private int blockchainId = retrieveBlockchainId();
	private long timestamp;
	private boolean thin = true;
	
	public String getVersion() {
		return version;
	}
	
	protected int retrieveBlockchainId() {
		Network network = Network.getInstance();
		if(network != null) {
			thin = true;
			return network.getBootstrapBlockchainId();
		}
		return 0;
	}
	
	public int getBlockchainId() {
		return blockchainId;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean isValid() {
		if(StringUtil.compareVersions(getVersion(), VERSION) < 0) {
			return false;
		}
		
		if(getTimestamp() > System.currentTimeMillis() + VALID_PERIOD ) {
			return false;
		}
		
		if(getTimestamp() < System.currentTimeMillis() - VALID_PERIOD ) {
			return false;
		}
		
		return true;
	}
	
	public boolean isDoDisconnect() {
		return false;
	}

	public boolean isThin() {
		return thin;
	}

}
