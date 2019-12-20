/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import net.miginfocom.swing.MigLayout;

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
		
		JPanel form = new JPanel(new MigLayout("wrap 2", "[right][fill]"));
		
		JLabel label = new JLabel("Enter Old Passphrase:");
		form.add(label);
		
		oldPasswordField = new JPasswordField(45);
		form.add(oldPasswordField);
		
		label = new JLabel("Enter New Passphrase:");
		form.add(label);
		
		newPasswordField = new JPasswordField(45);
        form.add(newPasswordField);
		
		label = new JLabel("Reenter New Passphrase:");
		form.add(label);

		confirmPasswordField = new JPasswordField(45);
		form.add(confirmPasswordField);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new ChangePasswordAction(frame, oldPasswordField, newPasswordField, confirmPasswordField));
		p.add(btnSubmit);
		form.add(p);

		frame.getContentPane().add(form);
		frame.getRootPane().setDefaultButton(btnSubmit);
		frame.revalidate();
		frame.getContentPane().repaint();
	}

}
