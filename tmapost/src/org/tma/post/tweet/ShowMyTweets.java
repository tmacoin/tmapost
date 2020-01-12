/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import org.tma.blockchain.Wallet;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.GetMyTweetsRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.Tweet;
import org.tma.post.Caller;
import org.tma.post.Wallets;
import org.tma.post.util.SwingUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

import net.miginfocom.swing.MigLayout;

public class ShowMyTweets extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;

	private JFrame frame;
	private String tmaAddress;

	public ShowMyTweets(JFrame frame) {
		putValue(NAME, "Show my tweets");
		putValue(SHORT_DESCRIPTION, "Show my tweets");
		this.frame = frame;
		Wallets wallets = Wallets.getInstance();
		
		Collection<String> names = wallets.getNames(Wallets.TWITTER);
		if(!names.isEmpty()) {
			String accountName = names.iterator().next();
			Wallet twitterWallet = wallets.getWallet(Wallets.TWITTER, accountName);
			tmaAddress = twitterWallet.getTmaAddress();
		}
	}

	public ShowMyTweets(JFrame frame, String tmaAddress) {
		putValue(NAME, "Show tweets");
		putValue(SHORT_DESCRIPTION, "Show tweets");
		this.frame = frame;
		this.tmaAddress = tmaAddress;
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		frame.getContentPane().removeAll();
		Wallets wallets = Wallets.getInstance();
		Wallet twitterWallet = null;
		Collection<String> names = wallets.getNames(Wallets.TWITTER);
		if(!names.isEmpty()) {
			String accountName = names.iterator().next();
			twitterWallet = wallets.getWallet(Wallets.TWITTER, accountName);
		}

		if (twitterWallet == null) {
			JPanel form = new JPanel(new BorderLayout());

			JTextArea message = new JTextArea();
			message.setText("You have not created you twitter account yet");
			message.setLineWrap(true);
			message.setWrapStyleWord(true);
			message.setOpaque(false);
			message.setEditable(false);

			JScrollPane scroll = new JScrollPane(message);
			scroll.setBorder(null);
			form.add(scroll);

			form.add(message);
			frame.getContentPane().add(form, BorderLayout.CENTER);
			frame.getContentPane().revalidate();
			frame.getContentPane().repaint();
			return;
		}
		if(tmaAddress == null) {
			tmaAddress = twitterWallet.getTmaAddress();
		}

		final JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("Show MyTweets") {
			public void doRun() {
				Network network = Network.getInstance();
				if(!network.isPeerSetComplete()) {
					new BootstrapRequest(network).start();
				}
				
				GetMyTweetsRequest request = new GetMyTweetsRequest(network, tmaAddress);
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
				
				if(title != null) {
					twitterHelper.print(panel, title.getKeywords().getMap().get("create"));
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
				
				JScrollPane jScrollPane = new JScrollPane (panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				frame.getContentPane().add(jScrollPane);
				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();

			}
		});

	}
	

	

	


}
