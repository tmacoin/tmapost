/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.rating;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.tma.post.Caller;
import org.tma.post.util.JTextFieldRegularPopupMenu;

import net.miginfocom.swing.MigLayout;

public class FindRatee extends AbstractAction implements Caller {

	private static final long serialVersionUID = 4036313657721664495L;
	
	private JFrame frame;
	
	public FindRatee(JFrame frame) {
		putValue(NAME, "Find Ratee");
		putValue(SHORT_DESCRIPTION, "Find Ratee");
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
		JPanel form = new JPanel(new MigLayout(
		        "wrap 2",
		        "[right][fill]"
		        ));
		frame.getContentPane().add(form, BorderLayout.NORTH);
		
		form.add(new JLabel("Ratee:"));
		
		JTextField account = new JTextField(45);
		JTextFieldRegularPopupMenu.addTo(account);
		form.add(account);

		form.add(new JLabel("Keywords:"));
		
		JTextField keywords = new JTextField(45);
		JTextFieldRegularPopupMenu.addTo(keywords);
		form.add(keywords);
		
		form.add(new JLabel(""));
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new FindRateeAction(frame, account, keywords));
		p.add(btnSubmit);
		form.add(p);

		frame.getRootPane().setDefaultButton(btnSubmit);
	}
	

}
