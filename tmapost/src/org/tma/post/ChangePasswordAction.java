/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
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

public class ChangePasswordAction extends AbstractAction implements Caller {
	
	private static final long serialVersionUID = -5348678702516608164L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	
	private JFrame frame;
	private JPasswordField oldPasswordField;
	private JPasswordField newPasswordField;
	private JPasswordField confirmPasswordField;
	
	public ChangePasswordAction(JFrame frame, JPasswordField oldPasswordField, JPasswordField newPasswordField, JPasswordField confirmPasswordField) {
		putValue(NAME, "Submit");
		putValue(SHORT_DESCRIPTION, "Submit Passphrase");
		this.frame = frame;
		this.oldPasswordField = oldPasswordField;
		this.newPasswordField = newPasswordField;
		this.confirmPasswordField = confirmPasswordField;
	}
	
	public void actionPerformed(ActionEvent actionEvent) {
		String oldPassword = StringUtil.trim(new String(oldPasswordField.getPassword()));
		String newPassword = StringUtil.trim(new String(newPasswordField.getPassword()));
		String confirmPassword = StringUtil.trim(new String(confirmPasswordField.getPassword()));
		if(!newPassword.equals(confirmPassword)) {
			log("New and reentered new passwords do not match");
			return;
		}
		
		try {
			PasswordUtil passwordUtil = new PasswordUtil(this);
			if(!passwordUtil.loadKeys(oldPassword)) {
				return;
			}
			passwordUtil.saveKeys(newPassword);
			frame.getContentPane().removeAll();
			JPanel form = new JPanel(new BorderLayout());
			JLabel label = new JLabel("Passphrase Changed");
			label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			label.setBorder(new EmptyBorder(5,5,5,5));
			form.add(label);
			frame.getContentPane().add(form, BorderLayout.NORTH);
			frame.revalidate();
			frame.getContentPane().repaint();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}
}
