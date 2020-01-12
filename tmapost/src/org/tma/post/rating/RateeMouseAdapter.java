/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.rating;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.tma.peer.thin.Ratee;
import org.tma.post.util.SwingUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

public class RateeMouseAdapter extends MouseAdapter {
	
	private static final TmaLogger logger = TmaLogger.getLogger();
	
	private JTable table;
	private List<Ratee> list;
	private JFrame frame;
	
	public RateeMouseAdapter(JTable table, List<Ratee> list, JFrame frame) {
		this.table = table;
		this.list = list;
		this.frame = frame;
	}
	
	public void mouseClicked(MouseEvent evt) {
        int row = table.rowAtPoint(evt.getPoint());
        int col = table.columnAtPoint(evt.getPoint());
        if (row >= 0 && col >= 0) {
            doit(list.get(row));
        }
    }

	private void doit(final Ratee ratee) {
		final JLabel label = SwingUtil.showWait(frame);

		ThreadExecutor.getInstance().execute(new TmaRunnable("RateeMouseAdapter") {
			public void doRun() {
				label.setText(ratee.getName());
				logger.debug("ratee.getName()={}", ratee.getName());
				
				new RatingHelper(frame).show(ratee);
				
			}

			
		});
	}
	
}
