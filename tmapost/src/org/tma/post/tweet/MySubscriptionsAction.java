/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.awt.event.ActionEvent;
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

import org.tma.peer.thin.TwitterAccount;
import org.tma.post.Caller;
import org.tma.post.persistance.TwitterStore;
import org.tma.post.util.SwingUtil;
import org.tma.post.util.TableColumnAdjuster;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class MySubscriptionsAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4008418980341407814L;
	
	private JFrame frame;
	
	public MySubscriptionsAction(JFrame frame) {
		putValue(NAME, "My Subscriptions");
		putValue(SHORT_DESCRIPTION, "My Subscriptions");
		this.frame = frame;
	}

	public void actionPerformed(ActionEvent e) {
		
		SwingUtil.showWait(frame);
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("My Subscriptions") {
			public void doRun() {

				List<TwitterAccount> list = TwitterStore.getInstance().selectAll();
				
				frame.getContentPane().removeAll();
				
				JPanel form = new JPanel();
				form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
				
				if(list.size() != 0) {
					JLabel label = new JLabel("My Subscriptions");
					label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
					label.setBorder(new EmptyBorder(5,5,5,5));
					form.add(label);
					
					TwitterAccountTableModel model = new TwitterAccountTableModel(list);
					JTable table = new JTable(model);

					table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					TableColumnAdjuster tca = new TableColumnAdjuster(table);
					tca.adjustColumns();
					table.addMouseListener(new TwitterAccountMouseAdapter(table, list, frame));

					JScrollPane scroll = new JScrollPane (table);
					scroll.setBorder(null);
					form.add(scroll);
				} else {
					JLabel label = new JLabel("No Subscriptions found");
					label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
					label.setBorder(new EmptyBorder(5,5,5,5));
					form.add(label);
				}
				
				
				
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
