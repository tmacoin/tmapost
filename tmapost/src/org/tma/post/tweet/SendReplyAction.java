/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.tma.blockchain.Keywords;
import org.tma.blockchain.Transaction;
import org.tma.blockchain.TransactionOutput;
import org.tma.blockchain.Wallet;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.SendTransactionRequest;
import org.tma.peer.thin.GetInputsRequest;
import org.tma.peer.thin.Tweet;
import org.tma.post.Caller;
import org.tma.post.Wallets;
import org.tma.post.util.SwingUtil;
import org.tma.util.Applications;
import org.tma.util.Coin;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

import com.jidesoft.swing.AutoResizingTextArea;

import net.miginfocom.swing.MigLayout;

public class SendReplyAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = -2832907634168820222L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	
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
				sendTweetReplyTransaction(jlabel);
				
			}
		});

	}

	private void sendTweetReplyTransaction(JLabel label) {
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		String twitterTmaAddress = tweet.getRecipient();
		String tmaAddress = network.getTmaAddress();
		Wallets wallets = Wallets.getInstance();
		Wallet wallet = wallets.getWallet(Wallets.TMA, Wallets.WALLET_NAME);
		Coin amount = Coin.SATOSHI.multiply(2);
		List<Coin> totals = new ArrayList<Coin>();
		totals.add(amount);
		List<Set<TransactionOutput>> inputList = new GetInputsRequest(network, tmaAddress, totals).getInputlist();
		int i = 0;
		
		if(inputList.size() != totals.size()) {
			label.setText("No inputs available for tma address " + tmaAddress + ". Please check your balance.");
			return;
		}
		
		Set<TransactionOutput> inputs = inputList.get(i++);
		
		Keywords keywords = new Keywords();
		keywords.getMap().put("transactionId", tweet.getTransactionId());
		Collection<String> names = wallets.getNames(Wallets.TWITTER);
		if (!names.isEmpty()) {
			String accountName = names.iterator().next();
			keywords.getMap().put("from", accountName);
		} else {
			label.setText("You have not created your twitter account yet.");
			return;
		}
		
		Transaction transaction = new Transaction(wallet.getPublicKey(), twitterTmaAddress, Coin.SATOSHI, Coin.SATOSHI, 
				inputs, wallet.getPrivateKey(), area.getText(), null, keywords);
		transaction.setApp(Applications.TWITTER);
		
		new SendTransactionRequest(network, transaction).start();
		logger.debug("sent {}", transaction);
		feedback();
	}
	
	private void feedback() {
		frame.getContentPane().removeAll();

		JPanel form = new JPanel(new MigLayout("wrap 2", "[right][fill]"));
		JScrollPane scrollPane = new JScrollPane(form, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);
		
		form.add(new JLabel("The reply was successfully sent:"), "span, left");
		
		JTextArea description = new AutoResizingTextArea(5, 40, 55);
		description.setText(area.getText());
		
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setOpaque(false);
		description.setEditable(false);
		description.revalidate();
		
		form.add(description, "span, left");
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}


}
