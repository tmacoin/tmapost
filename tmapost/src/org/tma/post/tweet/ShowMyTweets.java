/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.tma.blockchain.Wallet;
import org.tma.peer.Network;
import org.tma.peer.thin.GetMyTweetsRequest;
import org.tma.peer.thin.GetRepliesRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.Tweet;
import org.tma.post.Caller;
import org.tma.post.JTextFieldRegularPopupMenu;
import org.tma.post.SwingUtil;
import org.tma.post.Wallets;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class ShowMyTweets extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;

	private JFrame frame;

	public ShowMyTweets(JFrame frame) {
		putValue(NAME, "Show my tweets");
		putValue(SHORT_DESCRIPTION, "Show my tweets");
		this.frame = frame;
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		frame.getContentPane().removeAll();
		Wallets wallets = Wallets.getInstance();
		Wallet twitterWallet = wallets.getWalletStartsWith(Wallets.TWITTER + "-");

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

		JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("ShowMyTweets") {
			public void doRun() {
				GetMyTweetsRequest request = new GetMyTweetsRequest(Network.getInstance(), twitterWallet.getTmaAddress());
				request.start();
				@SuppressWarnings("unchecked")
				List<Tweet> list = (List<Tweet>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
				
				if(list == null) {
					label.setText("Failed to retrieve transactions. Please try again");
					return;
				}
				
				frame.getContentPane().removeAll();
				JPanel p = new JPanel();
				p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

				Tweet title = null;
				for(Tweet tweet: list) {
					if(tweet.getKeywords() != null && !tweet.getKeywords().isEmpty() && tweet.getKeywords().contains("create")) {
						title = tweet;
					}
				}
				if(title != null) {
					print(p, title.getText());
				}
				
				list.removeIf(t -> t.getKeywords() != null && !t.getKeywords().isEmpty());
				
				print(p, "Retrieved number of tweets " + list.size());
				
				Comparator<Tweet> compareByTimestamp = (Tweet o1, Tweet o2) -> Long.valueOf(o2.getTimeStamp()).compareTo( o1.getTimeStamp() );
				Collections.sort(list, compareByTimestamp);
				
				for(Tweet tweet: list) {
					addTweet(p, tweet);
				}
				
				frame.getContentPane().add(p);

				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();

			}
		});

	}
	
	private void print(JPanel p, String str) {
		JLabel label = new JLabel();
		label.setText("<html>" + str + "</html>");
		p.add(label);
		p.add(Box.createRigidArea(new Dimension(0, 10)));
		label.setAlignmentX( Component.LEFT_ALIGNMENT );
	}

	private void addTweet(JPanel p, Tweet tweet) {
		String text = new Date(tweet.getTimeStamp()).toString() + "<BR/>" + tweet.getText();
		JLabel label = new JLabel();
		label.setText("<html>" + text + "</html>");
		p.add(label);
		p.add(Box.createRigidArea(new Dimension(0, 10)));
		label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                displayTweet(tweet);
            }

        });
		label.setAlignmentX( Component.LEFT_ALIGNMENT );
	}

	private void displayTweet(Tweet tweet) {
		JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("ShowTweet") {
			public void doRun() {
				Wallets wallets = Wallets.getInstance();
				Wallet twitterWallet = wallets.getWalletStartsWith(Wallets.TWITTER + "-");
				GetRepliesRequest request = new GetRepliesRequest(Network.getInstance(), tweet.getTransactionId(), twitterWallet.getTmaAddress());
				request.start();
				@SuppressWarnings("unchecked")
				List<Tweet> list = (List<Tweet>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
				
				if(list == null) {
					label.setText("Failed to retrieve transactions. Please try again");
					return;
				}
				
				frame.getContentPane().removeAll();
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				
				addTweet(panel, tweet);
				
				for(Tweet tweet: list) {
					addTweet(panel, tweet);
				}
				
				frame.getContentPane().add(panel);
				
				JPanel p = new JPanel();
				JScrollPane scroll = new JScrollPane (p);
				scroll.setBorder(null);
				panel.add(scroll);
				createForm(p, tweet);

				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();
			}
		});
	}
	
	private void createForm(JPanel panel, Tweet tweet) {
		JPanel form = new JPanel(new BorderLayout());
		panel.add(form, BorderLayout.NORTH);

		
		JPanel labelPanel = new JPanel(new GridLayout(2, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(2, 1));
		form.add(labelPanel, BorderLayout.WEST);
		form.add(fieldPanel, BorderLayout.CENTER);
		
		
		JLabel label = new JLabel("Enter reply:", JLabel.RIGHT);
		labelPanel.add(label);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextArea area = new JTextArea(3, 45);
		JTextFieldRegularPopupMenu.addTo(area);
		JScrollPane scroll = new JScrollPane (area);
		p.add(scroll);
		fieldPanel.add(p);
		
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new SendReplyAction(frame, area, tweet));
		p.add(btnSubmit);
		fieldPanel.add(p);

		frame.getRootPane().setDefaultButton(btnSubmit);
	}

}
