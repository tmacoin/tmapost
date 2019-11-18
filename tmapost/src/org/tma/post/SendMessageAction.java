/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
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
import org.tma.persistance.Encryptor;
import org.tma.util.Applications;
import org.tma.util.Base58;
import org.tma.util.Coin;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class SendMessageAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4798442956508802794L;
	private static final Logger logger = LogManager.getLogger();
	private static final Encryptor encryptor = new Encryptor();
	
	private JFrame frame;
	
	private JTextField jaddress;
	private JTextField jfee;
	private JTextField jexpire;
	private JTextArea jexpiringData;
	
	
	private PublicKey recipient;
	private String fee; 
	private String expire; 
	private String expiringData;
	private JLabel label;

	public SendMessageAction(JFrame frame, JTextField address, JTextField fee, JTextField expire, JTextArea expiringData) {
		putValue(NAME, "Send Message");
		putValue(SHORT_DESCRIPTION, "Send Message Action");
		this.frame = frame;
		
		jaddress = address;
		jfee = fee;
		jexpire = expire;
		jexpiringData = expiringData;
	}
	
	private boolean validate() {
		if(recipient == null) {
			return false;
		}
		try {
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
		try {
			recipient = StringUtil.loadPublicKey(jaddress.getText());
		} catch (GeneralSecurityException e) {
			logger.error(e.getMessage(), e);
		}
		fee = StringUtil.trim(jfee.getText());
		expire = StringUtil.trim(jexpire.getText());
		expiringData = StringUtil.trimToNull(jexpiringData.getText());
	}

	public void actionPerformed(ActionEvent ae) {
		load();
		
		if(!validate()) {
			log("Please verify inputs");
			return;
		}
		String tmaAddress = Network.getInstance().getTmaAddress();
		Coin total = Coin.SATOSHI.add(new Coin(Long.parseLong(fee)));
		Wallet wallet = Wallets.getInstance().getWallets().get(0);
		TransactionData data = null;
		if(expiringData != null) {
			try {
				expiringData = Base58.encode(encryptor.encryptAsymm(expiringData.getBytes(StandardCharsets.UTF_8), recipient));
				data = new TransactionData(this.expiringData, Long.parseLong(expire));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		TransactionData expiringData = data;
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
				Transaction transaction = new Transaction(wallet.getPublicKey(), StringUtil.getStringFromKey(recipient), Coin.SATOSHI, 
						new Coin(Integer.parseInt(fee)), inputs, wallet.getPrivateKey(), null, expiringData);
				transaction.setApp(Applications.MESSAGING);
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
