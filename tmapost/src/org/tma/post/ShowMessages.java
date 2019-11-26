/*******************************************************************************
 * Copyright � 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.security.PublicKey;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.blockchain.SecureMessage;
import org.tma.blockchain.Wallet;
import org.tma.peer.Network;
import org.tma.peer.thin.GetMessagesRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class ShowMessages extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;
	private static final Logger logger = LogManager.getLogger();
	
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
		
		JLabel label = SwingUtil.showWait(frame);
		
		Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA);
		PublicKey publicKey = wallet.getPublicKey();
		ThreadExecutor.getInstance().execute(new TmaRunnable("ShowMessages") {
			public void doRun() {
				GetMessagesRequest request = new GetMessagesRequest(Network.getInstance(), publicKey);
				request.start();
				@SuppressWarnings("unchecked")
				List<SecureMessage> list = (List<SecureMessage>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
				
				if(list == null) {
					label.setText("Failed to retrieve transactions. Please try again");
					return;
				}
				
				String tmaAddress = wallet.getTmaAddress();
				logger.debug("found # of messages: {} for {}", list.size(), tmaAddress);
				
				frame.getContentPane().removeAll();
				
				JPanel form = new JPanel();
				form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
				
				JLabel label = new JLabel("Messages for " + tmaAddress);
				form.add(label);
				
				form.add(Box.createRigidArea(new Dimension(0, 20)));
				
				SecureMessageTableModel model = new SecureMessageTableModel(list);
				JTable table = new JTable(model);

				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				TableColumnAdjuster tca = new TableColumnAdjuster(table);
				tca.adjustColumns();
				table.addMouseListener(new MessageMouseAdapter(table, list, frame));

				JScrollPane scroll = new JScrollPane (table);
				scroll.setBorder(null);
				form.add(scroll);
				
				frame.getContentPane().add(form);
				//frame.pack();
				frame.revalidate();
				frame.getContentPane().repaint();
			}
		});
	}

	

}
