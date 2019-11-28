/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class CreateTwitter extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;
	
	private JFrame frame;
	private JTextField account;
	
	public CreateTwitter(JFrame frame) {
		putValue(NAME, "Create Twitter");
		putValue(SHORT_DESCRIPTION, "Create Twitter");
		this.frame = frame;
	}
	
	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		frame.getContentPane().removeAll();
		Wallets wallets = Wallets.getInstance();
		for(String key: wallets.getKeys()) {
			if(key.startsWith(Wallets.TWITTER + "-")) {
				String accountName = key.split("-")[1];
				log("Twitter account was already created. Account name is " + accountName);
				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();
				return;
			}
		}

		createForm();
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
		account.grabFocus();
	}
	
	private void createForm() {
		JPanel form = new JPanel(new BorderLayout());
		frame.getContentPane().add(form, BorderLayout.NORTH);
		
		JPanel labelPanel = new JPanel(new GridLayout(3, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(3, 1));
		form.add(labelPanel, BorderLayout.WEST);
		form.add(fieldPanel, BorderLayout.CENTER);
		
		JLabel label = new JLabel("Account name:", JLabel.RIGHT);
		labelPanel.add(label);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		account = new JTextField(36);
		JTextFieldRegularPopupMenu.addTo(account);
		p.add(account);
		fieldPanel.add(p);
		
		label = new JLabel("Description:", JLabel.RIGHT);
		labelPanel.add(label);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField description = new JTextField(36);
		JTextFieldRegularPopupMenu.addTo(description);
		p.add(description);
		fieldPanel.add(p);
		
		label = new JLabel("Enter Passphrase:", JLabel.RIGHT);
		labelPanel.add(label);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPasswordField passwordField = new JPasswordField(20);
		p.add(passwordField);
		fieldPanel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new CreateTwitterAction(frame, account, description, passwordField));
		p.add(btnSubmit);
		fieldPanel.add(p);
		
		frame.getContentPane().add(p);

		frame.getRootPane().setDefaultButton(btnSubmit);
	}
	

}
