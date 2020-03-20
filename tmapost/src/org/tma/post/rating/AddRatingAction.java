/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.rating;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
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
import javax.swing.SwingUtilities;

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
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

import com.jidesoft.swing.AutoResizingTextArea;

import net.miginfocom.swing.MigLayout;

public class AddRatingAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = -6540143195727094951L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	
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
		if(StringUtil.isEmpty(comment.getText())) {
			log("Please enter comment.");
			return;
		}
		
		final JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("AddRatingAction") {
			public void doRun() {
				logger.debug("comment: {}", comment.getText());
				logger.debug("selected button: {}", SwingUtil.getSelectedButtonText(bgroup));
				if(doIt(label)) {
					feedback();
				}
			}

			
		});
	}

	private void feedback() {
		frame.getContentPane().removeAll();

		JPanel form = new JPanel(new MigLayout("wrap 2", "[right][fill]"));
		JScrollPane scrollPane = new JScrollPane(form, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);
		
		String rating = "Yes".equals(SwingUtil.getSelectedButtonText(bgroup)) ? "Positive": "Negative";
		form.add(new JLabel(rating + " rating was added"), "span, left");
		
		form.add(new JLabel("Post:"));
		
		JTextField ratee = new JTextField(45);
		ratee.setText(account.getText());
		ratee.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		ratee.setOpaque(false);
		ratee.setEditable(false);
		ratee.setBorder(null);
		ratee.setForeground(Color.BLUE);
		JTextFieldRegularPopupMenu.addTo(ratee);
		Map<TextAttribute, Integer> attributes = new HashMap<>();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		ratee.setFont(ratee.getFont().deriveFont(attributes));
		ratee.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
            	if(SwingUtilities.isLeftMouseButton(e)) {
            		new RatingHelper(frame).showRatee(account.getText(), transactionId.getText());
            	}
                
            }
        });
		form.add(ratee);
		
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

	private boolean doIt(JLabel label) {
		String accountName = account.getText().trim();
		String ratee = StringUtil.getTmaAddressFromString(accountName);
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			BootstrapRequest.getInstance().start();
		}
		
		GetKeywordsRequest getKeywordsRequest = new GetKeywordsRequest(network, ratee, transactionId.getText());
		getKeywordsRequest.start();
		
		Keywords accountKeywords = (Keywords)ResponseHolder.getInstance().getObject(getKeywordsRequest.getCorrelationId());
		
		if(accountKeywords == null || accountKeywords.isEmpty()) {
			label.setText("Could not retrieve any keywords for " + accountName);
			return false;
		}
		
		
		String tmaAddress = network.getTmaAddress();
		Wallet wallet = Wallets.getInstance().getWallet(Wallets.TMA, Wallets.WALLET_NAME);
		Coin amount = Coin.SATOSHI.multiply(2);
		List<Coin> totals = new ArrayList<Coin>();
		totals.add(amount);
		for(String word: accountKeywords.keySet()) {
			if(word.equals(accountKeywords.get(word))) {
				totals.add(amount);
			}
		}
		List<Set<TransactionOutput>> inputList = new GetInputsRequest(network, tmaAddress, totals).getInputlist();
		int i = 0;
		
		if(inputList.size() != totals.size()) {
			label.setText("No inputs available for tma address " + tmaAddress + ". Please check your balance.");
			return false;
		}
		
		Keywords keywords = new Keywords();
		keywords.put("rater", wallet.getTmaAddress());
		keywords.put("ratee", accountName);
		keywords.put("transactionId", transactionId.getText());
		keywords.put("rating", SwingUtil.getSelectedButtonText(bgroup));
	
		Transaction transaction = new Transaction(wallet.getPublicKey(), ratee, Coin.SATOSHI, Coin.SATOSHI, 
				inputList.get(i++), wallet.getPrivateKey(), comment.getText().trim(), null, keywords);
		transaction.setApp(Applications.RATING);
		new SendTransactionRequest(network, transaction).start();
		logger.debug("sent {}", transaction);
		
		for(String word: accountKeywords.keySet()) {
			if(word.equals(accountKeywords.get(word))) {
				Keywords words = keywords.copy();
				words.remove("rater");
				transaction = new Transaction(wallet.getPublicKey(), StringUtil.getTmaAddressFromString(word), Coin.SATOSHI, Coin.SATOSHI, 
						inputList.get(i++), wallet.getPrivateKey(), comment.getText().trim(), null, words);
				transaction.setApp(Applications.RATING);
				new SendTransactionRequest(network, transaction).start();
				logger.debug("sent {}", transaction);
			}
		}
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		return true;
	}

	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}
	


}
