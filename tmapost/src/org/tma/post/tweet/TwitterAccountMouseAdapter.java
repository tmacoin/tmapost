/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;

import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.GetMyTweetsRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.Tweet;
import org.tma.peer.thin.TwitterAccount;
import org.tma.post.util.SwingUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

import net.miginfocom.swing.MigLayout;

public class TwitterAccountMouseAdapter extends MouseAdapter {
	
	private JTable table;
	private List<TwitterAccount> list;
	private JFrame frame;
	
	public void mouseClicked(MouseEvent evt) {
        int row = table.rowAtPoint(evt.getPoint());
        int col = table.columnAtPoint(evt.getPoint());
        if (row >= 0 && col >= 0) {
            doit(list.get(row));
        }
    }

	private void doit(final TwitterAccount twitterAccount) {
		final JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("TwitterAccountMouseAdapter") {
			public void doRun() {
				Network network = Network.getInstance();
				if(!network.isPeerSetComplete()) {
					new BootstrapRequest(network).start();
				}
				GetMyTweetsRequest request = new GetMyTweetsRequest(network, twitterAccount.getTmaAddress());
				request.start();
				@SuppressWarnings("unchecked")
				List<Tweet> list = (List<Tweet>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
				
				if(list == null) {
					label.setText("Failed to retrieve transactions. Please try again");
					return;
				}
				
				frame.getContentPane().removeAll();
				JPanel panel = new JPanel(new MigLayout("wrap 1", "[right][fill]"));

				Tweet title = null;
				for(Tweet tweet: list) {
					if(tweet.getKeywords() != null && tweet.getKeywords().getMap().get("create") != null) {
						title = tweet;
					}
				}
				
				TwitterHelper twitterHelper = new TwitterHelper(frame);
				List<TwitterAccount> subscribedAccounts = SubscriptionStore.getInstance().getSubscriptions();
				
				if(title != null) {
					
					
					if(!subscribedAccounts.contains(twitterAccount)) {
						JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
						JButton btnSubmit = new JButton("Submit");
						twitterAccount.setDescription(title.getText());
						
						btnSubmit.setAction(new SubscribeAction(frame, twitterAccount));
						buttonPanel.add(btnSubmit);
						panel.add(buttonPanel);
					}

					twitterHelper.print(panel, title.getText());
				}

				Iterator<Tweet> i = list.iterator();
				 
				while(i.hasNext()) {
					Tweet t = i.next();
				    if (t.getKeywords() != null && (t.getKeywords().getMap().get("create") != null || t.getKeywords().getMap().get("transactionId") != null)) {
				        i.remove();
				    }
				}
				
				twitterHelper.print(panel, "Retrieved number of tweets " + list.size());
				
				panel.add(new JSeparator(), "growx, span");
				
				Comparator<Tweet> compareByTimestamp = new Comparator<Tweet>() {
					
					@Override
					public int compare(Tweet o1, Tweet o2) {
						return Long.valueOf(o2.getTimeStamp()).compareTo( o1.getTimeStamp() );
					}
				};
						
				Collections.sort(list, compareByTimestamp);
				
				for(Tweet tweet: list) {
					twitterHelper.addTweet(panel, tweet);
				}
				
				if(subscribedAccounts.contains(twitterAccount)) {
					JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
					JButton btnSubmit = new JButton("Submit");
					btnSubmit.setAction(new UnSubscribeAction(frame, twitterAccount));
					buttonPanel.add(btnSubmit);
					panel.add(buttonPanel);
				}
				
				JScrollPane jScrollPane = new JScrollPane (panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				frame.getContentPane().add(jScrollPane);
				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();

			}
		});
	}
	
	public TwitterAccountMouseAdapter(JTable table, List<TwitterAccount> list, JFrame frame) {
		this.table = table;
		this.list = list;
		this.frame = frame;
	}

}
