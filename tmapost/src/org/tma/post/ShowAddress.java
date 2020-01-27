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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.tma.blockchain.Wallet;
import org.tma.peer.Network;
import org.tma.peer.thin.GetBalanceRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.post.util.JTextFieldRegularPopupMenu;
import org.tma.post.util.SwingUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

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
		
		final JLabel label = SwingUtil.showWait(frame);
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("ShowMessages") {
			public void doRun() {

				Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA, Wallets.WALLET_NAME);
				
				String balance = null;
				int i = 5;
				while (balance == null && i-- > 0) {
					SwingUtil.checkNetwork();

					GetBalanceRequest request = new GetBalanceRequest(Network.getInstance(), wallet.getTmaAddress());
					request.start();
					balance = (String) ResponseHolder.getInstance().getObject(request.getCorrelationId());
				}
				
				if(balance == null) {
					label.setText("Failed to retrieve balance. Please try again");
					return;
				}

				frame.getContentPane().removeAll();

				JPanel form = new JPanel(new BorderLayout());

				JPanel labelPanel = new JPanel(new GridLayout(2, 1));
				JPanel fieldPanel = new JPanel(new GridLayout(2, 1));
				form.add(labelPanel, BorderLayout.WEST);
				form.add(fieldPanel, BorderLayout.CENTER);

				JLabel label = new JLabel(
						"Your TMA Address on shard " + Network.getInstance().getBootstrapBlockchainId() + ":",
						JLabel.RIGHT);
				label.setBorder(new EmptyBorder(5, 5, 5, 5));
				labelPanel.add(label);

				JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
				JTextField textField = new JTextField(30);
				textField.setText(wallet.getTmaAddress());
				JTextFieldRegularPopupMenu.addTo(textField);
				p.add(textField);
				fieldPanel.add(p);

				

				label = new JLabel("Balance:", JLabel.RIGHT);
				label.setBorder(new EmptyBorder(5, 5, 5, 5));
				labelPanel.add(label);

				p = new JPanel(new FlowLayout(FlowLayout.LEFT));
				textField = new JTextField(30);
				textField.setText(balance + " coins");
				JTextFieldRegularPopupMenu.addTo(textField);
				p.add(textField);
				fieldPanel.add(p);

				frame.getContentPane().add(form, BorderLayout.NORTH);
				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();

			}
		});
	}

	

}
