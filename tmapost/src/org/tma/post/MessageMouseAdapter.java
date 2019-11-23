/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.blockchain.SecureMessage;
import org.tma.blockchain.Wallet;
import org.tma.persistance.Encryptor;
import org.tma.util.Base58;
import org.tma.util.StringUtil;

public class MessageMouseAdapter extends MouseAdapter {
	
	private static final Logger logger = LogManager.getLogger();
	private static final Encryptor encryptor = new Encryptor();
	
	private JTable table;
	private List<SecureMessage> list;
	private JFrame frame;
	
	public void mouseClicked(MouseEvent evt) {
        int row = table.rowAtPoint(evt.getPoint());
        int col = table.columnAtPoint(evt.getPoint());
        if (row >= 0 && col >= 0) {
            doit(list.get(row));
        }
    }

	private void doit(SecureMessage secureMessage) {
		frame.getContentPane().removeAll();
		
		JPanel form = new JPanel(new BorderLayout());
		frame.getContentPane().add(form, BorderLayout.NORTH);
		
		JPanel labelPanel = new JPanel(new GridLayout(7, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(7, 1));
		form.add(labelPanel, BorderLayout.WEST);
		form.add(fieldPanel, BorderLayout.CENTER);
		
		JLabel label = new JLabel("Sender:", JLabel.RIGHT);
		labelPanel.add(label);

		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField address = new JTextField(36);
		address.setText(StringUtil.getStringFromKey(secureMessage.getSender()));
		address.setBorder( null );
		address.setOpaque( false );
		address.setEditable( false );
		p.add(address);
		fieldPanel.add(p);
		
		label = new JLabel("Recipient:", JLabel.RIGHT);
		labelPanel.add(label);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField recipient = new JTextField(36);
		recipient.setText(secureMessage.getRecipient());
		recipient.setBorder( null );
		recipient.setOpaque( false );
		recipient.setEditable( false );
		p.add(recipient);
		fieldPanel.add(p);
		
		
		label = new JLabel("Value:", JLabel.RIGHT);
		labelPanel.add(label);

		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField amount = new JTextField(36);
		amount.setText(secureMessage.getValue().toNumberOfCoins());
		amount.setBorder( null );
		amount.setOpaque( false );
		amount.setEditable( false );
		p.add(amount);
		fieldPanel.add(p);
		
		label = new JLabel("Fee:", JLabel.RIGHT);
		labelPanel.add(label);
				
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField fee = new JTextField(36);
		fee.setText(secureMessage.getFee().toNumberOfCoins());
		fee.setBorder( null );
		fee.setOpaque( false );
		fee.setEditable( false );
		p.add(fee);
		fieldPanel.add(p);
		

		label = new JLabel("Date:", JLabel.RIGHT);
		labelPanel.add(label);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField date = new JTextField(36);
		date.setText(new Date(secureMessage.getTimeStamp()).toString());
		date.setBorder( null );
		date.setOpaque( false );
		date.setEditable( false );
		p.add(date);
		fieldPanel.add(p);
		
		
		label = new JLabel("Subject:", JLabel.RIGHT);
		labelPanel.add(label);

		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField subject = new JTextField(36);
		subject.setBorder( null );
		subject.setOpaque( false );
		subject.setEditable( false );
		p.add(subject);
		fieldPanel.add(p);

		label = new JLabel("Body:", JLabel.RIGHT);
		labelPanel.add(label);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		frame.getContentPane().add(p);

		JTextArea expiringData = new JTextArea();
		expiringData.setLineWrap(true);
		expiringData.setWrapStyleWord(true);
		expiringData.setOpaque( false );
		expiringData.setEditable( false );
		
		
		JScrollPane scroll = new JScrollPane (expiringData);
		scroll.setBorder(null);
		p.add(scroll);
		
		try {
			String str = StringUtil.trimToNull(secureMessage.getText());
			Wallet wallet = Wallets.getInstance().getWallets().get(0);
			if(str != null && secureMessage.getRecipient().equals(wallet.getTmaAddress())) {
				str = new String(encryptor.decryptAsymm(Base58.decode(str), wallet.getPrivateKey()), StandardCharsets.UTF_8);
				int index = str.indexOf("\n");
				index = index == -1? str.length(): index;
				subject.setText(str.substring(0, index));
				expiringData.setText(str.substring(index + 1));
			}
		} catch (IOException | GeneralSecurityException e) {
			logger.error(e.getMessage(), e);
		}

		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		expiringData.setCaretPosition(0);
	}

	public MessageMouseAdapter(JTable table, List<SecureMessage> list, JFrame frame) {
		this.table = table;
		this.list = list;
		this.frame = frame;
	}

}
