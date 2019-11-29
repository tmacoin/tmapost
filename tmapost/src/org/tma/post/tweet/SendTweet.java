/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.tweet;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.tma.post.Caller;
import org.tma.post.JTextFieldRegularPopupMenu;

public class SendTweet extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;
	
	private JFrame frame;
	
	public SendTweet(JFrame frame) {
		putValue(NAME, "Send Tweet");
		putValue(SHORT_DESCRIPTION, "Send Tweet");
		this.frame = frame;
	}
	
	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		frame.getContentPane().removeAll();

		createForm();
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}
	
	private void createForm() {
		JPanel form = new JPanel(new BorderLayout());
		frame.getContentPane().add(form, BorderLayout.NORTH);
		
		JPanel labelPanel = new JPanel(new GridLayout(2, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(2, 1));
		form.add(labelPanel, BorderLayout.WEST);
		form.add(fieldPanel, BorderLayout.CENTER);
		
		
		JLabel label = new JLabel("Enter tweet:", JLabel.RIGHT);
		labelPanel.add(label);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextArea tweet = new JTextArea(3, 45);
		JTextFieldRegularPopupMenu.addTo(tweet);
		JScrollPane scroll = new JScrollPane (tweet);
		p.add(scroll);
		fieldPanel.add(p);
		
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new SendTweetAction(frame, tweet));
		p.add(btnSubmit);
		fieldPanel.add(p);
		
		frame.getContentPane().add(p);

		frame.getRootPane().setDefaultButton(btnSubmit);
	}
	

}
