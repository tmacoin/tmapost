/*******************************************************************************
 * Copyright � 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.tma.peer.thin.TwitterAccount;
import org.tma.post.Caller;
import org.tma.post.util.SwingUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class SubscribeAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4008418980341407814L;
	
	private JFrame frame;
	private TwitterAccount twitterAccount;
	
	public SubscribeAction(JFrame frame, TwitterAccount twitterAccount) {
		putValue(NAME, "Subscribe");
		putValue(SHORT_DESCRIPTION, "Subscribe");
		this.frame = frame;
		this.twitterAccount = twitterAccount;
	}

	public void actionPerformed(ActionEvent e) {
		
		SwingUtil.showWait(frame);
		
		ThreadExecutor.getInstance().execute(new TmaRunnable("Subscribe") {
			public void doRun() {
				String message = "Subscribed to account: " + twitterAccount.getName();
				save(twitterAccount);
				frame.getContentPane().removeAll();
				JPanel form = new JPanel(new BorderLayout());
				JLabel label = new JLabel(message);
				label.setBorder(new EmptyBorder(5,5,5,5));
				form.add(label);
				frame.getContentPane().add(form, BorderLayout.NORTH);
				frame.revalidate();
				frame.getContentPane().repaint();
			}
		});
	}

	private void save(TwitterAccount twitterAccount) {
		SubscriptionStore.getInstance().save(twitterAccount);
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

}
