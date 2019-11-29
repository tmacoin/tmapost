/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.blockchain.Transaction;
import org.tma.blockchain.TransactionOutput;
import org.tma.blockchain.Wallet;
import org.tma.peer.Network;
import org.tma.peer.SendTransactionRequest;
import org.tma.peer.thin.GetInputsRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.Tweet;
import org.tma.post.Caller;
import org.tma.post.SwingUtil;
import org.tma.post.Wallets;
import org.tma.util.Applications;
import org.tma.util.Coin;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class SendReplyAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = -2832907634168820222L;
	private static final Logger logger = LogManager.getLogger();
	
	private JFrame frame;
	private JTextArea area;
	private Tweet tweet;

	public SendReplyAction(JFrame frame, JTextArea area, Tweet tweet) {
		putValue(NAME, "Send Reply");
		putValue(SHORT_DESCRIPTION, "Send Reply");
		this.frame = frame;
		this.area = area;
		this.tweet = tweet;
		
	}

	public void actionPerformed(ActionEvent e) {
		JLabel jlabel = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("SendTweet") {
			public void doRun() {
				sendTweetReplyTransaction();
				jlabel.setText("Reply was sent");
			}
		});

	}

	private void sendTweetReplyTransaction() {
		String twitterTmaAddress = tweet.getRecipient();
		String tmaAddress = Network.getInstance().getTmaAddress();
		Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA);
		GetInputsRequest request = new GetInputsRequest(Network.getInstance(), tmaAddress, Coin.SATOSHI);
		request.start();
		@SuppressWarnings("unchecked")
		Set<TransactionOutput> inputs = (Set<TransactionOutput>)ResponseHolder.getInstance().getObject(request.getCorrelationId()); 
		Transaction transaction = new Transaction(wallet.getPublicKey(), twitterTmaAddress, Coin.SATOSHI, Coin.SATOSHI, 
				inputs, wallet.getPrivateKey(), area.getText(), null);
		transaction.setApp(Applications.TWITTER);
		
		Set<String> keywords = new HashSet<String>();
		keywords.add(tweet.getTransactionId());
		transaction.setKeywords(keywords);
		
		new SendTransactionRequest(Network.getInstance(), transaction).start();
		logger.debug("sent {}", transaction);
		
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}


}
