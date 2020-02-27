/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

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
	private static BootstrapRequest instance = new BootstrapRequest();
	private static final int SEND_PEERS_MAX_NUMBER = 20;

	private transient Network clientNetwork;
	private int clientBlockchainId;
	private transient Set<Peer> sentPeers = new HashSet<>();
	private transient final ReentrantLock lock = new ReentrantLock();
	private transient long startTime;
	
	public static BootstrapRequest getInstance() {
		return instance;
	}

	private BootstrapRequest() {
		this.clientNetwork = Network.getInstance();
		this.clientBlockchainId = clientNetwork.getBootstrapBlockchainId();
	}

	public Response getResponse(Network serverNetwork, Peer peer) throws Exception {
		return new Response();
	}
	
	public void startAndWait() {
		boolean gotLock = lock.tryLock();
		if(!gotLock) {
			if(!clientNetwork.getMyPeers().isEmpty()) {
				return;
			}
			lock.lock();
			lock.unlock();
			return;
		}
		lock.unlock();
		start();
	}

	public void start() {
		boolean gotLock = lock.tryLock();
		if(!gotLock) {
			return;
		}
		logger.debug("Network status: {}", clientNetwork.getPeerCount());
		startTime = System.currentTimeMillis();
		init();
		lock.unlock();
		ThreadExecutor.getInstance().execute(new TmaRunnable("BootstrapRequest") {
			public void doRun() {
				lock.lock();
				try {
					process();
				} finally {
					new SubscribeToMessagesRequest(clientNetwork, clientNetwork.getTmaAddress()).start();
					getSentPeers().clear();
					for (Peer peer : clientNetwork.getMyPeers()) {
						peer.addResetListener(BootstrapRequest.this);
					}
					logger.debug("Network status: {}, bootstrap took: {} ms", clientNetwork.getPeerCount(), System.currentTimeMillis() - startTime);
					lock.unlock();
				}
			}
		});
	}
	
	public void onSendComplete(Peer peer) {
		synchronized(this) {
			getSentPeers().remove(peer);
			notify();
		}
		
	}
	
	private void init() {
		if (clientNetwork.isPeerSetCompleteForMyShard()) {
			clientNetwork.removeNonMyPeers();
			clientNetwork.removedUnconnectedPeers();
			return;
		}

		while (true) {
			if (myPeers.size() < Network.getPeerSetCompleteMinSize()) {
				bootstrap.addPeers(clientNetwork);
			}
			clientNetwork.add(myPeers);

			Set<Peer> peers = clientNetwork.getAllPeers();
			synchronized(this) {
				peers.removeAll(getSentPeers());
			}
			peers = sortByClosest(peers);
			for (Peer peer : peers) {
				synchronized(this) {
					if (clientNetwork.getMyPeers().size() > 0) {
						myPeers.addAll(clientNetwork.getMyPeers());
						return;
					}
					try {
						if(getSentPeers().size() > SEND_PEERS_MAX_NUMBER) {
							wait(Constants.ONE_MINUTE);
						}
						if(!addCachedPeers(peer)) {
							peer.send(clientNetwork, this);
						}
						
						getSentPeers().add(peer);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}

			if (Configurator.getInstance().getBooleanProperty("org.tma.network.bootstrap.nowait")) {
				break;
			}
		}
		return;
	}
	
	private boolean addCachedPeers(Peer peer) {
		Set<Peer> set = BootstrapResponse.toPeersMap.get(peer);
		if(set != null) {
			clientNetwork.add(set);
			logger.debug("BootstrapRequest peer={} set={}", peer, set);
			return true;
		}
		return false;
	}
	
	private Set<Peer> sortByClosest(Set<Peer> peers) {

		List<Peer> list = new ArrayList<Peer>(peers);
		final int myShardId = clientNetwork.getBootstrapBlockchainId();
		Collections.sort(list, new Comparator<Peer>() {
			@Override
			public int compare(Peer peer1, Peer peer2) {
				return Integer.compare(peer1.getBlockchainId() ^ myShardId, peer2.getBlockchainId() ^ myShardId);
			}
		});
		return new LinkedHashSet<Peer>(list);

	}

	private void process() {
		if (clientNetwork.isPeerSetCompleteForMyShard()) {
			clientNetwork.removeNonMyPeers();
			clientNetwork.removedUnconnectedPeers();
			return;
		}

		while (true) {
			if (myPeers.size() < Network.getPeerSetCompleteMinSize()) {
				bootstrap.addPeers(clientNetwork);
			}
			clientNetwork.add(myPeers);

			Set<Peer> peers = clientNetwork.getAllPeers();
			synchronized(this) {
				peers.removeAll(getSentPeers());
			}

			for (Peer peer : peers) {
				synchronized(this) {
					if (clientNetwork.isPeerSetCompleteForMyShard()) {
						clientNetwork.removeNonMyPeers();
						clientNetwork.removedUnconnectedPeers();
						myPeers.addAll(clientNetwork.getMyPeers());
						return;
					}
					try {
						if(getSentPeers().size() > SEND_PEERS_MAX_NUMBER) {
							wait(Constants.ONE_MINUTE);
						}
						
						if(!addCachedPeers(peer)) {
							peer.send(clientNetwork, this);
						}
						
						getSentPeers().add(peer);
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
		myPeers.remove(peer);
		ThreadExecutor.getInstance().execute(new TmaRunnable("BootstrapRequest.onPeerReset()") {
			public void doRun() {
				start();
			}
		});
	}

	public Set<Peer> getSentPeers() {
		return sentPeers;
	}

}
