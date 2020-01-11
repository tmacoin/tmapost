/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.tma.util.Base58;
import org.tma.util.Configurator;
import org.tma.util.ShardUtil;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;

public class Network implements Serializable {

	private static final long serialVersionUID = -6546699938045894752L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	
	private static final SecureRandom random = new SecureRandom();
	private static Network instance;
	
	/**
     * Number of times a node can be marked as stale before it is actually removed.
     **/
    public static final int STALE = 5;
	
	
	private Set<Peer> fromPeers = new LinkedHashSet<Peer>();
	private Set<Peer> toPeers = Collections.synchronizedSet(new LinkedHashSet<Peer>());
	private Set<Peer> locals = new LinkedHashSet<Peer>();
	private Set<Peer> thinPeers = new LinkedHashSet<Peer>();
	private Peer local;
	
	private transient String networkIdentifier;
	private transient boolean networkStarted;
	private transient String tmaAddress;
	private transient int bootstrapShardingPower;
	private transient boolean bootstrapShardingPowerUpdated;
	
	private boolean thin;
	
	public Network(String tmaAddress) throws UnknownHostException {
		instance = this;
		thin = true;
		this.tmaAddress = tmaAddress;
		byte[] bytes = ArrayUtils.addAll(StringUtil.BLOCKCHAIN_TYPE, random.generateSeed(16));
		setNetworkIdentifier(Base58.encode(bytes));
		local = Peer.getInstance(new InetSocketAddress(InetAddress.getLocalHost(), 0));
		local.setNetworkIdentifier(getNetworkIdentifier());
		new GetBootstrapPowerRequest(this).start();
		logger.info("Your shard id is {}", getBootstrapBlockchainId());
		new BootstrapRequest(this).start();
		new SyncPeersRequest(this).start();
		setNetworkStarted(true);
		logger.info("Network started");
	}

	public Network(int port, String tmaAddress) throws UnknownHostException {
		instance = this;
		this.tmaAddress = tmaAddress;
		byte[] bytes = ArrayUtils.addAll(StringUtil.BLOCKCHAIN_TYPE, random.generateSeed(16));
		setNetworkIdentifier(Base58.encode(bytes));
		local = Peer.getInstance(new InetSocketAddress(InetAddress.getLocalHost(), port));
		local.setNetworkIdentifier(getNetworkIdentifier());
		logger.info("local peer {}", local);
		init();
	}

	private void init() {
		new GetBootstrapPowerRequest(this).start();
		logger.info("Your shard id is {}", getBootstrapBlockchainId());
		new BootstrapRequest(this).start();
		new SyncPeersRequest(this).start();
		setNetworkStarted(true);
		printConnectedPeers();
	}
	
	public static Network getInstance() {
		return instance;
	}

	public Peer getLocal() {
		return local;
	}
	
	public synchronized boolean add(Peer peer) {
		if(getSkipPeers().contains(peer)) {
			return false;
		}
		if(peer.getiNetAddress().getAddress() == null) {
			return false;
		}
		if(local.equals(peer) || local.getNetworkIdentifier().equals(peer.getNetworkIdentifier()) || toPeers.contains(peer) || locals.contains(peer)) {
			return false;
		}
		for(Peer toPeer: toPeers) {
			if(toPeer.getNetworkIdentifier() != null && toPeer.getNetworkIdentifier().equals(peer.getNetworkIdentifier())) {
				return false;
			}
		}

		if(!getMateShardsId().contains(peer.getBlockchainId()) && peer.isBlockchainIdSet()) {
			return false;
		}
		
		if(peer.isFromPeer() || peer.isLocalPeer()) {
			return false;
		}

		return toPeers.add(peer);
	}
	
	public void add(Set<Peer> peers) {
		for(Peer peer: peers) {
			add(peer);
		}
	}
	
	public synchronized void addBootstrap(Set<Peer> set) {
		set.removeAll(getSkipPeers());
		toPeers.addAll(set);
	}
	
	public boolean removeIfStale(Peer peer) {
		
		if(peer.incFailCount() > Network.STALE) {
			removePeer(peer);
			return true;
		}
		if(peer.isFromPeer()) {
			removePeer(peer);
			return true;
		}
		peer.reset();
		return false;
	}
	
	public synchronized boolean removePeer(Peer peer) {
		
		boolean result = getToPeers().remove(peer) || getFromPeers().remove(peer) || getLocals().remove(peer);
		if(result) {
			peer.reset();
		}
		peer.seenNow();
		return result;
	}
	
	public synchronized void addToThinPeers(Peer peer) {
		getToPeers().remove(peer);
		getFromPeers().remove(peer);
		getLocals().remove(peer);
		thinPeers.add(peer);
	}

	public Set<Peer> getFromPeers() {
		return fromPeers;
	}

	private Set<Peer> getToPeers() {
		return toPeers;
	}
	
	public synchronized Set<Peer> getToPeersCopy() {
		return new LinkedHashSet<Peer>(getToPeers());
	}
	
	public synchronized Set<Peer> getAllPeers() {
		Set<Peer> peers = new LinkedHashSet<Peer>();
		peers.addAll(getLocals());
		peers.addAll(getFromPeers());
		peers.addAll(getToPeers());
		peers.removeIf(p -> p == null);
		return peers;
	}
	
	public Set<Peer> getMatePeers() {
		Set<Peer> peers = getAllPeers();
		List<Integer> blockShardIds = getMateShardsId();
		peers.removeIf(peer -> peer.isBlockchainIdSet() && !blockShardIds.contains(peer.getBlockchainId()));
		return peers;
	}
	
	public Set<Peer> getCommonPeers(int shardId) {
		Set<Peer> peers = getAllPeers();
		List<Integer> blockShardIds = getCommonShards(shardId);
		peers.removeIf(peer -> peer.isBlockchainIdSet() && !blockShardIds.contains(peer.getBlockchainId()));
		return peers;
	}
	
	public List<Peer> getPeersByShardId(int shardId) {
		List<Peer> list = new ArrayList<Peer>(getAllPeers());
		list.removeIf(peer -> peer.isBlockchainIdSet() && peer.getBlockchainId() != shardId);
		return list;
	}

	public String getNetworkIdentifier() {
		return networkIdentifier;
	}

	public void setNetworkIdentifier(String networkIdentifier) {
		this.networkIdentifier = networkIdentifier;
	} 
	
	public Set<String> getConnectedNetworkIds() {
		Set<String> set = new HashSet<String>();
		for(Peer peer : getAllPeers()) {
			if(peer.isConnected()) {
				set.add(peer.getNetworkIdentifier());
			}
		}
		return set;
	}

	public boolean isNetworkStarted() {
		return networkStarted;
	}

	public void setNetworkStarted(boolean networkStarted) {
		this.networkStarted = networkStarted;
	}
	
	public int getBlockchainId() {
		return getBootstrapBlockchainId();
	}

	public int getShardingPower() {
		return getBootstrapShardingPower();
	}
	
	public List<Integer> getMateShardsId() {
		int shardingPower = getShardingPower();
		int blockchainId = getBlockchainId();
		if(getBootstrapShardingPower() > shardingPower) {
			shardingPower = getBootstrapShardingPower();
			blockchainId = getBootstrapBlockchainId();
		}
		List<Integer> mates = ShardUtil.getMateShardsId(blockchainId, shardingPower);
		return mates;
	}
	
	public boolean isMate(int shardId) {
		return getMateShardsId().contains(shardId);
	}
	
	public List<Integer> getCommonShards(int shardId) {
		int shardingPower = getShardingPower();
		int blockchainId = getBlockchainId();
		if(getBootstrapShardingPower() > shardingPower) {
			shardingPower = getBootstrapShardingPower();
			blockchainId = getBootstrapBlockchainId();
		}
		List<Integer> mates = ShardUtil.getCommonShards(blockchainId, shardId, shardingPower);
		return mates;
	}

	public String getTmaAddress() {
		return tmaAddress;
	}

	public void setBootstrapShardingPower(int bootstrapShardingPower) {
		if(this.bootstrapShardingPower >= bootstrapShardingPower) {
			return;
		}
		this.bootstrapShardingPower = bootstrapShardingPower;
	}

	public int getBootstrapShardingPower() {
		return bootstrapShardingPower;
	}
	
	public int getBootstrapBlockchainId() {
		return StringUtil.getShard(tmaAddress, getBootstrapShardingPower());
	}

	public Set<Peer> getLocals() {
		return locals;
	}

	public synchronized void addLocals(Set<Peer> locals) {
		locals.removeAll(getSkipPeers());
		Peer loopbackPeer = Peer.getInstance(new InetSocketAddress(InetAddress.getLoopbackAddress(), local.getiNetAddress().getPort()));
		locals.remove(loopbackPeer);
		for(Peer peer: locals) {
			peer.setLocalPeer(true);
		}
		getLocals().addAll(locals);
	}

	public synchronized void addFromPeer(Peer fromPeer) {
		getFromPeers().add(fromPeer);
	}
	
	private void printConnectedPeers() {
		List<Peer> list = getConnectedPeers();
		System.out.println("Connected to: ");
		for(Peer peer: list) {
			System.out.println(String.format("%s %s", peer.getBlockchainId(), peer));
		}
	}
	
	public List<Peer> getConnectedPeers() {
		Set<Peer> mates = getMatePeers();
		
		mates.removeIf(peer -> !peer.isConnected() || !peer.isBlockchainIdSet());
		List<Peer> list = new ArrayList<Peer>(mates);
		Collections.sort(list, new Comparator<Peer>() {
	        @Override
	        public int compare(Peer peer1, Peer peer2) {
	            return Integer.compare(peer1.getBlockchainId(), peer2.getBlockchainId());
	        }
	    });
		return list;
	}

	public boolean isPeerSetComplete() {
		boolean result = true;
		List<Integer> mateIds= getMateShardsId();
		Map<Integer, Integer> peerCount = new HashMap<Integer, Integer>();
		for(int i: mateIds) {
			int count = 0;
			List<Peer> peers = getPeersByShardId(i);
			for(Peer peer: peers) {
				if(peer.isConnected()) {
					count++;
				}
			}
			if(count < getPeerSetCompleteMinSize()) {
				result = false;
			}
			peerCount.put(i, count);
		}
		logger.debug("{} peerCount={}", getBlockchainId(), peerCount);
		if(!result) {
			ThreadExecutor.sleep(1000);
		}
		return result;
	}
	
	private static int getPeerSetCompleteMinSize() {
		return Configurator.getInstance().getIntProperty("org.tma.peer.peers.complete.minsize", 1);
	}
	
	public boolean isPeerSetFull() {
		List<Integer> mateIds= getMateShardsId();
		Map<Integer, Integer> peerCount = new HashMap<Integer, Integer>();
		for(int i: mateIds) {
			int count = 0;
			List<Peer> peers = getPeersByShardId(i);
			for(Peer peer: peers) {
				if(peer.isConnected()) {
					count++;
				}
			}
			peerCount.put(i, count);
		}
		int min = Collections.min(peerCount.values());
		return min >= getToPeersMaxSize();
	}
	
	public boolean isPeerSetFull(int shardId) {

		int count = 0;
		List<Peer> peers = getPeersByShardId(shardId);
		for (Peer peer : peers) {
			if (peer.isConnected()) {
				count++;
			}
		}
		return count >= getFromPeersMaxSize();
	}
	
	public Collection<Peer> getSkipPeers() {
		Set<String> connectedIds = getConnectedNetworkIds();
		Map<String, Peer> map = SkipPeers.getInstance().getMap();
		synchronized(map) {
			map.keySet().removeIf(id -> !connectedIds.contains(id));
			Set<Peer> result = new HashSet<Peer>(map.values());
			result.addAll(getConnectedPeers());
			return result;
		}
	}
	
	private static int getToPeersMaxSize() {
		return Configurator.getInstance().getIntProperty("org.tma.peer.peers.maxsize", 5);
	}
	
	private static int getFromPeersMaxSize() {
		return getToPeersMaxSize() * 2;
	}

	public boolean areMoreFromPeersAllowed() {
		return getFromPeers().size() < getFromPeersMaxSize() * (getBootstrapShardingPower() + 1) + 5;
	}

	public boolean isBootstrapShardingPowerUpdated() {
		return bootstrapShardingPowerUpdated;
	}

	public void setBootstrapShardingPowerUpdated(boolean bootstrapShardingPowerUpdated) {
		this.bootstrapShardingPowerUpdated = bootstrapShardingPowerUpdated;
	}

	public boolean isThin() {
		return thin;
	}

	

	

}
