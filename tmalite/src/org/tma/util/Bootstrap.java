/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.tma.peer.Network;
import org.tma.peer.Peer;

public class Bootstrap {
	
	private static final TmaLogger logger = TmaLogger.getLogger();
	
	private Set<Peer> getBootstrapPeers(String config) {
		File file = new File(config);
		if(!file.exists()) {
			createAndCopy(config);
		}
		Set<Peer> set = new HashSet<Peer>();
		try (
			InputStream is = new FileInputStream(config);
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
		) {
			String line;
			while ((line = in.readLine()) != null) {
				try {
					line = line.trim();
					String[] strs = line.split(":");
					if (strs.length != 2) {
						continue;
					}
					String ip = strs[0].trim();
					int port = Integer.parseInt(strs[1].trim());
					InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(ip), port);
					Peer peer = Peer.getInstance(address);
					peer.setResponseCounter(1);//to ignore network.isPeerSetFull() since connecting to peer myself
					set.add(peer);
				} catch (UnknownHostException e) {
					//logger.error("UnknownHostException : {}");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return set;
	}

	private void createAndCopy(String config) {
		String resource = "";
		if(config.endsWith("locals.config")) {
			resource = "org/tma/resources/locals.config";
		}
		if(config.endsWith("peers.config")) {
			resource = "org/tma/resources/peers.config";
		}
		try (
				InputStream is = getClass().getClassLoader().getResourceAsStream(resource);
				OutputStream os = new FileOutputStream(config);
		) {
			
			int b;
			while((b = is.read()) != -1) {
				os.write(b);
			}
		} catch(Exception e) {
			logger.debug(e.getMessage(), e);
		}
		
	}

	public void addPeers(Network network) {
		addListedPeers(network);
	}
	
	public void addListedPeers(Network network) {
		network.addBootstrap(getBootstrapPeers(Constants.FILES_DIRECTORY + "config/peers.config"));
		network.addLocals(getBootstrapPeers(Constants.FILES_DIRECTORY + "config/locals.config"));
	}
	
	public void addLocals(Network network) {
		network.addLocals(getBootstrapPeers(Constants.FILES_DIRECTORY + "config/locals.config"));
	}

}
