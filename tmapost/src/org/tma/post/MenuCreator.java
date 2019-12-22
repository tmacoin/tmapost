/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
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
import org.tma.post.rating.CreateRatee;
import org.tma.post.rating.FindRatee;
import org.tma.post.tweet.CreateTwitter;
import org.tma.post.tweet.MySubscriptionsAction;
import org.tma.post.tweet.SearchTwitter;
import org.tma.post.tweet.SendTweet;
import org.tma.post.tweet.ShowMyTweets;

public class MenuCreator {
	
	public static void addMenu(JFrame frame) {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		addMenuFile(menuBar);
		addMenuTools(menuBar, frame);
		addMenuMessaging(menuBar, frame);
		addMenuTwitter(menuBar, frame);
		addMenuRating(menuBar, frame);
		menuBar.updateUI();
	}
	
	private static void addMenuRating(JMenuBar menuBar, JFrame frame) {
		JMenu menu = new JMenu("Posting");
		menuBar.add(menu);
		
		JMenuItem menuItem = new JMenuItem("Create Post");
		menuItem.setAction(new CreateRatee(frame));
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Find Post");
		menuItem.setAction(new FindRatee(frame));
		menu.add(menuItem);
		
	}
	
	private static void addMenuFile(JMenuBar menuBar) {
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAction(new ExitAction());
		mnFile.add(mntmExit);
	}
	
	private static void addMenuTools(JMenuBar menuBar, JFrame frame) {
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
	}
	
	private static void addMenuMessaging(JMenuBar menuBar, JFrame frame) {
		JMenu mnMessaging = new JMenu("Messaging");
		menuBar.add(mnMessaging);
		
		JMenuItem mntmSendMessage = new JMenuItem("Send Secure Message");
		mntmSendMessage.setAction(new SendMessage(frame));
		mnMessaging.add(mntmSendMessage);
		
		JMenuItem mntmShowMessages = new JMenuItem("Show Messages");
		mntmShowMessages.setAction(new ShowMessages(frame));
		mnMessaging.add(mntmShowMessages);
	}
	
	private static void addMenuTwitter(JMenuBar menuBar, JFrame frame) {
		JMenu mnTwitter = new JMenu("Twitter");
		menuBar.add(mnTwitter);
		
		JMenuItem mntmCreateTwitter = new JMenuItem("Create Twitter");
		mntmCreateTwitter.setAction(new CreateTwitter(frame));
		mnTwitter.add(mntmCreateTwitter);
		
		JMenuItem mntmShowMyTweets = new JMenuItem("My Tweets");
		mntmShowMyTweets.setAction(new ShowMyTweets(frame));
		mnTwitter.add(mntmShowMyTweets);
		
		JMenuItem mntmSendTweet = new JMenuItem("Send Tweet");
		mntmSendTweet.setAction(new SendTweet(frame));
		mnTwitter.add(mntmSendTweet);
		
		JMenuItem mntmSearchTwitter = new JMenuItem("Search Twitter");
		mntmSearchTwitter.setAction(new SearchTwitter(frame));
		mnTwitter.add(mntmSearchTwitter);
		
		JMenuItem mntmMySubscriptions = new JMenuItem("My Subscriptions");
		mntmMySubscriptions.setAction(new MySubscriptionsAction(frame));
		mnTwitter.add(mntmMySubscriptions);
	}
	
	

}
