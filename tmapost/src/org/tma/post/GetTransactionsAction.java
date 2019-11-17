/*******************************************************************************
 * Copyright � 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.blockchain.Transaction;
import org.tma.peer.Network;
import org.tma.peer.thin.GetTransactionsRequest;
import org.tma.peer.thin.Transactions;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class GetTransactionsAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4798442956508802794L;
	private static final Logger logger = LogManager.getLogger();
	
	private JFrame frame;
	private JTextField jAddress;
	private String address;

	public GetTransactionsAction(JFrame frame, JTextField address) {
		putValue(NAME, "Get Transactions");
		putValue(SHORT_DESCRIPTION, "Get Transactions Action");
		this.frame = frame;
		this.jAddress = address;
	}

	public void actionPerformed(ActionEvent e) {
		address = StringUtil.trim(jAddress.getText());
		if(!StringUtil.isTmaAddressValid(address)) {
			JOptionPane.showMessageDialog(frame, "TMA Address is not valid");
			return;
		}
		
		frame.getContentPane().removeAll();
		
		JLabel label = new JLabel("Please wait, processing.");
		label.setBounds(20, 104, 350, 14);
		frame.getContentPane().add(label);
		frame.getContentPane().repaint();
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("GetTransactionsAction") {
			public void doRun() {
				GetTransactionsRequest request = new GetTransactionsRequest(Network.getInstance(), address);
				request.start();
				Set<Transaction> set = Transactions.getInstance().getTransactions(request.getCorrelationId()); 
				
				logger.debug("found # of transactions: {} for {}", set.size(), address);
				
				frame.getContentPane().removeAll();
				JLabel label = new JLabel("Transactions for " + address);
				label.setBounds(20, 20, 400, 14);
				frame.getContentPane().add(label);
				
				TransactionTableModel model = new TransactionTableModel(set);
				JTable table = new JTable(model);

				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				TableColumnAdjuster tca = new TableColumnAdjuster(table);
				tca.adjustColumns();
				//tca.setDynamicAdjustment(true);
				
				JScrollPane scroll = new JScrollPane (table);
				scroll.setBounds(20, 60, 1000, 200);
				frame.getContentPane().add(scroll);
				
				frame.setSize(1100, 400);
				frame.getContentPane().repaint();
			}
		});
		
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}



}