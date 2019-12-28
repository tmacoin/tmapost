/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.GetBalanceRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.post.util.SwingUtil;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class GetBalanceAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4798442956508802794L;
	private static final Logger logger = LogManager.getLogger();
	
	private JFrame frame;
	private JTextField jAddress;
	private String address;

	public GetBalanceAction(JFrame frame, JTextField address) {
		putValue(NAME, "Get Balance");
		putValue(SHORT_DESCRIPTION, "Get Balance Action");
		this.frame = frame;
		this.jAddress = address;
	}

	public void actionPerformed(ActionEvent e) {
		address = StringUtil.trim(jAddress.getText());
		if(!StringUtil.isTmaAddressValid(address)) {
			JOptionPane.showMessageDialog(frame, "TMA Address is not valid");
			return;
		}
		
		SwingUtil.showWait(frame);
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("GetBalanceAction") {
			public void doRun() {
				Network network = Network.getInstance();
				if(!network.isPeerSetComplete()) {
					new BootstrapRequest(network).start();
				}
				GetBalanceRequest request = new GetBalanceRequest(Network.getInstance(), address);
				request.start();
				String balance = (String)ResponseHolder.getInstance().getObject(request.getCorrelationId()); 
				
				logger.debug("balance: {} for {}", balance, address);
				
				frame.getContentPane().removeAll();
				JPanel form = new JPanel(new BorderLayout());
				
				JPanel labelPanel = new JPanel(new GridLayout(2, 1));
				form.add(labelPanel, BorderLayout.WEST);
				
				JLabel label = new JLabel("Balance for " + address + " is");
				labelPanel.add(label);
				label = new JLabel(balance + " coins");
				labelPanel.add(label);
				frame.getContentPane().add(form, BorderLayout.NORTH);
				frame.revalidate();
				frame.getContentPane().repaint();
				
			}
		});
		
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}



}
