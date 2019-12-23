/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.security.Security;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

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
	private JLabel label;
	
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
			passwordUtil.generateKey(Wallets.TMA, passphrase, confirmPassword);
			
			frame.getContentPane().removeAll();
			JPanel form = new JPanel(new BorderLayout());
			label = new JLabel("New key generated, starting network");
			label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			label.setBorder(new EmptyBorder(5,5,5,5));
			form.add(label);
			frame.getContentPane().add(form, BorderLayout.NORTH);
			frame.revalidate();
			frame.getContentPane().repaint();

			StartNetwork.getInstance().start(this);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void log(String message) {
		if(label != null) {
			label.setText(message);
			MenuCreator.addMenu(frame);
			return;
		}
		JOptionPane.showMessageDialog(frame, message);
	}

}
