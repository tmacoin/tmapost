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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.peer.Network;
import org.tma.peer.thin.Balance;
import org.tma.peer.thin.GetBalanceRequest;

public class GetBalanceAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4798442956508802794L;
	private static final Logger logger = LogManager.getLogger();
	
	private JFrame frame;
	private JTextField address;

	public GetBalanceAction(JFrame frame, JTextField address) {
		putValue(NAME, "Get Balance");
		putValue(SHORT_DESCRIPTION, "Get Balance Action");
		this.frame = frame;
		this.address = address;
	}

	public void actionPerformed(ActionEvent e) {
		frame.getContentPane().removeAll();
		new GetBalanceRequest(Network.getInstance(), address.getText()).start();
		
		logger.debug("balance: {}", Balance.getInstance().getBalance());
		
		JLabel label = new JLabel("Balance for " + address.getText());
		label.setBounds(20, 104, 350, 14);
		frame.getContentPane().add(label);
		
		JLabel balance = new JLabel(Balance.getInstance().getBalance());
		balance.setBounds(20, 144, 350, 14);
		frame.getContentPane().add(balance);
		
		
		frame.getContentPane().repaint();
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}



}
