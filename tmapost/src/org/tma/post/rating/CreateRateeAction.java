/*******************************************************************************
 * Copyright � 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.rating;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
import org.tma.peer.thin.ResponseHolder;
import org.tma.post.Caller;
import org.tma.post.Wallets;
import org.tma.post.util.SwingUtil;
import org.tma.util.Applications;
import org.tma.util.Coin;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class CreateRateeAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 6886690569988480986L;
	private static final Logger logger = LogManager.getLogger();
	
    private JFrame frame;
    private JTextField account;
    private JTextArea description;
    private JTextField jkeywords;
	
	public CreateRateeAction(JFrame frame, JTextField account, JTextArea description, JTextField jkeywords) {
		putValue(NAME, "Create Ratee");
		putValue(SHORT_DESCRIPTION, "Create Ratee");
		this.frame = frame;
		this.account = account;
		this.description = description;
		this.jkeywords = jkeywords;
		
	}

	public void actionPerformed(ActionEvent e) {
		
		if(StringUtil.isEmpty(account.getText())) {
			log("Ratee can not be blank");
			return;
		}
		if(StringUtil.isEmpty(description.getText())) {
			log("Description can not be blank");
			return;
		}
		if(StringUtil.isEmpty(jkeywords.getText())) {
			log("Keywords can not be blank");
			return;
		}
		
		JLabel label = SwingUtil.showWait(frame);
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("CreateRateeAction") {
			public void doRun() {
				sendCreateRateeTransaction();
				
				label.setText("Ratee " + account.getText() + " was created successfully with keywords: " + getKeywords());
			}
		});

		


	}
	
	
	private Transaction sendCreateRateeTransaction() {
		Set<String> words = getKeywords();
		String accountName = account.getText().trim();
		String ratee = StringUtil.getTmaAddressFromString(accountName);
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		String tmaAddress = network.getTmaAddress();
		Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA);
		Coin amount = Coin.SATOSHI.multiply(2);
		List<Coin> totals = new ArrayList<Coin>();
		totals.add(amount);
		for(@SuppressWarnings("unused") String word: words) {
			totals.add(amount);
		}
		GetInputsRequest request = new GetInputsRequest(Network.getInstance(), tmaAddress, totals);
		request.start();
		@SuppressWarnings("unchecked")
		List<Set<TransactionOutput>> inputList = (List<Set<TransactionOutput>>)ResponseHolder.getInstance().getObject(request.getCorrelationId());
		int i = 0;
		Set<TransactionOutput> inputs = inputList.get(i++);
		Keywords keywords = new Keywords();
		keywords.getMap().put("create", accountName);
		keywords.getMap().put("first", accountName);
		for(String word: words) {
			keywords.getMap().put(word, word);
		}
		
		Transaction transaction = new Transaction(wallet.getPublicKey(), ratee, Coin.SATOSHI, Coin.SATOSHI, 
				inputs, wallet.getPrivateKey(), description.getText(), null, keywords);
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
					inputs, wallet.getPrivateKey(), description.getText(), null, keywords);
			keyWordTransaction.setApp(Applications.RATING);
			new SendTransactionRequest(network, keyWordTransaction).start();
			logger.debug("sent {}", keyWordTransaction);
			
		}
		
		return transaction;
	}
	
	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}
	
	private Set<String> getKeywords() {
		Set<String> set = new HashSet<String>();
		String[] strings = jkeywords.getText().split(" ");
		for(String str: strings) {
			if(!"".equals(str)) {
				set.add(str);
			}
		}
		return set;
	}


}