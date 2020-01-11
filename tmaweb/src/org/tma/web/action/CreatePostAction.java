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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tma.blockchain.Keywords;
import org.tma.blockchain.Transaction;
import org.tma.blockchain.TransactionOutput;
import org.tma.blockchain.Wallet;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.SendTransactionRequest;
import org.tma.peer.thin.GetInputsRequest;
import org.tma.util.Applications;
import org.tma.util.Coin;
import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

import com.opensymphony.xwork2.ActionSupport;

public class CreatePostAction extends ActionSupport {

	private static final long serialVersionUID = 2135249924702466538L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final int MAX_NUMBER_OF_KEYWORDS = 10;
	private String post;
	private String description;
	private String keywords;
	

    public String execute() throws Exception {
    	if(StringUtil.isEmpty(post)) {
    		addActionMessage("Post input field cannot be empty");
    		return SUCCESS;
    	}
    	if(StringUtil.isEmpty(description)) {
    		addActionMessage("Description input field cannot be empty");
    		return SUCCESS;
    	}
    	if(StringUtil.isEmpty(post)) {
    		addActionMessage("keywords input field cannot be empty");
    		return SUCCESS;
    	}
    	
    	if(sendCreateRateeTransaction() != null) {
    		addActionMessage("Post " + post + " was created successfully with keywords: " + getKeywords(keywords));
		}

        return SUCCESS;
    }


	public String getPost() {
		return post;
	}


	public void setPost(String post) {
		this.post = post;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getKeywords() {
		return keywords;
	}


	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	private Transaction sendCreateRateeTransaction() {
		Set<String> words = getKeywords(keywords);
		String accountName = post.trim();
		String ratee = StringUtil.getTmaAddressFromString(accountName);
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		String tmaAddress = network.getTmaAddress();
		Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA, Wallets.WALLET_NAME);
		Coin amount = Coin.SATOSHI.multiply(2);
		List<Coin> totals = new ArrayList<Coin>();
		totals.add(amount);
		for(@SuppressWarnings("unused") String word: words) {
			totals.add(amount);
		}
		List<Set<TransactionOutput>> inputList = new GetInputsRequest(network, tmaAddress, totals).getInputlist();
		int i = 0;
		
		if(inputList.size() != totals.size()) {
			addActionMessage("No inputs available for tma address " + tmaAddress + ". Please check your balance.");
			return null;
		}
		
		Set<TransactionOutput> inputs = inputList.get(i++);
		Keywords keywords = new Keywords();
		keywords.getMap().put("create", accountName);
		keywords.getMap().put("first", accountName);
		for(String word: words) {
			keywords.getMap().put(word, word);
		}
		
		Transaction transaction = new Transaction(wallet.getPublicKey(), ratee, Coin.SATOSHI, Coin.SATOSHI, 
				inputs, wallet.getPrivateKey(), description, null, keywords);
		transaction.setApp(Applications.RATING);
		new SendTransactionRequest(network, transaction).start();
		logger.debug("sent {}", transaction);
		
		Map<String, String> map = keywords.getMap();
		
		for(String word: words) {
			keywords = new Keywords();
			keywords.getMap().putAll(map);
			keywords.getMap().put("transactionId", transaction.getTransactionId());
			keywords.getMap().put("first", word);
			inputs = inputList.get(i++);
			String recipient = StringUtil.getTmaAddressFromString(word);
			Transaction keyWordTransaction = new Transaction(wallet.getPublicKey(), recipient, Coin.SATOSHI, Coin.SATOSHI, 
					inputs, wallet.getPrivateKey(), description, null, keywords);
			keyWordTransaction.setApp(Applications.RATING);
			new SendTransactionRequest(network, keyWordTransaction).start();
			logger.debug("sent {}", keyWordTransaction);
			
		}
		
		return transaction;
	}
	
	public Set<String> getKeywords(String keywords) {
		Set<String> set = new HashSet<String>();
		String[] strings = keywords.split(" ");
		for(String str: strings) {
			if(set.size() > MAX_NUMBER_OF_KEYWORDS) {
				break;
			}
			if(!"".equals(str)) {
				set.add(str.toLowerCase());
			}
		}
		return set;
	}


}
