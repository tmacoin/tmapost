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
import org.tma.util.Constants;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

public class BootstrapRequest extends Request implements PeerResetListener {

	private static final long serialVersionUID = -3701748162180479992L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Bootstrap bootstrap = new Bootstrap();
	private static final Set<Peer> myPeers = new HashSet<Peer>();
	private static BootstrapRequest instance;
	private static final int SEND_PEERS_MAX_NUMBER = 30;

	private transient Network clientNetwork;
	private int clientBlockchainId;
	private transient Set<Peer> sentPeers;
	private transient boolean active;
	
	public static synchronized BootstrapRequest getInstance() {
		if(instance == null) {
			instance = new BootstrapRequest();
		}
		return instance;
	}

	private BootstrapRequest() {
		this.clientNetwork = Network.getInstance();
		this.clientBlockchainId = clientNetwork.getBootstrapBlockchainId();
	}

	public Response getResponse(Network serverNetwork, Peer peer) throws Exception {
		return new Response();
	}

	public synchronized void start() {
		logger.debug("Network status: {}", clientNetwork.getPeerCount());
		if(active) {
			return;
		}
		active = true;
		sentPeers = new HashSet<>();
		init();
		ThreadExecutor.getInstance().execute(new TmaRunnable("BootstrapRequest") {
			public void doRun() {
				try {
					process();
				} finally {
					new SubscribeToMessagesRequest(clientNetwork, clientNetwork.getTmaAddress()).start();
					sentPeers.clear();
					active = false;
					for (Peer peer : clientNetwork.getMyPeers()) {
						peer.addResetListener(BootstrapRequest.this);
					}
				}
			}
		});
	}
	
	public void onSendComplete(Peer peer) {
		synchronized(sentPeers) {
			sentPeers.remove(peer);
			//logger.debug("removed {}", peer);
			sentPeers.notify();
		}
		
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
			synchronized(sentPeers) {
				peers.removeAll(sentPeers);
			}
			//logger.debug("peers.size()={}", peers.size());
			for (Peer peer : peers) {
				
				if (clientNetwork.getMyPeers().size() > 0) {
					myPeers.addAll(clientNetwork.getMyPeers());
					return;
				}

				synchronized(sentPeers) {
					try {
						if(sentPeers.size() > SEND_PEERS_MAX_NUMBER) {
							sentPeers.wait(Constants.TIMEOUT);
						}
						
						peer.send(clientNetwork, this);
						sentPeers.add(peer);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}
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
			synchronized(sentPeers) {
				peers.removeAll(sentPeers);
			}
			
			//logger.debug("peers.size()={}", peers.size());
			for (Peer peer : peers) {
				
				if (clientNetwork.isPeerSetCompleteForMyShard()) {
					clientNetwork.removeNonMyPeers();
					clientNetwork.removedUnconnectedPeers();
					myPeers.addAll(clientNetwork.getMyPeers());
					return;
				}
				
				synchronized(sentPeers) {
					try {
						if(sentPeers.size() > SEND_PEERS_MAX_NUMBER) {
							sentPeers.wait(Constants.TIMEOUT);
						}
						
						peer.send(clientNetwork, this);
						sentPeers.add(peer);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}
				
			}

			if (Configurator.getInstance().getBooleanProperty("org.tma.network.bootstrap.nowait")) {
				break;
			}
		}
	}
	
	

	public int getClientBlockchainId() {
		return clientBlockchainId;
	}

	@Override
	public void onPeerReset(Peer peer) {
		logger.debug("Peer removed {}", peer);
		ThreadExecutor.getInstance().execute(new TmaRunnable("BootstrapRequest.onPeerReset()") {
			public void doRun() {
				start();
			}
		});
	}

}
