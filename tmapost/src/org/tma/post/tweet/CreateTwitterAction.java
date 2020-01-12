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
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.tma.blockchain.Keywords;
import org.tma.blockchain.Transaction;
import org.tma.blockchain.TransactionOutput;
import org.tma.blockchain.Wallet;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.SendTransactionRequest;
import org.tma.peer.thin.GetBalanceRequest;
import org.tma.peer.thin.GetInputsRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.post.Caller;
import org.tma.post.Wallets;
import org.tma.post.key.PasswordUtil;
import org.tma.post.util.SwingUtil;
import org.tma.util.Applications;
import org.tma.util.Coin;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

public class CreateTwitterAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4008418980341407814L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final int POWER = 20;
	
	private JFrame frame;
	private JTextField account;
	private JPasswordField passwordField;
	private JTextField description;
	
	public CreateTwitterAction(JFrame frame, JTextField account, JTextField description, JPasswordField passwordField) {
		putValue(NAME, "Create Twitter Account");
		putValue(SHORT_DESCRIPTION, "Create Twitter Account");
		this.frame = frame;
		this.account = account;
		this.passwordField = passwordField;
		this.description = description;
		
	}

	public void actionPerformed(ActionEvent e) {
		
		final JLabel label = SwingUtil.showWait(frame);
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("CreateTwitterAction") {
			public void doRun() {
				try {
					if(generateKeyPair(label)) {
						label.setText("Twitter account key pair created for " + account.getText());
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
				frame.revalidate();
				frame.getContentPane().repaint();
			}
		});
	}
	
	private boolean generateKeyPair(JLabel label) throws Exception {
		
		Network network = Network.getInstance();
		
		GetBalanceRequest request = new GetBalanceRequest(network, network.getTmaAddress());
		request.start();
		String balance = (String)ResponseHolder.getInstance().getObject(request.getCorrelationId());
		if("0".equals(balance)) {
			label.setText("Your balance is zero. You cannot create Twitter account.");
			return false;
		}
		
		String passphrase = StringUtil.trim(new String(passwordField.getPassword()));
		PasswordUtil passwordUtil = new PasswordUtil(this);

		if (!passwordUtil.loadKeys(passphrase)) {
			return false;
		}

		Wallet wallet = new Wallet();
		int shardId = StringUtil.getShardForNonTmaAddress(account.getText(), POWER);
		logger.debug("shardId: {}", shardId);
		while (true) {
			wallet.generateKeyPair();
			if (StringUtil.getShard(wallet.getTmaAddress(), POWER) == shardId) {
				break;
			}
		}
		shardId = StringUtil.getShard(wallet.getTmaAddress(), network.getBootstrapShardingPower());
		logger.debug("shardId: {}", shardId);
		Wallets wallets = Wallets.getInstance();
		wallets.putWallet(Wallets.TWITTER, account.getText(), wallet);
		passwordUtil.saveKeys(passphrase);
		return sendCreateTwitterTransaction(wallet.getTmaAddress(), label);
	}
	
	private boolean sendCreateTwitterTransaction(String twitterTmaAddress, JLabel label) {
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		String tmaAddress = network.getTmaAddress();
		Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA, Wallets.WALLET_NAME);
		Coin amount = Coin.SATOSHI.multiply(2);
		List<Coin> totals = new ArrayList<Coin>();
		totals.add(amount);
		List<Set<TransactionOutput>> inputList = new GetInputsRequest(network, tmaAddress, totals).getInputlist();
		int i = 0;
		
		if(inputList.size() != totals.size()) {
			label.setText("No inputs available for tma address " + tmaAddress + ". Please check your balance.");
			return false;
		}
		
		Set<TransactionOutput> inputs = inputList.get(i++);
		
		Keywords keywords = new Keywords();
		keywords.getMap().put("create", account.getText());
		
		Transaction transaction = new Transaction(wallet.getPublicKey(), twitterTmaAddress, Coin.SATOSHI, Coin.SATOSHI, 
				inputs, wallet.getPrivateKey(), description.getText(), null, keywords);
		transaction.setApp(Applications.TWITTER);
		new SendTransactionRequest(network, transaction).start();
		logger.debug("sent {}", transaction);
		return true;
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

}
