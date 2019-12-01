/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.tma.peer.Network;
import org.tma.peer.thin.GetMyTweetsRequest;
import org.tma.peer.thin.GetRepliesRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.Tweet;
import org.tma.peer.thin.TwitterAccount;
import org.tma.post.JTextFieldRegularPopupMenu;
import org.tma.post.SwingUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

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

	private void doit(TwitterAccount twitterAccount) {
		JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("Show MyTweets") {
			public void doRun() {
				GetMyTweetsRequest request = new GetMyTweetsRequest(Network.getInstance(), twitterAccount.getTmaAddress());
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
				
				JScrollPane jScrollPane = new JScrollPane (p, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				frame.getContentPane().add(jScrollPane);
				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();

			}
		});
	}
	
	private void print(JPanel panel, String str) {
		JTextArea area = new JTextArea();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setOpaque( false );
		area.setEditable( false );
		area.setText(str);
		panel.add(area);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
	}

	private void addTweet(JPanel panel, Tweet tweet) {
		String text = tweet.getSenderAddress() + " · " + new Date(tweet.getTimeStamp()).toString() + "\n" + tweet.getText();
		JTextArea area = new JTextArea();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setOpaque( false );
		area.setEditable( false );
		area.setText(text);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(area);
		p.add(Box.createRigidArea(new Dimension(0, 10)));
		area.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                displayTweet(tweet);
            }

        });
		panel.add(p);
	}
	
	private void displayTweet(Tweet tweet) {
		JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("ShowTweet") {
			public void doRun() {
				GetRepliesRequest request = new GetRepliesRequest(Network.getInstance(), tweet.getTransactionId(), tweet.getRecipient());
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
				
				JPanel p = showBackButton(tweet);
				panel.add(p);
				
				addTweet(p, tweet);
				
				for(Tweet tweet: list) {
					addTweet(p, tweet);
				}
				
				
				
				p = new JPanel(new FlowLayout(FlowLayout.LEFT));
				JScrollPane scroll = new JScrollPane (p);
				scroll.setBorder(null);
				panel.add(scroll);
				createForm(p, tweet);
				
				JScrollPane jScrollPane = new JScrollPane (panel);
				frame.getContentPane().add(jScrollPane);
				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();
			}
		});
	}
	
	private JPanel showBackButton(Tweet tweet) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JButton btnSubmit = new JButton();
		btnSubmit.setAction(new ShowMyTweets(frame, tweet.getRecipient()));
		btnSubmit.setText("Back");
		
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backButton");

		frame.getRootPane().getActionMap().put("backButton", new AbstractAction() {
			private static final long serialVersionUID = 4946947535624344910L;

			public void actionPerformed(ActionEvent actionEvent) {
				btnSubmit.doClick();
				frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).clear();
				frame.getRootPane().getActionMap().clear();
			}
		});
		
		p.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JPanel flow = new JPanel(new FlowLayout(FlowLayout.LEFT));
		flow.add(btnSubmit);
		p.add(flow);
		p.add(Box.createRigidArea(new Dimension(0, 10)));
		return p;
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

	public TwitterAccountMouseAdapter(JTable table, List<TwitterAccount> list, JFrame frame) {
		this.table = table;
		this.list = list;
		this.frame = frame;
	}

}
