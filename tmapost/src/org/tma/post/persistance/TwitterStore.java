/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.persistance;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.peer.thin.TwitterAccount;
import org.tma.persistance.DBExecutor;

public class TwitterStore {
	
	private static final Logger logger = LogManager.getLogger();
	private static Map<String, String> tables = new HashMap<String, String>();
	private static TwitterStore instance = new TwitterStore();
	
	public static TwitterStore getInstance() {
		return instance;
	}
	
	private TwitterStore() {
		try {
			verifyTable();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void verifyTable() throws Exception {
		if (tables.containsKey("TWITTER_SUBSCRIPTION")) {
			return;
		}
		new DBExecutor() {
			public void doWork() throws Exception {
				DatabaseMetaData dbmd = conn.getMetaData();
				rs = dbmd.getTables(null, USER, "TWITTER_SUBSCRIPTION", null);
				if (!rs.next()) {
					s.execute("CREATE TABLE TWITTER_SUBSCRIPTION (id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
							+ " account varchar(64) NOT NULL, address varchar(64) NOT NULL, description varchar(1024), timeStamp bigint NOT NULL, "
							+ " unique (address), primary key(id) )");
				}
				rs.close();
				tables.put("TWITTER_SUBSCRIPTION", "TWITTER_SUBSCRIPTION");
			}
		}.execute();
	}
	
	public void save(TwitterAccount twitterAccount) throws Exception {
		new DBExecutor() {
			public void doWork() throws Exception {
	            String sql = "insert into TWITTER_SUBSCRIPTION (account, address, timeStamp, description) values (?,?,?,?)";
	            ps = conn.prepareStatement(sql);
	            int i = 1;
	            ps.setString(i++, twitterAccount.getName());
	            ps.setString(i++, twitterAccount.getTmaAddress());
	            ps.setLong(i++, twitterAccount.getTimeStamp());
	            ps.setString(i++, twitterAccount.getDescription());
	            ps.executeUpdate();
			}
		}.execute();
	}
	
	public List<TwitterAccount> selectAll() {
		List<TwitterAccount> list = new ArrayList<TwitterAccount>();
		try {
			new DBExecutor() {
				public void doWork() throws Exception {
					String sql = "select * from TWITTER_SUBSCRIPTION";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while (rs.next()) {
						TwitterAccount twitterAccount = new TwitterAccount();
						twitterAccount.setName(rs.getString("account"));
						twitterAccount.setTmaAddress(rs.getString("address"));
						twitterAccount.setTimeStamp(rs.getLong("timeStamp"));
						twitterAccount.setDescription(rs.getString("description"));
						list.add(twitterAccount);
					}
				}
			}.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return list;
	}
	
	public void removeAll() {
		try {
			new DBExecutor() {
				public void doWork() throws Exception {
					String sql = "delete from TWITTER_SUBSCRIPTION";
					ps = conn.prepareStatement(sql);
					ps.executeUpdate();
				}
			}.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
