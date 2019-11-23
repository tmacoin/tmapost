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
import javax.swing.JPanel;
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
		
		JPanel form = new JPanel(new BorderLayout());
		
		JPanel labelPanel = new JPanel(new GridLayout(4, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(4, 1));
		form.add(labelPanel, BorderLayout.WEST);
		form.add(fieldPanel, BorderLayout.CENTER);
		
		JLabel label = new JLabel("Enter Old Passphrase:", JLabel.RIGHT);
		labelPanel.add(label);
		
		label = new JLabel("Enter New Passphrase:", JLabel.RIGHT);
		labelPanel.add(label);
		
		label = new JLabel("Reenter New Passphrase:");
		labelPanel.add(label);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oldPasswordField = new JPasswordField(20);
		p.add(oldPasswordField);
		fieldPanel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		newPasswordField = new JPasswordField(20);
		p.add(newPasswordField);
		fieldPanel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		confirmPasswordField = new JPasswordField(20);
		p.add(confirmPasswordField);
		fieldPanel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new ChangePasswordAction(frame, oldPasswordField, newPasswordField, confirmPasswordField));
		p.add(btnSubmit);
		fieldPanel.add(p);

		frame.getContentPane().add(form, BorderLayout.NORTH);
		frame.getRootPane().setDefaultButton(btnSubmit);
		//frame.pack();
		frame.revalidate();
		frame.getContentPane().repaint();
	}

}
