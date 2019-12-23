/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.tma.post.Caller;
import org.tma.post.Wallets;
import org.tma.post.util.JTextFieldRegularPopupMenu;

public class CreateTwitter extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;
	private static final Wallets wallets = Wallets.getInstance();
	
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
		String key = wallets.getKeyStartsWith(Wallets.TWITTER + "-");
		if (key != null) {
			String accountName = key.split("-", 2)[1];

			JPanel form = new JPanel(new BorderLayout());

			JTextArea message = new JTextArea();
			message.setText("Twitter account was already created. Account name is " + accountName + " with tma address " + wallets.getWallet(key).getTmaAddress());
			message.setLineWrap(true);
			message.setWrapStyleWord(true);
			message.setOpaque(false);
			message.setEditable(false);
			message.setBorder(new EmptyBorder(5,5,5,5));

			JScrollPane scroll = new JScrollPane(message);
			scroll.setBorder(null);
			form.add(scroll);

			form.add(message);
			frame.getContentPane().add(form, BorderLayout.NORTH);

			frame.getContentPane().revalidate();
			frame.getContentPane().repaint();
			return;
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
		account = new JTextField(45);
		JTextFieldRegularPopupMenu.addTo(account);
		p.add(account);
		fieldPanel.add(p);
		
		label = new JLabel("Description:", JLabel.RIGHT);
		labelPanel.add(label);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField description = new JTextField(45);
		JTextFieldRegularPopupMenu.addTo(description);
		p.add(description);
		fieldPanel.add(p);
		
		label = new JLabel("Enter Passphrase:", JLabel.RIGHT);
		labelPanel.add(label);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPasswordField passwordField = new JPasswordField(45);
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
