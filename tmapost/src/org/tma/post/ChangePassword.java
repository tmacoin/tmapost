/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class ChangePassword extends AbstractAction {

	private static final long serialVersionUID = -5588285023715553613L;
	
	private JFrame frame;
	private JPasswordField oldPasswordField;
	private JPasswordField newPasswordField;
	private JPasswordField confirmPasswordField;

	public ChangePassword(JFrame frame) {
		this.frame = frame;
		putValue(NAME, "Change Password");
		putValue(SHORT_DESCRIPTION, "Change Password");
	}

	public void actionPerformed(ActionEvent actionEvent) {
		
		frame.getContentPane().removeAll();
		
		JLabel lblEnterPassphrase = new JLabel("Enter Existing Passphrase:");
		lblEnterPassphrase.setBounds(49, 74, 160, 14);
		frame.getContentPane().add(lblEnterPassphrase);
		
		oldPasswordField = new JPasswordField();
		oldPasswordField.setBounds(209, 71, 138, 20);
		frame.getContentPane().add(oldPasswordField);
		
		JLabel lblEnterNewPassphrase = new JLabel("Enter New Passphrase:");
		lblEnterNewPassphrase.setBounds(49, 104, 160, 14);
		frame.getContentPane().add(lblEnterNewPassphrase);
		
		newPasswordField = new JPasswordField();
		newPasswordField.setBounds(209, 101, 138, 20);
		frame.getContentPane().add(newPasswordField);
		
		JLabel lblconfirmPassphrase = new JLabel("Reenter New Passphrase:");
		lblconfirmPassphrase.setBounds(49, 134, 160, 14);
		frame.getContentPane().add(lblconfirmPassphrase);
		
		confirmPasswordField = new JPasswordField();
		confirmPasswordField.setBounds(209, 131, 138, 20);
		frame.getContentPane().add(confirmPasswordField);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new ChangePasswordAction(frame, oldPasswordField, newPasswordField, confirmPasswordField));
		btnSubmit.setBounds(256, 162, 91, 23);
		frame.getContentPane().add(btnSubmit);
		
		frame.getRootPane().setDefaultButton(btnSubmit);
		
	}

}
