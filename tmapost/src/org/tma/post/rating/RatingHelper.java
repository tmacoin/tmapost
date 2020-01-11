package org.tma.post.rating;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.Ratee;
import org.tma.peer.thin.Rating;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.SearchPostsRequest;
import org.tma.peer.thin.SearchRateeRequest;
import org.tma.peer.thin.SearchRatingForRaterRequest;
import org.tma.peer.thin.SearchRatingRequest;
import org.tma.post.Wallets;
import org.tma.post.message.SendMessage;
import org.tma.post.util.JTextFieldRegularPopupMenu;
import org.tma.post.util.SwingUtil;
import org.tma.post.util.TableColumnAdjuster;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

import com.jidesoft.swing.AutoResizingTextArea;

import net.miginfocom.swing.MigLayout;

public class RatingHelper {
	
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final int MAX_NUMBER_OF_KEYWORDS = 10;

	private JFrame frame;

	public RatingHelper(JFrame frame) {
		this.frame = frame;
	}
	
	public void showRatee(String ratee, String transactionId) {
		JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("showRater") {
			public void doRun() {
				doShowRatee(ratee, transactionId, label);
			}
		});
		
	}

	private void doShowRatee(String account, String transactionId, JLabel label) {
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}

		SearchRateeRequest request = new SearchRateeRequest(network, account, transactionId);
		request.start();
		Ratee ratee = (Ratee) ResponseHolder.getInstance().getObject(request.getCorrelationId());
		
		if(ratee == null) {
			label.setText("Failed to retrieve ratee. Please try again");
			return;
		}
		show(ratee);
		
	}
	
	public void show(Ratee ratee) {
		
		frame.getContentPane().removeAll();

		JPanel form = new JPanel(new MigLayout("wrap 2", "[right][fill]"));
		JScrollPane scrollPane = new JScrollPane(form, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);

		
		form.add(new JLabel("Post:"));
		
		JTextField account = new JTextField(45);
		account.setText(ratee.getName());
		account.setOpaque(false);
		account.setEditable(false);
		account.setBorder(null);
		JTextFieldRegularPopupMenu.addTo(account);
		account.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
            	if(SwingUtilities.isLeftMouseButton(e)) {
            		SendMessage sendMessage = new SendMessage(frame, ratee.getCreatorTmaAddress());
            		sendMessage.setSubject("Re: " + ratee.getName());
            		sendMessage.actionPerformed(null);
            	}
            }
        });
		account.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		account.setForeground(Color.BLUE);
		Map<TextAttribute, Integer> attributes = new HashMap<>();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		account.setFont(account.getFont().deriveFont(attributes));
		form.add(account);

		form.add(new JLabel("Description:"));
		
		JTextArea description = new AutoResizingTextArea(1, 40, 45);
		description.setText(ratee.getDescription());
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setOpaque(false);
		description.setEditable(false);
		description.revalidate();
		form.add(description);
		
		form.add(new JLabel("Created On:"));
		
		JTextField date = new JTextField(45);
		date.setText(new Date(ratee.getTimeStamp()).toString());
		date.setOpaque(false);
		date.setEditable(false);
		date.setBorder(null);
		form.add(date);
		
		form.add(new JLabel("Identifier:"));
		
		JTextField transactionId = new JTextField(45);
		transactionId.setText(ratee.getTransactionId());
		transactionId.setOpaque(false);
		transactionId.setEditable(false);
		transactionId.setBorder(null);
		form.add(transactionId);
		
		form.add(new JLabel("Comment:"));
		
		JTextArea comment = new AutoResizingTextArea(5, 40, 45);
		comment.setLineWrap(true);
		comment.setWrapStyleWord(true);
		JTextFieldRegularPopupMenu.addTo(comment);
		comment.setBorder(new JTextField().getBorder());
		form.add(comment);
		
		form.add(new JLabel("Rating:"));
		
        //... Create the buttons.
        JRadioButton yesButton   = new JRadioButton("Yes");
        JRadioButton noButton    = new JRadioButton("No");

        //... Create a button group and add the buttons.
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(yesButton);
        bgroup.add(noButton);
        
        
        //... Arrange buttons vertically in a panel
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(yesButton);
        radioPanel.add(noButton);
        
        //... Add a titled border to the button panel.
        radioPanel.setBorder(BorderFactory.createTitledBorder(
                   BorderFactory.createEtchedBorder(), "Approve?"));
        
        form.add(radioPanel);
        
        form.add(new JLabel(""));
        
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new AddRatingAction(frame, comment, bgroup, account, transactionId));
		p.add(btnSubmit);
		form.add(p);
		
		showComments(form, ratee);
		
		form.add(new JSeparator(), "growx, span");
		
		String myTmaAdress = Wallets.getInstance().getWallet(Wallets.TMA, Wallets.WALLET_NAME).getTmaAddress();
		
		if(myTmaAdress.equals(ratee.getCreatorTmaAddress())) {
			form.add(new JLabel(""));
	        
	        p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JButton deleteRatee = new JButton("Delete Post");
			deleteRatee.setAction(new DeleteRateeAction(frame, ratee.getTransactionId(), StringUtil.getTmaAddressFromString(ratee.getName())));
			p.add(deleteRatee);
			form.add(p);
		}
		
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
	}
	
	private void showComments(JPanel form, Ratee ratee) {
		
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		String accountName = StringUtil.trim(ratee.getName());
		SearchRatingRequest request = new SearchRatingRequest(network, accountName, ratee.getTransactionId());
		request.start();
		@SuppressWarnings("unchecked")
		List<Rating> list = (List<Rating>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
		
		if(list == null) {
			form.add(new JLabel("Failed to retrieve ratings. Please try again"), "span, left");
			return;
		}
		
		
		form.add(new JLabel("Total rating is " + ratee.getTotalRating()), "span, left");
		
		
		
		showRatings(list, form);
		
	}
	
	private void showRatings(List<Rating> list, JPanel form) {
		for(Rating rating: list) {
			
			form.add(new JSeparator(), "growx, span");
			
			form.add(new JLabel("Rater:"));
			
			JTextField rater = new JTextField(45);
			rater.setText(rating.getRater());
			rater.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			rater.setOpaque(false);
			rater.setEditable(false);
			rater.setBorder(null);
			rater.setForeground(Color.BLUE);
			Font font = rater.getFont();
			Map<TextAttribute, Integer> attributes = new HashMap<>();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			rater.setFont(font.deriveFont(attributes));
			rater.addMouseListener(new MouseAdapter(){
	            public void mouseClicked(MouseEvent e){
	            	if(SwingUtilities.isLeftMouseButton(e)) {
	            		showRater(rating.getRater());
	            	}
	            }
	        });
			form.add(rater);
			
			form.add(new JLabel("Post:"));
			
			JTextField ratee = new JTextField(45);
			ratee.setText(rating.getRatee());
			ratee.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			ratee.setOpaque(false);
			ratee.setEditable(false);
			ratee.setBorder(null);
			ratee.setForeground(Color.BLUE);
			JTextFieldRegularPopupMenu.addTo(ratee);
			ratee.setFont(font.deriveFont(attributes));
			ratee.addMouseListener(new MouseAdapter(){
	            public void mouseClicked(MouseEvent e){
	            	if(SwingUtilities.isLeftMouseButton(e)) {
	            		showRatee(rating.getRatee(), rating.getTransactionId());
	            	}
	                
	            }
	        });
			form.add(ratee);
			
			form.add(new JLabel("Rate:"));
			
			JTextField rate = new JTextField(45);
			rate.setText(rating.getDisplayRate());
			rate.setOpaque(false);
			rate.setEditable(false);
			rate.setBorder(null);
			JTextFieldRegularPopupMenu.addTo(rate);
			form.add(rate);
			
			form.add(new JLabel("Date:"));
			
			JTextField date = new JTextField(45);
			date.setText(new Date(rating.getTimeStamp()).toString());
			date.setOpaque(false);
			date.setEditable(false);
			date.setBorder(null);
			JTextFieldRegularPopupMenu.addTo(date);
			form.add(date);
			
			form.add(new JLabel("Comment:"));
			
			JTextArea comment = new AutoResizingTextArea(1, 40, 45);
			comment.setText(rating.getComment());
			comment.setLineWrap(true);
			comment.setWrapStyleWord(true);
			comment.setOpaque(false);
			comment.setEditable(false);
			comment.revalidate();
			form.add(comment);

		}
	}
	
	public void showRater(String rater) {
		JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("showRater") {
			public void doRun() {
				displayRaterComments(rater, label);
			}
		});
		
	}

	private void displayRaterComments(String rater, JLabel label) {
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}

		SearchRatingForRaterRequest request = new SearchRatingForRaterRequest(network, rater);
		request.start();
		@SuppressWarnings("unchecked")
		List<Rating> list = (List<Rating>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
		
		if(list == null) {
			label.setText("Failed to retrieve ratings. Please try again");
			return;
		}
		
		frame.getContentPane().removeAll();

		JPanel form = new JPanel(new MigLayout("wrap 2", "[right][fill]"));
		JScrollPane scrollPane = new JScrollPane(form, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);
		
		form.add(new JLabel("Total number of comments found for " + rater + ": " + list.size()), "span, left");
		
		showRatings(list, form);
	
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}

	public void showPosts(String tmaAddress) {
		JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("showRater") {
			public void doRun() {
				doShowPosts(tmaAddress, label);
			}
		});
		
	}

	private void doShowPosts(String tmaAddress, JLabel label) {
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		
		SearchPostsRequest request = new SearchPostsRequest(network, tmaAddress);
		request.start();
		@SuppressWarnings("unchecked")
		List<Ratee> list = (List<Ratee>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
		
		if(list == null) {
			label.setText("Failed to retrieve posts. Please try again");
			return;
		}
		
		logger.debug("Retrieved {} posts", list.size());
		
		if(list.size() == 0) {
			label.setText("No posts were found.");
			return;
		}
		
		displayPosts(list, label);
		
	}
	
	public void displayPosts(List<Ratee> list, JLabel label) {
		frame.getContentPane().removeAll();
		
		JPanel form = new JPanel();
		BoxLayout boxLayout = new BoxLayout(form, BoxLayout.Y_AXIS);
		form.setLayout(boxLayout);
		
		frame.getContentPane().add(form);
		
		logger.debug("list.size()={}", list.size());
		
		label = new JLabel("Found " + list.size() + " posts: ");
		label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		label.setBorder(new EmptyBorder(5,5,5,5));
		form.add(label);
		
		RateeTableModel model = new RateeTableModel(list);
		JTable table = new JTable(model);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnAdjuster tca = new TableColumnAdjuster(table);
		tca.adjustColumns();
		table.addMouseListener(new RateeMouseAdapter(table, list, frame));
		
		JScrollPane scroll = new JScrollPane (table);
		scroll.setBorder(null);
		scroll.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
		form.add(scroll);
		
		frame.revalidate();
		frame.getContentPane().repaint();
	}
	
	public Set<String> getKeywords(JTextField jkeywords) {
		Set<String> set = new HashSet<String>();
		String[] strings = jkeywords.getText().split(" ");
		for(String str: strings) {
			if(set.size() > MAX_NUMBER_OF_KEYWORDS) {
				break;
			}
			if(!"".equals(str)) {
				set.add(str.toLowerCase());
			}
		}
		return set;
	}

}
