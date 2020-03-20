/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.blockchain;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.tma.peer.Network;
import org.tma.util.Base58;
import org.tma.util.Coin;
import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

public class Transaction implements Serializable {

	private static final long serialVersionUID = -9101180840822172358L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	public static final int MAX_NUMBER_OF_INPUTS = 1000;
	public static final int INPUTS_VALIDATION_INDEX = 189057;
	public static Coin MINIMUM_TRANSACTION = Coin.SATOSHI;
	private static int DATA_LENGTH = 1024; 
	private static int APP_CODE_LIMIT = 5;
	
	private String transactionId;
	private PublicKey sender;
	private String recipient;
	private Coin value;
	private Coin fee;
	private String signature;
	private Set<TransactionOutput> inputs = new LinkedHashSet<TransactionOutput>();
	private Set<TransactionOutput> outputs = new LinkedHashSet<TransactionOutput>();
	private String blockHash;
	private String data;
	private TransactionData expiringData;
	private String app;
	private Keywords keywords;
	
	public Transaction() {
		
	}
	
	public Transaction(PublicKey from, String to, Coin value,  Coin fee, Set<TransactionOutput> inputs, 
			PrivateKey privateKey, String data, TransactionData expiringData, Keywords keywords) {
		this.sender = from;
		if(!StringUtil.isTmaAddressValid(to)) {
			throw new RuntimeException("Recipient tma address is not valid");
		}
		if(!isSenderShardEqualsBlockchainId()) {
			throw new RuntimeException("Cannot create transaction for sender for shardId different from current blockchain");
		}
		if(expiringData != null && !expiringData.isValid()) {
			throw new RuntimeException("Expiring Data is not valid");
		}
		this.recipient = to;
		this.value = value;
		this.fee = fee;
		this.inputs = inputs;
		setData(data);
		setExpiringData(expiringData);
		setKeywords(keywords);
		signTransaction(privateKey);
		setTransactionId(calculateHash());
	}
	
	public Coin getInputsValue() {
		Coin total = Coin.ZERO;
		for(TransactionOutput i : inputs) {
			total = total.add(i.getValue());
		}
		return total;
	}
	
	public boolean verifySignature() {
		String preHash = getPreHash();
		boolean result = StringUtil.verifyECDSASig(sender, preHash, StringUtil.fromHexString(signature));
		if(result) {
			return true;
		}
		return false;
	}
	
	public Coin getOutputsValue() {
		Coin total = Coin.ZERO;
		for(TransactionOutput o : outputs) {
			total = total.add(o.getValue());
		}
		return total;
	}
	
	private String calculateHash() {
		return StringUtil.applySha256(getPreHash() + signature);
	}
	
	private String getPreHash() {
		return StringUtil.getStringFromKey(sender) + recipient + value + fee + getInputsMerkleRoot() + (data == null? "": data) 
				+ (expiringData == null? "": expiringData.getHash()) + (keywords == null? "": keywords.getHash());
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		for (TransactionOutput input : inputs) {
			input.setInputTransactionId(transactionId);
		}
		if(expiringData != null) {
			expiringData.setTransactionId(transactionId);
		}
		if(keywords != null) {
			keywords.setTransactionId(transactionId);
		}
	}

	public Set<TransactionOutput> getOutputs() {
		return outputs;
	}

	public PublicKey getSender() {
		return sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public Coin getValue() {
		return value;
	}

	public Set<TransactionOutput> getInputs() {
		return inputs;
	}

	private void setSignature(byte[] signature) {
		this.signature = StringUtil.toHexString(signature);
	}
	
	public String getInputsMerkleRoot() {
		if(inputs.isEmpty()) {
			return "";
		}
		List<String> strings =  new ArrayList<String>();
		for(TransactionOutput input: inputs) {
			strings.add(input.getTransactionOutputId());
		}
		return StringUtil.getMerkleTreeRoot(strings);
	}
	
	private void signTransaction(PrivateKey privateKey) {
		String preHash = getPreHash();
		byte[] signature = StringUtil.applyECDSASig(privateKey, preHash);
		setSignature(signature);
	}
	
	public boolean isRecipientValid() {
		return StringUtil.isTmaAddressValid(getRecipient());
	}
	
	public boolean isTransactionValid() throws CreateTransactionException {
		if(getKeywords() != null && !getKeywords().isValid()) {
			throw new CreateTransactionException("Keywords are not valid");
		}
		if (!verifySignature()) {
			throw new CreateTransactionException("Transaction signature is not valid");
		}
		if (!isRecipientValid()) {
			throw new CreateTransactionException("Transaction recipient is not valid");
		}
		
		if(getExpiringData() != null && !getExpiringData().isValid()) {
			return false;
		}

		return true;
	}

	public boolean canBeAddedTo(List<Transaction> transactions) {
		if(getInputs().isEmpty() || transactions==null || transactions.isEmpty()) {
			return true;
		}
		for(Transaction transaction: transactions) {
			for(TransactionOutput input: transaction.getInputs()) {
				for(TransactionOutput i: getInputs()) {
					if(i.getTransactionOutputId().equals(input.getTransactionOutputId())) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean inputsEqualToOutputs() {
		if (!getInputsValue().equals(getOutputsValue().add(getFee()))) {
			logger.debug("returned false this {}", this);
			logger.debug("inputsEqualToOutputs returned false");
			return false;
		}
		return true;
	}
	
	public Coin getChangeAmount() {
		return getOutputsValue().subtract(getValue());
	}
	
	public Coin getFee() {
		return fee;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
		for(TransactionOutput output: getOutputs()) {
			output.setOutputBlockHash(blockHash);
		}
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public void setValue(Coin value) {
		this.value = value;
	}

	public void setFee(Coin fee) {
		this.fee = fee;
	}

	public String getSignature() {
		return signature;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getSenderAsString() {
		return Base58.encode(sender.getEncoded());
	}

	public void setSender(String sender) {
		try {
			this.sender = StringUtil.loadPublicKey(sender);
		} catch (GeneralSecurityException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public String getSenderAddress() {
		return StringUtil.getStringFromKey(sender);
	}
	
	public int getSenderShardId() {
		int shardingPower = Network.getInstance().getShardingPower();
		int senderShardId = StringUtil.getShard(getSenderAddress(), shardingPower);
		return senderShardId;
	}
	
	public int getRecipientShardId() {
		int shardingPower = Network.getInstance().getShardingPower();
		int recipientShardId = StringUtil.getShard(getRecipient(), shardingPower);
		return recipientShardId;
	}
	
	public boolean isSenderShardEqualsBlockchainId() {
		return Network.getInstance().getBlockchainId() == getSenderShardId();
	}
	
	public String toString() {
		return "transaction from " + getSenderAddress() + " to " + getRecipient() + ", amount " + getValue().toNumberOfCoins() + 
				", getInputsValue()=" + getInputsValue() + ", #inputs=" + getInputs().size() + ", getFee()=" + getFee() + (data == null? "": ", data=" + data)
				 + (expiringData == null? "": ", expiringData=" + expiringData) + (app == null? "": ", app=" + app)
				 + (keywords == null? "": ", keywords=" + keywords);
	}
	
	public String getData() {
		return StringUtils.substring(data, 0, DATA_LENGTH);
	}

	public void setData(String data) {
		this.data = StringUtils.substring(data, 0, DATA_LENGTH);
	}
	
	public boolean isCoinbase() {
		return inputs.isEmpty() && getSenderAddress().equals(getRecipient());
	}

	public int hashCode() {
		return transactionId.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Transaction)) {
			return false;
		}
		Transaction other = (Transaction) obj;
		return transactionId.equals(other.transactionId);
	}

	public TransactionData getExpiringData() {
		return expiringData;
	}

	public void setExpiringData(TransactionData expiringData) {
		this.expiringData = expiringData;
	}

	public String getApp() {
		return StringUtil.truncate(app, APP_CODE_LIMIT);
	}

	public void setApp(String app) {
		this.app = StringUtil.truncate(app, APP_CODE_LIMIT);
	}

	public Keywords getKeywords() {
		return keywords;
	}

	public void setKeywords(Keywords keywords) {
		if(keywords != null && keywords.isEmpty()) {
			return;
		}
		if(keywords != null && !keywords.isValid()) {
			throw new RuntimeException("Keywords are not valid");
		}
		this.keywords = keywords;
	}
	
}
