/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.BorderLayout;
import java.security.NoSuchAlgorithmException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.util.StringUtil;

public class SwingUtil {
	
	private static final Logger logger = LogManager.getLogger();
	
	public static JLabel showWait(JFrame frame) {
		frame.getContentPane().removeAll();
		JPanel form = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Please wait, processing.");
		form.add(label);
		frame.getContentPane().add(form, BorderLayout.NORTH);
		frame.revalidate();
		frame.getContentPane().repaint();
		return label;
	}
	
	public static int getShard(String input, int power) {
		if(power == 0) {
			return 0;
		}
		byte[] bytes = null;
		try {
			bytes = StringUtil.getBytesSha256(input);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
		String str = "";
		for(byte b: bytes) {
			str = str + Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		}
		str = str.substring(0, power);
		return Integer.valueOf(str, 2);
	}

}
