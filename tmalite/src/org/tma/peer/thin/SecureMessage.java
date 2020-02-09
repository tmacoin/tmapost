/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer.thin;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.tma.util.Base58;
import org.tma.util.Coin;
import org.tma.util.Encryptor;
import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

public class SecureMessage implements Serializable {

	private static final long serialVersionUID = 3594175706393924245L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Encryptor encryptor = new Encryptor();
	
	private PublicKey sender;
	private String text;
	private String recipient;
	private Coin value;
	private Coin fee;
	private long timeStamp;
	private long expire;
	private String transactionId;
	
	public SecureMessage() {
		
	}
	
	public SecureMessage(PublicKey sender, String text) {
		this.sender = sender;
		this.text = text;
	}

	public PublicKey getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		try {
			this.sender = StringUtil.loadPublicKey(sender);
		} catch (GeneralSecurityException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Coin getValue() {
		return value;
	}

	public void setValue(Coin value) {
		this.value = value;
	}

	public Coin getFee() {
		return fee;
	}

	public void setFee(Coin fee) {
		this.fee = fee;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}
	
	public String getSubject(PrivateKey privateKey) {
        try {
            String str = StringUtil.trimToNull(getText());
            if(str != null) {
                str = new String(encryptor.decryptAsymm(Base58.decode(str), privateKey), StandardCharsets.UTF_8);
                int index = str.indexOf("\n");
                index = index == -1? str.length(): index;
                return str.substring(0, index);
            }
        } catch (GeneralSecurityException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    public String getBody(PrivateKey privateKey) {
        try {
            String str = StringUtil.trimToNull(getText());
            if(str != null) {
                str = new String(encryptor.decryptAsymm(Base58.decode(str), privateKey), StandardCharsets.UTF_8);
                int index = str.indexOf("\n");
                index = index == -1? str.length(): index;
                return  str.substring(index + 1);
            }
        } catch (GeneralSecurityException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public String getSenderTmaAddress() {
		return StringUtil.getStringFromKey(getSender());
	}

	
	
}
