/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.blockchain;

import java.io.Serializable;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.tma.util.Base58;
import org.tma.util.Coin;
import org.tma.util.StringUtil;

public class TransactionOutput implements Serializable {

	private static final long serialVersionUID = 1602011506192721201L;
	private static final SecureRandom random = new SecureRandom();
	private String transactionOutputId;
	private String recipient;
	private Coin value;
	private String inputTransactionId;
	private String outputTransactionId; //the id of the transaction this output was created in
	private String outputBlockHash;
	
	public TransactionOutput() {
		
	}
	
	public TransactionOutput(String recipient, Coin value, String outputTransactionId) {
		this.recipient = recipient;
		this.value = value;
		this.outputTransactionId = outputTransactionId;
		this.transactionOutputId = StringUtil.applySha256(recipient + value.toString() + this.outputTransactionId + Base58.encode(random.generateSeed(16)));
	}
	
	public boolean isMine(PublicKey publicKey) {
		return StringUtil.getStringFromKey(publicKey).equals(recipient);
	}

	public String getTransactionOutputId() {
		return transactionOutputId;
	}

	public Coin getValue() {
		return value;
	}

	public String getRecipient() {
		return recipient;
	}

	public String getOutputTransactionId() {
		return outputTransactionId;
	}

	public void setValue(Coin value) {
		this.value = value;
	}

	public boolean equals(Object object) {
		if(object == null || ! (object instanceof TransactionOutput)) {
			return false;
		}
		TransactionOutput output = (TransactionOutput)object;
		return getTransactionOutputId().equals(output.getTransactionOutputId());
	}

	public int hashCode() {
		return getTransactionOutputId().hashCode();
	}

	public String getInputTransactionId() {
		return inputTransactionId;
	}

	public void setInputTransactionId(String inputTransactionId) {
		this.inputTransactionId = inputTransactionId;
	}
	
	public boolean isRecipientValid() {
		return StringUtil.isTmaAddressValid(getRecipient());
	}

	public void setTransactionOutputId(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	public String toString() {
		return "TransactionOutput: {transactionOutputId: " + transactionOutputId + ", recipient: " + recipient + ", value: " + value + ", inputTransactionId: " + inputTransactionId + ", outputTransactionId: " + outputTransactionId + "}";
	}

	public String getOutputBlockHash() {
		return outputBlockHash;
	}

	public void setOutputBlockHash(String outputBlockHash) {
		this.outputBlockHash = outputBlockHash;
	}

	public void setOutputTransactionId(String outputTransactionId) {
		this.outputTransactionId = outputTransactionId;
	}
	
}
