package org.tma.post.tweet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tma.peer.thin.TwitterAccount;
import org.tma.util.TmaLogger;

public class SubscriptionStore {
	
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final SubscriptionStore instance = new SubscriptionStore();
	private static final String FILE = "subscriptions.txt";
	
	public static SubscriptionStore getInstance() {
		return instance;
	}

	public void save(TwitterAccount twitterAccount) {
		List<TwitterAccount> list = getSubscriptions();
		if(list.contains(twitterAccount)) {
			return;
		}
		list.add(twitterAccount);
		save(list);
	}
	
	private void save(List<TwitterAccount> list) {
		File file = new File(FILE);
		file.delete();
		try (
				OutputStream os = new FileOutputStream(file);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
			) {
				for(TwitterAccount account: list) {
					out.write(
							account.getTmaAddress()
							+ "," + account.getName()
							+ "," + account.getDescription()
							+ "," + account.getTimeStamp()
							);
					out.newLine();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
	}

	public void delete(TwitterAccount twitterAccount) {
		List<TwitterAccount> list = getSubscriptions();
		if(!list.contains(twitterAccount)) {
			return;
		}
		list.remove(twitterAccount);
		save(list);
		
	}

	public List<TwitterAccount> getSubscriptions() {
		File file = new File(FILE);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		List<TwitterAccount> result = new ArrayList<TwitterAccount>();
		try (
				InputStream is = new FileInputStream(FILE);
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
		) {
			String line;
			while ((line = in.readLine()) != null) {
				List<String> list =  new ArrayList<String>(Arrays.asList(line.split(",")));
				int size = list.size();
				if(size != 4) {
					continue;
				}
				TwitterAccount account = new TwitterAccount();
				int i = 0;
				account.setTmaAddress(list.get(i++));
				account.setName(list.get(i++));
				account.setDescription(list.get(i++));
				account.setTimeStamp(Long.parseLong(list.get(i++)));
				result.add(account);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}

}
