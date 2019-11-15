/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.event.ActionEvent;
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
import org.tma.peer.Network;
import org.tma.peer.SendTransactionRequest;
import org.tma.peer.thin.GetInputsRequest;
import org.tma.peer.thin.Inputs;
import org.tma.util.Coin;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class SendTransactionAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4798442956508802794L;
	private static final Logger logger = LogManager.getLogger();
	
	private JFrame frame;
	private JTextField address;
	private JTextField amount; 
	private JTextField fee; 
	private JTextField data; 
	private JTextField expire; 
	private JTextArea expiringData;
	private JLabel label;

	public SendTransactionAction(JFrame frame, JTextField address, JTextField amount, JTextField fee, JTextField data, JTextField expire, JTextArea expiringData) {
		putValue(NAME, "Send Transaction");
		putValue(SHORT_DESCRIPTION, "Send Transaction Action");
		this.frame = frame;
		this.address = address;
		this.amount = amount;
		this.fee = fee;
		this.data = data;
		this.expire = expire;
		this.expiringData = expiringData;
	}

	public void actionPerformed(ActionEvent e) {
		String tmaAddress = Network.getInstance().getTmaAddress();
		String recipient = address.getText();
		Coin total = Coin.ONE.multiply(Double.parseDouble(amount.getText())).add(new Coin(Integer.parseInt(fee.getText())));
		Wallet wallet = Wallets.getInstance().getWallets().get(0);
		TransactionData expiringData = new TransactionData(this.expiringData.getText(), Long.parseLong(expire.getText()));
		frame.getContentPane().removeAll();
		
		label = new JLabel("Please wait, processing.");
		label.setBounds(100, 124, 250, 14);
		frame.getContentPane().add(label);
		frame.getContentPane().repaint();
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("SendTransactionAction") {
			public void doRun() {
				GetInputsRequest request = new GetInputsRequest(Network.getInstance(), tmaAddress, total);
				request.start();
				Set<TransactionOutput> inputs = Inputs.getInstance().getInputs(request.getCorrelationId()); 
				logger.debug("number of inputs: {} for {}", inputs.size(), tmaAddress);
				Transaction transaction = new Transaction(wallet.getPublicKey(), recipient, Coin.ONE.multiply(Double.parseDouble(amount.getText())), 
						new Coin(Integer.parseInt(fee.getText())), inputs, wallet.getPrivateKey(), data.getText(), expiringData);
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
