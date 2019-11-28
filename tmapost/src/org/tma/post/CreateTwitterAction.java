/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.event.ActionEvent;
import java.security.NoSuchAlgorithmException;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.blockchain.Wallet;
import org.tma.post.key.PasswordUtil;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class CreateTwitterAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4008418980341407814L;
	private static final Logger logger = LogManager.getLogger();
	private static final int POWER = 20;
	
	private JFrame frame;
	private JTextField account;
	private JPasswordField passwordField;
	
	public CreateTwitterAction(JFrame frame, JTextField account, JPasswordField passwordField) {
		putValue(NAME, "Create Twitter Account");
		putValue(SHORT_DESCRIPTION, "Create Twitter Account");
		this.frame = frame;
		this.account = account;
		this.passwordField = passwordField;
	}

	public void actionPerformed(ActionEvent e) {
		
		JLabel label = SwingUtil.showWait(frame);
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("ShowMessages") {
			public void doRun() {
				try {
					if(generateKeyPair()) {
						label.setText("Twitter account key pair created for " + account.getText());
					} else {
						label.setText("");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
				frame.revalidate();
				frame.getContentPane().repaint();
			}
		});
	}
	
	private boolean generateKeyPair() throws Exception {
		String passphrase = StringUtil.trim(new String(passwordField.getPassword()));
		PasswordUtil passwordUtil = new PasswordUtil(this);

		if (!passwordUtil.loadKeys(passphrase)) {
			return false;
		}

		Wallet wallet = new Wallet();
		int shardId = getShard(account.getText(), POWER);
		logger.debug("shardId: {}", shardId);
		while (true) {
			wallet.generateKeyPair();
			if (StringUtil.getShard(wallet.getTmaAddress(), POWER) == shardId) {
				break;
			}
		}
		Wallets wallets = Wallets.getInstance();
		wallets.putWallet(Wallets.TWITTER + "-" + account.getText(), wallet);
		passwordUtil.saveKeys(passphrase);
		return true;
	}
	
	public static int getShard(String input, int power) {
		if(power == 0) {
			return 0;
		}
		byte[] bytes = null;
		try {
			bytes = StringUtil.getBytesSha256(input);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
		String str = "";
		for(byte b: bytes) {
			str = str + Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		}
		str = str.substring(0, power);
		return Integer.valueOf(str, 2);
	}
	
	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

}
