package org.tma.post.tweet;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.GetRepliesRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.Tweet;
import org.tma.post.util.JTextFieldRegularPopupMenu;
import org.tma.post.util.SwingUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

import com.jidesoft.swing.AutoResizingTextArea;

import net.miginfocom.swing.MigLayout;

public class TwitterHelper {
	
	private JFrame frame;
	
	public TwitterHelper(JFrame frame) {
		this.frame = frame;
	}

	public void print(JPanel panel, String str) {
		JTextArea area = new AutoResizingTextArea(1, 40, 55);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setOpaque( false );
		area.setEditable( false );
		area.setText(str);
		panel.add(area, "span");
		panel.add(Box.createRigidArea(new Dimension(0, 10)), "span");
	}

	public void addTweet(JPanel panel, final Tweet tweet) {
		String text = tweet.getFromTwitterAccount() + " · " + new Date(tweet.getTimeStamp()).toString() + "\n" + tweet.getText();
		JTextArea area = new AutoResizingTextArea(1, 40, 55);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setOpaque( false );
		area.setEditable( false );
		area.setText(text);
		panel.add(area, "span");
		panel.add(new JSeparator(), "growx, span");
		area.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	if(SwingUtilities.isLeftMouseButton(e)) {
            		displayTweet(tweet);
            	}
                
            }

        });
	}
	
	public JPanel showBackButton(Tweet tweet) {
		JPanel panel = new JPanel(new MigLayout("wrap 2", "[right][fill]"));
		final JButton btnSubmit = new JButton();
		btnSubmit.setAction(new ShowMyTweets(frame, tweet.getRecipient()));
		btnSubmit.setText("Back");
		
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backButton");

		frame.getRootPane().getActionMap().put("backButton", new AbstractAction() {
			private static final long serialVersionUID = 4946947535624344910L;

			public void actionPerformed(ActionEvent actionEvent) {
				btnSubmit.doClick();
				frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).clear();
				frame.getRootPane().getActionMap().clear();
			}
		});
		
		JPanel flow = new JPanel(new FlowLayout(FlowLayout.LEFT));
		flow.add(btnSubmit);
		panel.add(flow);
		panel.add(new JLabel(""));
		return panel;
	}

	public void displayTweet(final Tweet tweet) {
		final JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("ShowTweet") {
			public void doRun() {
				Network network = Network.getInstance();
				if(!network.isPeerSetComplete()) {
					BootstrapRequest.getInstance().start();
				}
				GetRepliesRequest request = new GetRepliesRequest(network, tweet.getTransactionId(), tweet.getRecipient());
				request.start();
				@SuppressWarnings("unchecked")
				List<Tweet> list = (List<Tweet>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
				
				if(list == null) {
					label.setText("Failed to retrieve transactions. Please try again");
					return;
				}
				
				frame.getContentPane().removeAll();
				
				JPanel panel = showBackButton(tweet);
				
				addTweet(panel, tweet);
				
				Comparator<Tweet> compareByTimestamp = new Comparator<Tweet>() {
					
					@Override
					public int compare(Tweet o1, Tweet o2) {
						return Long.valueOf(o2.getTimeStamp()).compareTo( o1.getTimeStamp() );
					}
				};
				
				Collections.sort(list, compareByTimestamp);
				for(Tweet tweet: list) {
					addTweet(panel, tweet);
				}

				createForm(panel, tweet);
				
				JScrollPane jScrollPane = new JScrollPane (panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				frame.getContentPane().add(jScrollPane);
				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();
			}
		});
	}
	
	public void createForm(JPanel panel, Tweet tweet) {

		JLabel label = new JLabel("Enter reply:");
		panel.add(label);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextArea area = new JTextArea(3, 45);
		JTextFieldRegularPopupMenu.addTo(area);
		JScrollPane scroll = new JScrollPane (area);
		p.add(scroll);
		panel.add(p);
		
		panel.add(new JLabel(""));
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new SendReplyAction(frame, area, tweet));
		p.add(btnSubmit);
		panel.add(p);

		frame.getRootPane().setDefaultButton(btnSubmit);
	}

}
