/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.blockchain.Transaction;
import org.tma.blockchain.TransactionData;
import org.tma.blockchain.TransactionOutput;
import org.tma.blockchain.Wallet;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.SendTransactionRequest;
import org.tma.peer.thin.GetInputsRequest;
import org.tma.post.util.SwingUtil;
import org.tma.util.Coin;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class SendTransactionAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4798442956508802794L;
	private static final Logger logger = LogManager.getLogger();
	
	private JFrame frame;
	
	private JTextField jaddress;
	private JTextField jamount;
	private JTextField jfee;
	private JTextField jdata;
	private JTextField jexpire;
	private JTextArea jexpiringData;
	
	
	private String recipient;
	private String amount; 
	private String fee; 
	private String data; 
	private String expire; 
	private String expiringData;

	public SendTransactionAction(JFrame frame, JTextField address, JTextField amount, JTextField fee, JTextField data, JTextField expire, JTextArea expiringData) {
		putValue(NAME, "Send Transaction");
		putValue(SHORT_DESCRIPTION, "Send Transaction Action");
		this.frame = frame;
		
		jaddress = address;
		jamount = amount;
		jfee = fee;
		jdata = data;
		jexpire = expire;
		jexpiringData = expiringData;
	}
	
	private boolean validate() {
		if(!StringUtil.isTmaAddressValid(recipient)) {
			return false;
		}
		try {
			Double.parseDouble(amount);
			Long.parseLong(fee);
			if(expiringData != null) {
				Long.parseLong(expire);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private void load() {
		recipient = StringUtil.trim(jaddress.getText());
		amount = StringUtil.trim(jamount.getText());
		fee = StringUtil.trim(jfee.getText());
		data = StringUtil.trimToNull(jdata.getText());
		expire = StringUtil.trim(jexpire.getText());
		expiringData = StringUtil.trimToNull(jexpiringData.getText());
	}

	public void actionPerformed(ActionEvent e) {
		load();
		
		if(!validate()) {
			log("Please verify inputs");
			return;
		}
		Network network = Network.getInstance();
		String tmaAddress = network.getTmaAddress();
		Coin total = Coin.ONE.multiply(Double.parseDouble(amount)).add(new Coin(Long.parseLong(fee)));
		Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA, Wallets.WALLET_NAME);
		TransactionData expiringData = this.expiringData == null? null: new TransactionData(this.expiringData, Long.parseLong(expire));


		JLabel label = SwingUtil.showWait(frame);
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("SendTransactionAction") {
			public void doRun() {
				Network network = Network.getInstance();
				if(!network.isPeerSetComplete()) {
					new BootstrapRequest(network).start();
				}
				List<Coin> totals = new ArrayList<Coin>();
				totals.add(total);
				List<Set<TransactionOutput>> inputList = new GetInputsRequest(network, tmaAddress, totals).getInputlist();
				int i = 0;
				
				if(inputList.size() != totals.size()) {
					label.setText("No inputs available for tma address " + tmaAddress + ". Please check your balance.");
					return;
				}
				
				Set<TransactionOutput> inputs = inputList.get(i++);
				logger.debug("number of inputs: {} for {}", inputs.size(), tmaAddress);
				Transaction transaction = new Transaction(wallet.getPublicKey(), recipient, Coin.ONE.multiply(Double.parseDouble(amount)), 
						new Coin(Integer.parseInt(fee)), inputs, wallet.getPrivateKey(), data, expiringData, null);
				logger.debug("sent {}", transaction);
				new SendTransactionRequest(Network.getInstance(), transaction).start();
				
				label.setText("Transaction was sent");
			}
		});
		
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}



}
