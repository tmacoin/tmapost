/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
import javax.swing.JTextField;

import org.tma.peer.Network;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.SearchTwitterRequest;
import org.tma.peer.thin.TwitterAccount;
import org.tma.post.Caller;
import org.tma.post.SwingUtil;
import org.tma.post.TableColumnAdjuster;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class SearchTwitterAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4008418980341407814L;
	
	private JFrame frame;
	private JTextField account;
	
	public SearchTwitterAction(JFrame frame, JTextField account) {
		putValue(NAME, "Search Twitter Accounts");
		putValue(SHORT_DESCRIPTION, "Search Twitter Accounts");
		this.frame = frame;
		this.account = account;
	}

	public void actionPerformed(ActionEvent e) {
		
		JLabel label = SwingUtil.showWait(frame);
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("SearchTwitterAction") {
			public void doRun() {
				SearchTwitterRequest request = new SearchTwitterRequest(Network.getInstance(), account.getText());
				request.start();
				@SuppressWarnings("unchecked")
				List<TwitterAccount> list = (List<TwitterAccount>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
				
				if(list == null) {
					label.setText("Failed to retrieve twitter accounts. Please try again");
					return;
				}
				
				frame.getContentPane().removeAll();
				
				JPanel form = new JPanel();
				form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
				
				JLabel label = new JLabel("Twitter account with name " + account.getText());
				form.add(label);
				
				form.add(Box.createRigidArea(new Dimension(0, 20)));
				
				TwitterAccountTableModel model = new TwitterAccountTableModel(list);
				JTable table = new JTable(model);

				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				TableColumnAdjuster tca = new TableColumnAdjuster(table);
				tca.adjustColumns();
				table.addMouseListener(new TwitterAccountMouseAdapter(table, list, frame));

				JScrollPane scroll = new JScrollPane (table);
				scroll.setBorder(null);
				form.add(scroll);
				
				frame.getContentPane().add(form);
				
				frame.revalidate();
				frame.getContentPane().repaint();
			}
		});
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

}
