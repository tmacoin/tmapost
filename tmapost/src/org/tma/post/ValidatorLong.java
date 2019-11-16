/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.tma.util.StringUtil;

public class ValidatorLong implements DocumentListener {
	
	public static final Color VERY_LIGHT_RED = new Color(255,200,200);
	
	private JTextField tf;
	
	public ValidatorLong(JTextField tf) {
		this.tf = tf;
	}

	public void insertUpdate(DocumentEvent e) {
		validate();
	}

	public void removeUpdate(DocumentEvent e) {
		validate();
		
	}

	public void changedUpdate(DocumentEvent e) {
		validate();
		
	}
	
	private void validate() {
		String value = StringUtil.trim(tf.getText());
		try {
			Long.parseLong(value);
			tf.setBackground(Color.white);
		} catch (Exception e) {
			tf.setBackground(VERY_LIGHT_RED);
		}
	}

}
