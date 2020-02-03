/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer.thin;

import org.tma.peer.Network;
import org.tma.peer.Peer;
import org.tma.peer.Request;
import org.tma.peer.Response;
import org.tma.util.Constants;
import org.tma.util.ExpiringMap;
import org.tma.util.Listeners;
import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

public class NewMessagesNotificationRequest extends Request {

	private static final long serialVersionUID = 7852014295465690974L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Listeners listeners = Listeners.getInstance();
	private static final ExpiringMap<String, String> receivedMessages = new ExpiringMap<>(Constants.TIMEOUT, Constants.MAX_SIZE);

	private SecureMessage secureMessage;

	public Response getResponse(Network serverNetwork, Peer peer) throws Exception {
		String key = StringUtil.trimToBlank(secureMessage.getTransactionId()) + secureMessage.getText();
		synchronized (receivedMessages) {
			if (receivedMessages.containsKey(key)) {
				return new Response();
			}
			receivedMessages.put(key, key);
		}
		logger.debug("There are new messages");
		listeners.sendEvent(new NewMessageEvent(secureMessage));
		return new Response();
	}

}
