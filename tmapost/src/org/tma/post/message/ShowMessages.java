/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.message;

import java.awt.event.ActionEvent;
import java.security.PublicKey;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import org.tma.blockchain.Wallet;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.GetMessagesRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.SecureMessage;
import org.tma.post.Caller;
import org.tma.post.Wallets;
import org.tma.post.util.SwingUtil;
import org.tma.post.util.TableColumnAdjuster;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

public class ShowMessages extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	
	private JFrame frame;
	
	public ShowMessages(JFrame frame) {
		putValue(NAME, "Show Messages");
		putValue(SHORT_DESCRIPTION, "Show Messages");
		this.frame = frame;
	}
	
	
	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		
		final JLabel label = SwingUtil.showWait(frame);
		
		final Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA, Wallets.WALLET_NAME);
		final PublicKey publicKey = wallet.getPublicKey();
		ThreadExecutor.getInstance().execute(new TmaRunnable("ShowMessages") {
			public void doRun() {
				Network network = Network.getInstance();
				if(!network.isPeerSetComplete()) {
					new BootstrapRequest(network).start();
				}
				GetMessagesRequest request = new GetMessagesRequest(network, publicKey);
				request.start();
				@SuppressWarnings("unchecked")
				List<SecureMessage> list = (List<SecureMessage>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
				
				if(list == null) {
					label.setText("Failed to retrieve messages. Please try again");
					return;
				}
				
				String tmaAddress = wallet.getTmaAddress();
				logger.debug("found # of messages: {} for {}", list.size(), tmaAddress);
				
				frame.getContentPane().removeAll();
				
				JPanel form = new JPanel();
				form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
				
				JLabel label = new JLabel("Messages for " + tmaAddress);
				label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
				label.setBorder(new EmptyBorder(5,5,5,5));
				form.add(label);
				
				if(list.size() != 0) {
					SecureMessageTableModel model = new SecureMessageTableModel(list);
					JTable table = new JTable(model);

					table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					TableColumnAdjuster tca = new TableColumnAdjuster(table);
					tca.adjustColumns();
					table.addMouseListener(new MessageMouseAdapter(table, list, frame));

					JScrollPane scroll = new JScrollPane (table);
					scroll.setBorder(null);
					form.add(scroll);
				} else {
					label.setText("No messages found for " + tmaAddress);
				}
				
				
				frame.getContentPane().add(form);
				frame.revalidate();
				frame.getContentPane().repaint();
			}
		});
	}

	

}
