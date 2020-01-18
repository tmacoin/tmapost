/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.message;

import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.tma.blockchain.Transaction;
import org.tma.blockchain.TransactionData;
import org.tma.blockchain.TransactionOutput;
import org.tma.blockchain.Wallet;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.SendTransactionRequest;
import org.tma.peer.thin.GetInputsRequest;
import org.tma.peer.thin.GetPublicKeyRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.post.Caller;
import org.tma.post.Wallets;
import org.tma.post.util.KeyValue;
import org.tma.post.util.SwingUtil;
import org.tma.util.Applications;
import org.tma.util.Base58;
import org.tma.util.Coin;
import org.tma.util.Encryptor;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

public class SendMessageAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4798442956508802794L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Encryptor encryptor = new Encryptor();
	
	private JFrame frame;
	
	private JTextField jaddress;
	private JTextField jfee;
	private JComboBox<KeyValue> jexpire;
	private JTextField jsubject;
	private JTextArea jexpiringData;

	private PublicKey recipient;
	private String fee; 
	private String expire;
	private String subject;
	private String expiringData;
	

	public SendMessageAction(JFrame frame, JTextField address, JTextField fee, JComboBox<KeyValue> expire, JTextField subject, JTextArea expiringData) {
		putValue(NAME, "Send Secure Message");
		putValue(SHORT_DESCRIPTION, "Send SecureS Message Action");
		this.frame = frame;
		
		jaddress = address;
		jfee = fee;
		jexpire = expire;
		jexpiringData = expiringData;
		this.jsubject = subject;
	}
	
	private boolean validate() {
		String tmaAddress = StringUtil.trim(jaddress.getText());
		if(!StringUtil.isTmaAddressValid(tmaAddress)) {
			return false;
		}
		try {
			Long.parseLong(fee);
			if(subject != null) {
				Long.parseLong(expire);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private void load() {
		fee = StringUtil.trim(jfee.getText());
		expire = StringUtil.trim(((KeyValue) jexpire.getSelectedItem()).getValue());
		subject = StringUtil.trimToNull(jsubject.getText());
		expiringData = StringUtil.trimToNull(jexpiringData.getText());
	}

	public void actionPerformed(ActionEvent ae) {
		load();
		
		if(!validate()) {
			log("Please verify inputs");
			return;
		}
		final String tmaAddress = Network.getInstance().getTmaAddress();
		final Coin total = Coin.SATOSHI.add(new Coin(Long.parseLong(fee)));
		final Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA, Wallets.WALLET_NAME);
		
		final JLabel label = SwingUtil.showWait(frame);
		
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
				
				String recipientTmaAddress = jaddress.getText();
				GetPublicKeyRequest getPublicKeyRequest = new GetPublicKeyRequest(network, recipientTmaAddress);
				getPublicKeyRequest.start();
				recipient = (PublicKey) ResponseHolder.getInstance().getObject(getPublicKeyRequest.getCorrelationId());
				
				if(recipient == null) {
					logger.debug("Recipient public key is not found for tma address {}", recipientTmaAddress);
					label.setText("Recipient public key is not found for tma address " + recipientTmaAddress);
					return;
				}
				
				TransactionData data = null;
				if(subject != null) {
					try {
						String str = subject + "\n" + expiringData;
						byte[] encrypted = encryptor.encryptAsymm(str.getBytes(StandardCharsets.UTF_8), recipient);
						str = Base58.encode(encrypted);
						data = new TransactionData(str, Long.parseLong(expire));
						if(!str.equals(data.getData())) {
							logger.debug("Encrypted string length is {}", str.length());
							label.setText("Message is too long, cannot send.");
							return;
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						label.setText(e.getMessage());
						return;
					}
				}
				
				Transaction transaction = new Transaction(wallet.getPublicKey(), StringUtil.getStringFromKey(recipient), Coin.SATOSHI, 
						new Coin(Integer.parseInt(fee)), inputs, wallet.getPrivateKey(), null, data, null);
				transaction.setApp(Applications.MESSAGING);
				logger.debug("sent {}", transaction);
				new SendTransactionRequest(network, transaction).start();
				
				label.setText("Message was sent");
			}
		});
		
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}



}
