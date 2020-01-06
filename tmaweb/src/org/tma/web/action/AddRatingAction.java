package org.tma.web.action;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.blockchain.Keywords;
import org.tma.blockchain.Transaction;
import org.tma.blockchain.TransactionOutput;
import org.tma.blockchain.Wallet;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.SendTransactionRequest;
import org.tma.peer.thin.GetInputsRequest;
import org.tma.peer.thin.GetKeywordsRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.util.Applications;
import org.tma.util.Coin;
import org.tma.util.StringUtil;

import com.opensymphony.xwork2.ActionSupport;

public class AddRatingAction extends ActionSupport {

	private static final long serialVersionUID = 2135249924702466538L;
	private static final Logger logger = LogManager.getLogger();
	
	private String transactionId;
	private String name;
	private String comment;
	private String rate;

    public String execute() throws Exception {
    	if(StringUtil.isEmpty(comment)) {
    		addActionMessage("Comment input field cannot be empty");
    		return SUCCESS;
    	}
    	if(StringUtil.isEmpty(rate)) {
    		addActionMessage("Please select rate");
    		return SUCCESS;
    	}
    	addRating();
        return SUCCESS;
    }

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}
	
	private boolean addRating() {
		String ratee = StringUtil.getTmaAddressFromString(name);
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		
		GetKeywordsRequest getKeywordsRequest = new GetKeywordsRequest(network, ratee, transactionId);
		getKeywordsRequest.start();
		
		Keywords accountKeywords = (Keywords)ResponseHolder.getInstance().getObject(getKeywordsRequest.getCorrelationId());
		
		if(accountKeywords == null || accountKeywords.getMap().isEmpty()) {
			addActionMessage("Could not retrieve any keywords for " + name);
			return false;
		}
		
		
		String tmaAddress = network.getTmaAddress();
		Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA, Wallets.WALLET_NAME);
		Coin amount = Coin.SATOSHI.multiply(2);
		List<Coin> totals = new ArrayList<Coin>();
		totals.add(amount);
		for(String word: accountKeywords.getMap().keySet()) {
			if(word.equals(accountKeywords.getMap().get(word))) {
				totals.add(amount);
			}
		}
		List<Set<TransactionOutput>> inputList = new GetInputsRequest(network, tmaAddress, totals).getInputlist();
		int i = 0;
		
		if(inputList.size() != totals.size()) {
			addActionMessage("No inputs available for tma address " + tmaAddress + ". Please check your balance.");
			return false;
		}
		
		Keywords keywords = new Keywords();
		keywords.getMap().put("rater", wallet.getTmaAddress());
		keywords.getMap().put("ratee", name);
		keywords.getMap().put("transactionId", transactionId);
		keywords.getMap().put("rating", rate);
	
		Transaction transaction = new Transaction(wallet.getPublicKey(), ratee, Coin.SATOSHI, Coin.SATOSHI, 
				inputList.get(i++), wallet.getPrivateKey(), comment.trim(), null, keywords);
		transaction.setApp(Applications.RATING);
		new SendTransactionRequest(network, transaction).start();
		logger.debug("sent {}", transaction);
		
		Map<String, String> map = new HashMap<String, String>(keywords.getMap());
		map.remove("rater");
		for(String word: accountKeywords.getMap().keySet()) {
			if(word.equals(accountKeywords.getMap().get(word))) {
				Keywords words = new Keywords();
				words.getMap().putAll(map);
				transaction = new Transaction(wallet.getPublicKey(), StringUtil.getTmaAddressFromString(word), Coin.SATOSHI, Coin.SATOSHI, 
						inputList.get(i++), wallet.getPrivateKey(), comment.trim(), null, words);
				transaction.setApp(Applications.RATING);
				new SendTransactionRequest(network, transaction).start();
				logger.debug("sent {}", transaction);
			}
		}
		return true;
	}



}
