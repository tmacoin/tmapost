/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.rating;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.blockchain.Keywords;
import org.tma.blockchain.Transaction;
import org.tma.blockchain.TransactionOutput;
import org.tma.blockchain.Wallet;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.SendTransactionRequest;
import org.tma.peer.thin.GetInputsRequest;
import org.tma.peer.thin.GetKeywordsRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.post.Caller;
import org.tma.post.Wallets;
import org.tma.post.util.JTextFieldRegularPopupMenu;
import org.tma.post.util.SwingUtil;
import org.tma.util.Applications;
import org.tma.util.Coin;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

import com.jidesoft.swing.AutoResizingTextArea;

import net.miginfocom.swing.MigLayout;

public class AddRatingAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = -6540143195727094951L;
	private static final Logger logger = LogManager.getLogger();
	
	private JFrame frame;
	private JTextArea comment;
	private ButtonGroup bgroup;
	private JTextField account;
	private JTextField transactionId;
	
	public AddRatingAction(JFrame frame, JTextArea comment, ButtonGroup bgroup, JTextField account, JTextField transactionId) {
		putValue(NAME, "Add Rating");
		putValue(SHORT_DESCRIPTION, "Add Rating");
		this.frame = frame;
		this.comment = comment;
		this.bgroup = bgroup;
		this.account = account;
		this.transactionId = transactionId;
		
	}

	public void actionPerformed(ActionEvent e) {
		
		if(SwingUtil.getSelectedButtonText(bgroup) == null) {
			log("Please click on Yes or No radio button for rating.");
			return;
		}
		
		SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("AddRatingAction") {
			public void doRun() {
				logger.debug("comment: {}", comment.getText());
				logger.debug("selected button: {}", SwingUtil.getSelectedButtonText(bgroup));
				doIt();
				feedback();
				
			}

			
		});
	}

	private void feedback() {
		frame.getContentPane().removeAll();

		JPanel form = new JPanel(new MigLayout(
		        "wrap 2",
		        "[right][fill]"
		        ));
		JScrollPane scrollPane = new JScrollPane(form, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);
		
		String rating = "Yes".equals(SwingUtil.getSelectedButtonText(bgroup)) ? "Positive": "Negative";
		form.add(new JLabel(rating + " rating was added"), "span, left");
		
		form.add(new JLabel("Ratee:"));
		
		JTextField account = new JTextField(45);
		account.setText(this.account.getText());
		account.setOpaque(false);
		account.setEditable(false);
		account.setBorder(null);
		JTextFieldRegularPopupMenu.addTo(account);
		form.add(account);
		
		form.add(new JLabel("Comment:"));
		
		JTextArea description = new AutoResizingTextArea(5, 40, 45);
		description.setText(comment.getText());
		
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setOpaque(false);
		description.setEditable(false);
		description.revalidate();
		
		form.add(description);
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
	}

	private void doIt() {
		String accountName = account.getText().trim();
		String ratee = StringUtil.getTmaAddressFromString(accountName);
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		
		GetKeywordsRequest getKeywordsRequest = new GetKeywordsRequest(network, ratee, transactionId.getText());
		getKeywordsRequest.start();
		
		Keywords accountKeywords = (Keywords)ResponseHolder.getInstance().getObject(getKeywordsRequest.getCorrelationId());
		
		
		String tmaAddress = network.getTmaAddress();
		Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA);
		Coin amount = Coin.SATOSHI.multiply(2);
		List<Coin> totals = new ArrayList<Coin>();
		totals.add(amount);
		for(String word: accountKeywords.getMap().keySet()) {
			if(word.equals(accountKeywords.getMap().get(word))) {
				totals.add(amount);
			}
		}
		GetInputsRequest request = new GetInputsRequest(Network.getInstance(), tmaAddress, totals);
		request.start();
		@SuppressWarnings("unchecked")
		List<Set<TransactionOutput>> inputList = (List<Set<TransactionOutput>>)ResponseHolder.getInstance().getObject(request.getCorrelationId());
		int i = 0;
		
		Keywords keywords = new Keywords();
		keywords.getMap().put("rater", wallet.getTmaAddress());
		keywords.getMap().put("ratee", accountName);
		keywords.getMap().put("transactionId", transactionId.getText());
		keywords.getMap().put("rating", SwingUtil.getSelectedButtonText(bgroup));
	
		Transaction transaction = new Transaction(wallet.getPublicKey(), ratee, Coin.SATOSHI, Coin.SATOSHI, 
				inputList.get(i++), wallet.getPrivateKey(), comment.getText().trim(), null, keywords);
		transaction.setApp(Applications.RATING);
		new SendTransactionRequest(network, transaction).start();
		logger.debug("sent {}", transaction);
		
		Map<String, String> map = new HashMap<String, String>(keywords.getMap());
		map.remove("rater");
		for(String word: accountKeywords.getMap().keySet()) {
			if(word.equals(accountKeywords.getMap().get(word))) {
				Keywords words = new Keywords();
				words.getMap().putAll(map);
				transaction = new Transaction(wallet.getPublicKey(), StringUtil.getTmaAddressFromString(word), Coin.SATOSHI, Coin.SATOSHI, 
						inputList.get(i++), wallet.getPrivateKey(), comment.getText().trim(), null, words);
				transaction.setApp(Applications.RATING);
				new SendTransactionRequest(network, transaction).start();
				logger.debug("sent {}", transaction);
			}
		}
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

}
