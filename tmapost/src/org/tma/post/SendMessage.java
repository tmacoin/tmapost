/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SendMessage extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;
	public static final Color VERY_LIGHT_RED = new Color(255,200,200);
	
	private JFrame frame;
	
	public SendMessage(JFrame frame) {
		putValue(NAME, "Send Message");
		putValue(SHORT_DESCRIPTION, "Send Message");
		this.frame = frame;
	}
	
	
	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		
		frame.getContentPane().removeAll();
		
		JLabel label = new JLabel("Recipient:");
		label.setBounds(20, 14, 160, 14);
		frame.getContentPane().add(label);
		
		JTextField address = new JTextField(36);
		address.setBounds(160, 11, 260, 20);
		address.getDocument().addDocumentListener(new ValidatorTmaAddress(address));
		JTextFieldRegularPopupMenu.addTo(address);
		frame.getContentPane().add(address);
		
		label = new JLabel("Fee in satoshis:");
		label.setBounds(20, 44, 160, 14);
		frame.getContentPane().add(label);
		
		JTextField fee = new JTextField(36);
		fee.setBounds(160, 41, 200, 20);
		fee.getDocument().addDocumentListener(new ValidatorLong(fee));
		JTextFieldRegularPopupMenu.addTo(fee);
		frame.getContentPane().add(fee);
		
		label = new JLabel("Expire after # blocks:");
		label.setBounds(20, 74, 160, 14);
		frame.getContentPane().add(label);
		
		JTextField expire = new JTextField(36);
		expire.setBounds(160, 71, 150, 20);
		expire.getDocument().addDocumentListener(new ValidatorLong(expire));
		JTextFieldRegularPopupMenu.addTo(expire);
		frame.getContentPane().add(expire);
		
		label = new JLabel("Subject:");
		label.setBounds(20, 104, 160, 14);
		frame.getContentPane().add(label);
		
		JTextField subject = new JTextField(36);
		subject.setBounds(160, 101, 260, 20);
		JTextFieldRegularPopupMenu.addTo(subject);
		frame.getContentPane().add(subject);
		
		label = new JLabel("Body:");
		label.setBounds(20, 134, 160, 14);
		frame.getContentPane().add(label);
		
		JTextArea expiringData = new JTextArea();
		expiringData.setToolTipText("Limited to 32672 chars together with subject");
		JTextFieldRegularPopupMenu.addTo(expiringData);
		JScrollPane scroll = new JScrollPane (expiringData);
		scroll.setBounds(160, 131, 260, 130);
		frame.getContentPane().add(scroll);
		
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new SendMessageAction(frame, address, fee, expire, subject, expiringData));
		btnSubmit.setBounds(269, 270, 150, 23);
		frame.getContentPane().add(btnSubmit);
		
		frame.getRootPane().setDefaultButton(btnSubmit);
		frame.setSize(450, 370);
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
		address.grabFocus();
	}

	

}
