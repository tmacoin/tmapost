/*******************************************************************************
 * Copyright � 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.tma.post.message.SendMessage;
import org.tma.post.message.ShowMessages;
import org.tma.post.tweet.CreateTwitter;

public class MenuCreator {
	
	public static void addMenu(JFrame frame) {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAction(new ExitAction());
		mnFile.add(mntmExit);
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		JMenuItem mntmChangePassword = new JMenuItem("Change Password");
		mntmChangePassword.setAction(new ChangePassword(frame));
		mnTools.add(mntmChangePassword);
		
		JMenuItem mntmGetBalance = new JMenuItem("Get Balance");
		mntmGetBalance.setAction(new GetBalance(frame));
		mnTools.add(mntmGetBalance);
		
		JMenuItem mntmSendTransaction = new JMenuItem("Send Transaction");
		mntmSendTransaction.setAction(new SendTransaction(frame));
		mnTools.add(mntmSendTransaction);

		JMenuItem mntmShowAddress = new JMenuItem("Show Address");
		mntmShowAddress.setAction(new ShowAddress(frame));
		mnTools.add(mntmShowAddress);
		
		JMenu mnMessaging = new JMenu("Messaging");
		menuBar.add(mnMessaging);
		
		JMenuItem mntmSendMessage = new JMenuItem("Send Message");
		mntmSendMessage.setAction(new SendMessage(frame));
		mnMessaging.add(mntmSendMessage);
		
		JMenuItem mntmShowMessages = new JMenuItem("Show Messages");
		mntmShowMessages.setAction(new ShowMessages(frame));
		mnMessaging.add(mntmShowMessages);
		
		JMenu mnTwitter = new JMenu("Twitter");
		menuBar.add(mnTwitter);
		
		JMenuItem mntmCreateTwitter = new JMenuItem("Create Twitter");
		mntmCreateTwitter.setAction(new CreateTwitter(frame));
		mnTwitter.add(mntmCreateTwitter);
		
		menuBar.updateUI();
	}

}
