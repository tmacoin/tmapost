/*******************************************************************************
 * Copyright � 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

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
import javax.swing.JTextField;

public class SendMessage extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;
	
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
		
		JPanel form = new JPanel(new BorderLayout());
		frame.getContentPane().add(form, BorderLayout.NORTH);
		
		JPanel labelPanel = new JPanel(new GridLayout(5, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(5, 1));
		form.add(labelPanel, BorderLayout.WEST);
		form.add(fieldPanel, BorderLayout.CENTER);
		
		JLabel label = new JLabel("Recipient:", JLabel.RIGHT);
		labelPanel.add(label);
		
		label = new JLabel("Fee in satoshis:", JLabel.RIGHT);
		labelPanel.add(label);
		
		label = new JLabel("Expire after # blocks:", JLabel.RIGHT);
		labelPanel.add(label);
		
		label = new JLabel("Subject:", JLabel.RIGHT);
		labelPanel.add(label);
		
		label = new JLabel("Body:", JLabel.RIGHT);
		labelPanel.add(label);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField address = new JTextField(36);
		address.getDocument().addDocumentListener(new ValidatorTmaAddress(address));
		JTextFieldRegularPopupMenu.addTo(address);
		p.add(address);
		fieldPanel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField fee = new JTextField(36);
		fee.getDocument().addDocumentListener(new ValidatorLong(fee));
		JTextFieldRegularPopupMenu.addTo(fee);
		p.add(fee);
		fieldPanel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField expire = new JTextField(36);
		expire.getDocument().addDocumentListener(new ValidatorLong(expire));
		JTextFieldRegularPopupMenu.addTo(expire);
		p.add(expire);
		fieldPanel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField subject = new JTextField(36);
		subject.setBounds(160, 101, 260, 20);
		JTextFieldRegularPopupMenu.addTo(subject);
		p.add(subject);
		fieldPanel.add(p);
		
		
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		JTextArea expiringData = new JTextArea();
		expiringData.setToolTipText("Limited to 32672 chars together with subject");
		JTextFieldRegularPopupMenu.addTo(expiringData);
		JScrollPane scroll = new JScrollPane (expiringData);
		p.add(scroll);
		
		p.add(Box.createRigidArea(new Dimension(0, 10)));

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new SendMessageAction(frame, address, fee, expire, subject, expiringData));
		p.add(btnSubmit);
		
		p.add(Box.createRigidArea(new Dimension(0, 10)));
		
		frame.getContentPane().add(p);

		frame.getRootPane().setDefaultButton(btnSubmit);
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
		address.grabFocus();
	}

	

}
