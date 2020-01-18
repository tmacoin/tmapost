/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;

import org.tma.util.Bootstrap;
import org.tma.util.Configurator;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

public class SyncPeersRequest  extends Request {

	private static final long serialVersionUID = -3794625141357183838L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final int SYNC_PERIOD = 1000 * 60;
	private static final Bootstrap bootstrap = new Bootstrap();
	
	private transient Network clientNetwork;
	private transient PeerLock peerLock;
	private int port;

	public SyncPeersRequest(Network clientNetwork) {
		this.clientNetwork = clientNetwork;
		port = clientNetwork.getLocal().getiNetAddress().getPort();
	}

	public Response getResponse(Network serverNetwork, Peer peer) {
		Socket socket =  peer.getRawSocket();
		if(!serverNetwork.isNetworkStarted() || socket == null) {
			return new Response();
		}
		InetSocketAddress iNetAddress  = new InetSocketAddress(socket.getInetAddress(), socket.getPort());
		Peer requester = Peer.getInstance(iNetAddress);
		Set<Peer> toPeers = serverNetwork.getToPeersCopy();
		toPeers.remove(requester);
		
		if(port != 0) {
			iNetAddress  = new InetSocketAddress(socket.getInetAddress(), port);
			requester = Peer.getInstance(iNetAddress);
			toPeers.remove(requester);
			serverNetwork.add(requester);
		}

		Response response = new SyncPeersResponse(toPeers, serverNetwork.getLocal().getiNetAddress().getPort());
		response.setDoDisconnect(serverNetwork.isPeerSetFull(getBlockchainId()));
		return response;	
	}

	public void start() {
		ThreadExecutor.getInstance().execute(new TmaRunnable("SyncPeersRequest") {
			public void doRun() {
				syncPeers();
			}
		});
	}
	
	private void syncPeers() {
		while (true) {
			try {
				runSyncPeers();
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private void runSyncPeers() {
		ThreadExecutor.sleep(SYNC_PERIOD);
		if(clientNetwork.isPeerSetFull()) {
			bootstrap.addLocals(clientNetwork);
			return;
		}
		bootstrap.addListedPeers(clientNetwork);
		if(Configurator.getInstance().getBooleanProperty("org.tma.peer.listed.peers.only")) {
			return;
		}
		for (Peer peer : clientNetwork.getAllPeers()) {
			if(sendToOnePeer(peer)) {
				return;
			}
		}
		
	}
	
	private boolean sendToOnePeer(Peer peer) {
		if(peer.getiNetAddress() == null) {
			return false;
		}
		try {
			peerLock = new PeerLock(peer);
			synchronized (peerLock) {
				SyncPeersRequest request =  new SyncPeersRequest(clientNetwork);
				request.peerLock = peerLock;
				peer.send(clientNetwork, request);
				peerLock.wait(Peer.CONNECT_TIMEOUT);
			}
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return clientNetwork.isPeerSetFull();
	}
	
	public void onSendComplete(Peer peer) {
		PeerLock peerLock = this.peerLock;
		if(!peerLock.isForPeer(peer)) {
			return;
		}
		synchronized (peerLock) {
			peerLock.notify();
		}
	}

}
