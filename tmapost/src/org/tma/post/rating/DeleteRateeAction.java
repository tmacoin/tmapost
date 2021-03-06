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
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

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
import org.tma.post.Caller;
import org.tma.post.Wallets;
import org.tma.post.util.SwingUtil;
import org.tma.util.Applications;
import org.tma.util.Coin;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

public class DeleteRateeAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = -6540143195727094951L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	
	private JFrame frame;
	private String transactionId;
	private String rateeTmaAddress;
	
	public DeleteRateeAction(JFrame frame, String transactionId, String rateeTmaAddress) {
		putValue(NAME, "Delete Post");
		putValue(SHORT_DESCRIPTION, "Delete Post");
		this.frame = frame;
		this.transactionId = transactionId;
		this.rateeTmaAddress = rateeTmaAddress;
	}

	public void actionPerformed(ActionEvent e) {
		int input = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to delete Post? This action cannot be undone!", "Confirm Post Deletion", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

		if(input != JOptionPane.OK_OPTION) {
			return;
		}
		
		final JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("AddRatingAction") {
			public void doRun() {
				doIt(label);
			}
		});
	}

	private void doIt(JLabel label) {
		logger.debug("transactionId={}", transactionId);
		
		sendDeleteRateeTransaction(label);
		
		
	}

	private void sendDeleteRateeTransaction(JLabel label) {
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			BootstrapRequest.getInstance().start();
		}
		
		GetKeywordsRequest getKeywordsRequest = new GetKeywordsRequest(network, rateeTmaAddress, transactionId);
		getKeywordsRequest.start();
		
		Keywords accountKeywords = (Keywords)ResponseHolder.getInstance().getObject(getKeywordsRequest.getCorrelationId());
		
		if(accountKeywords == null || accountKeywords.isEmpty()) {
			label.setText("Could not retrieve any keywords for identifier " + transactionId);
			return;
		}
		
		String tmaAddress = network.getTmaAddress();
		Wallets wallets = Wallets.getInstance();
		Wallet wallet = wallets.getWallet(Wallets.TMA, Wallets.WALLET_NAME);
		Coin amount = Coin.SATOSHI.multiply(2);
		List<Coin> totals = new ArrayList<Coin>();
		totals.add(amount);
		for(String word: accountKeywords.keySet()) {
			if(word.equals(accountKeywords.get(word))) {
				totals.add(amount);
			}
		}

		List<Set<TransactionOutput>> inputList = new GetInputsRequest(network, tmaAddress, totals).getInputlist();
		int i = 0;
		
		if(inputList.size() != totals.size()) {
			label.setText("No inputs available for tma address " + tmaAddress + ". Please check your balance.");
			return;
		}

		Keywords keywords = new Keywords();
		keywords.put("delete", transactionId);
		
		Transaction transaction = new Transaction(wallet.getPublicKey(), rateeTmaAddress, Coin.SATOSHI, Coin.SATOSHI, 
				inputList.get(i++), wallet.getPrivateKey(), null, null, keywords);
		transaction.setApp(Applications.RATING);
		new SendTransactionRequest(network, transaction).start();
		logger.debug("sent {}", transaction);
		
		for(String word: accountKeywords.keySet()) {
			if(word.equals(accountKeywords.get(word))) {
				Keywords words = new Keywords();
				words.put("delete", transactionId);
				Transaction keyWordTransaction = new Transaction(wallet.getPublicKey(), StringUtil.getTmaAddressFromString(word), Coin.SATOSHI, Coin.SATOSHI, 
						inputList.get(i++), wallet.getPrivateKey(), null, null, words);
				keyWordTransaction.setApp(Applications.RATING);
				new SendTransactionRequest(network, keyWordTransaction).start();
				logger.debug("sent {}", keyWordTransaction);
			}
		}
		
		label.setText("Post with identifier " + transactionId + " was deleted.");
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

}
