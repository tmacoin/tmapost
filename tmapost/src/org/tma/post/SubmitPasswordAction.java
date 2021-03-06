/*******************************************************************************
 * Copyright � 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import org.tma.post.key.PasswordUtil;
import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

public class SubmitPasswordAction extends AbstractAction implements Caller {
	
	private static final long serialVersionUID = -5348678702516608164L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	
	private JFrame frame;
	private JPasswordField passwordField;
	private JLabel label;
	
	public SubmitPasswordAction(JFrame frame, JPasswordField passwordField) {
		putValue(NAME, "Submit");
		putValue(SHORT_DESCRIPTION, "Submit Passphrase");
		this.frame = frame;
		this.passwordField = passwordField;
	}
	
	public void actionPerformed(ActionEvent actionEvent) {
		String passphrase = StringUtil.trim(new String(passwordField.getPassword()));
		try {
			PasswordUtil passwordUtil = new PasswordUtil(this);
			if(!passwordUtil.loadKeys(passphrase)) {
				return;
			}
			frame.getContentPane().removeAll();
			JPanel form = new JPanel(new BorderLayout());
			label = new JLabel("Passphrase accepted, starting network");
			label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			label.setBorder(new EmptyBorder(5,5,5,5));
			form.add(label);
			frame.getContentPane().add(form, BorderLayout.NORTH);
			frame.revalidate();
			frame.getContentPane().repaint();
			StartNetwork.getInstance().start(this);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void log(String message) {
		if(label != null) {
			label.setText(message);
			MenuCreator.addMenu(frame);
			return;
		}
		JOptionPane.showMessageDialog(frame, message);
	}

}
