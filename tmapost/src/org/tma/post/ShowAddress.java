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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.tma.blockchain.Wallet;
import org.tma.peer.Network;
import org.tma.util.Base58;

public class ShowAddress extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;
	
	private JFrame frame;
	
	public ShowAddress(JFrame frame) {
		putValue(NAME, "Show Address");
		putValue(SHORT_DESCRIPTION, "Show Address");
		this.frame = frame;
	}
	
	
	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		
		frame.getContentPane().removeAll();
		Wallet wallet = Wallets.getInstance().getWallets().get(0);
		
		JLabel label = new JLabel("Your TMA Address on shard " + Network.getInstance().getBootstrapBlockchainId() + ":");
		label.setBounds(20, 20, 250, 14);
		frame.getContentPane().add(label);
		
		JTextField address = new JTextField(36);
		address.setText(wallet.getTmaAddress());
		address.setBounds(20, 40, 300, 20);
		JTextFieldRegularPopupMenu.addTo(address);
		frame.getContentPane().add(address);
		
		label = new JLabel("Your Public Key:");
		label.setBounds(20, 80, 250, 14);
		frame.getContentPane().add(label);
		
		JTextArea publicKey = new JTextArea();
		publicKey.setLineWrap(true);
		publicKey.setToolTipText("Public Key");
		publicKey.setText(Base58.encode(wallet.getPublicKey().getEncoded()));
		JTextFieldRegularPopupMenu.addTo(publicKey);
		JScrollPane scroll = new JScrollPane (publicKey);
		scroll.setBounds(20, 100, 300, 70);
		frame.getContentPane().add(scroll);
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}

	

}
