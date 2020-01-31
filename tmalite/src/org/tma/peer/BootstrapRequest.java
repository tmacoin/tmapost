/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import java.util.HashSet;
import java.util.Set;

import org.tma.peer.thin.SubscribeToMessagesRequest;
import org.tma.util.Bootstrap;
import org.tma.util.Configurator;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

public class BootstrapRequest extends Request {

	private static final long serialVersionUID = -3701748162180479992L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Bootstrap bootstrap = new Bootstrap();
	private static final Set<Peer> myPeers = new HashSet<Peer>();

	private transient Network clientNetwork;

	private int clientBlockchainId;

	public BootstrapRequest(Network clientNetwork) {
		this.clientNetwork = clientNetwork;
		this.clientBlockchainId = clientNetwork.getBootstrapBlockchainId();
	}

	public Response getResponse(Network serverNetwork, Peer peer) throws Exception {
		return new Response();
	}

	public void start() {
		init();
		ThreadExecutor.getInstance().execute(new TmaRunnable("BootstrapRequest") {
			public void doRun() {
				try {
					process();
				} finally {
					new SubscribeToMessagesRequest(clientNetwork, clientNetwork.getTmaAddress()).start();
				}
			}
		});
	}
	
	private void init() {
		if (clientNetwork.isPeerSetCompleteForMyShard()) {
			clientNetwork.removeNonMyPeers();
			clientNetwork.removedUnconnectedPeers();
			return;
		}

		while (true) {
			if (myPeers.isEmpty()) {
				bootstrap.addPeers(clientNetwork);
			}
			clientNetwork.add(myPeers);

			Set<Peer> peers = clientNetwork.getAllPeers();
			logger.debug("peers.size()={}", peers.size());
			for (Peer peer : peers) {
				BootstrapRequest request = new BootstrapRequest(clientNetwork);
				peer.send(clientNetwork, request);
			}
			
			ThreadExecutor.sleep(200);

			if (clientNetwork.getMyPeers().size() > 0) {
				myPeers.addAll(clientNetwork.getMyPeers());
				return;
			}

			if (Configurator.getInstance().getBooleanProperty("org.tma.network.bootstrap.nowait")) {
				break;
			}
		}
	}

	private void process() {
		if (clientNetwork.isPeerSetCompleteForMyShard()) {
			clientNetwork.removeNonMyPeers();
			clientNetwork.removedUnconnectedPeers();
			return;
		}

		while (true) {
			if (myPeers.isEmpty()) {
				bootstrap.addPeers(clientNetwork);
			}
			clientNetwork.add(myPeers);

			Set<Peer> peers = clientNetwork.getAllPeers();
			logger.debug("peers.size()={}", peers.size());
			for (Peer peer : peers) {
				BootstrapRequest request = new BootstrapRequest(clientNetwork);
				peer.send(clientNetwork, request);
			}
			
			ThreadExecutor.sleep(200);

			if (clientNetwork.isPeerSetCompleteForMyShard()) {
				clientNetwork.removeNonMyPeers();
				clientNetwork.removedUnconnectedPeers();
				myPeers.addAll(clientNetwork.getMyPeers());
				return;
			}

			if (Configurator.getInstance().getBooleanProperty("org.tma.network.bootstrap.nowait")) {
				break;
			}
		}
	}
	
	

	public int getClientBlockchainId() {
		return clientBlockchainId;
	}

}
