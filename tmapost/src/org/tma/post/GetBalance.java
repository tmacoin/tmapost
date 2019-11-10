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
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class GetBalance extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;
	
	private JFrame frame;
	
	public GetBalance(JFrame frame) {
		putValue(NAME, "Get Balance");
		putValue(SHORT_DESCRIPTION, "Get Balance");
		this.frame = frame;
	}
	
	
	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		
		frame.getContentPane().removeAll();
		
		JLabel label = new JLabel("Enter TMA Address:");
		label.setBounds(20, 74, 160, 14);
		frame.getContentPane().add(label);
		
		JTextField address = new JTextField(36);
		address.setBounds(160, 71, 260, 20);
		frame.getContentPane().add(address);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new GetBalanceAction(frame, address));
		btnSubmit.setBounds(241, 102, 105, 23);
		frame.getContentPane().add(btnSubmit);
		
		frame.getRootPane().setDefaultButton(btnSubmit);
		frame.getContentPane().repaint();
	}

	

}
