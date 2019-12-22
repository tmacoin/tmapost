/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.rating;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.Ratee;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.SearchRateesRequest;
import org.tma.post.Caller;
import org.tma.post.util.SwingUtil;
import org.tma.post.util.TableColumnAdjuster;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaRunnable;

public class FindRateeAction extends AbstractAction implements Caller {

	private static final long serialVersionUID = 6886690569988480986L;
	private static final Logger logger = LogManager.getLogger();
	
    private JFrame frame;
    private JTextField account;
    private JTextField jkeywords;
	
	public FindRateeAction(JFrame frame, JTextField account, JTextField jkeywords) {
		putValue(NAME, "Find Post");
		putValue(SHORT_DESCRIPTION, "Find Post");
		this.frame = frame;
		this.account = account;
		this.jkeywords = jkeywords;
		
	}

	public void actionPerformed(ActionEvent e) {
		JLabel label = SwingUtil.showWait(frame);
		ThreadExecutor.getInstance().execute(new TmaRunnable("CreateRateeAction") {
			public void doRun() {
				findRatees(label);
			}
		});
	}
	
	
	private void findRatees(JLabel label) {
		Set<String> words = getKeywords();
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		
		String accountName = StringUtil.trim(account.getText());
		SearchRateesRequest request = new SearchRateesRequest(network, accountName, words);
		request.start();
		@SuppressWarnings("unchecked")
		List<Ratee> list = (List<Ratee>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
		
		if(list == null) {
			label.setText("Failed to retrieve ratees. Please try again");
			return;
		}
		
		
		frame.getContentPane().removeAll();
		
		JPanel form = new JPanel();
		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
		
		logger.debug("list.size()={}", list.size());
		
		RateeTableModel model = new RateeTableModel(list);
		JTable table = new JTable(model);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnAdjuster tca = new TableColumnAdjuster(table);
		tca.adjustColumns();
		table.addMouseListener(new RateeMouseAdapter(table, list, frame));

		JScrollPane scroll = new JScrollPane (table);
		scroll.setBorder(null);
		form.add(scroll);
		
		frame.getContentPane().add(form);
		frame.revalidate();
		frame.getContentPane().repaint();
		
	}
	
	public void log(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}
	
	private Set<String> getKeywords() {
		Set<String> set = new HashSet<String>();
		String[] strings = jkeywords.getText().split(" ");
		for(String str: strings) {
			if(!"".equals(str)) {
				set.add(str);
			}
		}
		return set;
	}


}
