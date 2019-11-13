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
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class SendTransactionAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4798442956508802794L;
	private static final Logger logger = LogManager.getLogger();
	
	private JFrame frame;
	private JTextField address;

	public SendTransactionAction(JFrame frame, JTextField address) {
		putValue(NAME, "Send Transaction");
		putValue(SHORT_DESCRIPTION, "Send Transaction Action");
		this.frame = frame;
		this.address = address;
	}

	public void actionPerformed(ActionEvent e) {
		String tmaAddress = address.getText();
		frame.getContentPane().removeAll();
		
		JLabel label = new JLabel("Please wait, processing.");
		label.setBounds(20, 104, 350, 14);
		frame.getContentPane().add(label);
		frame.getContentPane().repaint();
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("SendTransactionAction") {
			public void doRun() {
				GetBalanceRequest request = new GetBalanceRequest(Network.getInstance(), tmaAddress);
				request.start();
				String balance = Balance.getInstance().getBalance(request.getCorrelationId()); 
				
				logger.debug("balance: {} for {}", balance, tmaAddress);
				
				frame.getContentPane().removeAll();
				JLabel label = new JLabel("Balance for " + tmaAddress);
				label.setBounds(20, 104, 350, 14);
				frame.getContentPane().add(label);
				
				JLabel balanceLabel = new JLabel(balance);
				balanceLabel.setBounds(20, 144, 350, 14);
				frame.getContentPane().add(balanceLabel);
				frame.getContentPane().repaint();
			}
		});
		
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}



}
