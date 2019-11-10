/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.event.ActionEvent;
import java.security.Security;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.tma.post.key.PasswordUtil;

public class CreateKeyAction extends AbstractAction implements Caller {
	
	static {
		Security.setProperty("crypto.policy", "unlimited");
		Security.addProvider(new BouncyCastleProvider());
	}
	
	private static final long serialVersionUID = -5348678702516608164L;
	private static final Logger logger = LogManager.getLogger();
	
	private JFrame frame;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	
	public CreateKeyAction(JFrame frame, JPasswordField passwordField, JPasswordField confirmPasswordField) {
		putValue(NAME, "Submit");
		putValue(SHORT_DESCRIPTION, "Submit Passphrase");
		this.frame = frame;
		this.passwordField = passwordField;
		this.confirmPasswordField = confirmPasswordField;
	}
	
	public void actionPerformed(ActionEvent actionEvent) {
		String passphrase = new String(passwordField.getPassword());
		String confirmPassword = new String(confirmPasswordField.getPassword());
		if(!passphrase.equals(confirmPassword)) {
			log("Passphrase and reentered passphrase do not match");
			return;
		}
		try {
			PasswordUtil passwordUtil = new PasswordUtil(this);
			passwordUtil.generateKey(passphrase, confirmPassword);
			frame.getContentPane().removeAll();
			JLabel label = new JLabel("New key generated");
			label.setBounds(160, 124, 160, 14);
			frame.getContentPane().add(label);
			frame.getContentPane().repaint();
			StartNetwork.getInstance().start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

}
