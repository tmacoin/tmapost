/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer.thin;

import java.io.Serializable;
import java.security.GeneralSecurityException;

import org.tma.blockchain.Keywords;
import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

public class Tweet implements Serializable {

	private static final long serialVersionUID = -8541311134915485210L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	
	private String transactionId;
	private String sender;
	private String recipient;
	private long timeStamp;
	private String text;
	private Keywords keywords;
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getSenderAddress() {
		try {
			return StringUtil.getStringFromKey(StringUtil.loadPublicKey(sender));
		} catch (GeneralSecurityException e) {
			logger.debug(e.getMessage(), e);
		}
		return null;
	}
	
	public Keywords getKeywords() {
		return keywords;
	}
	public void setKeywords(Keywords keywords) {
		this.keywords = keywords;
	}
	
	public String getFromTwitterAccount() {
		if(keywords == null) {
			return getSenderAddress();
		}
		String from = keywords.get("from");
		if(from == null) {
			return getSenderAddress();
		}
		return from;
	}


}
