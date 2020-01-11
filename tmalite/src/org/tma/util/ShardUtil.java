/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShardUtil {

	public static List<Integer> getMateShardsId(int blockchainId, int power) {

		List<Integer> list = new ArrayList<Integer>();
		BigInteger two = new BigInteger("2");
		for (int i = 0; i < power + 1; i++) {
			list.add(blockchainId ^ (two.pow(i).intValue() - 1));
		}
		return list;
	}
	
	public static List<Integer> getCommonShards(int shard1, int shard2, int power) {
		List<Integer> list = getMateShardsId(shard1, power);
		list.retainAll(getMateShardsId(shard2, power));
		return list;
	}
	
	public static List<Integer> getPath(int start, int end, int power) {
		List<Integer> list = new ArrayList<Integer>();
		
		int next = start;
		while(next != end) {
			list.add(next);
			List<Integer> mates = getMateShardsId(next, power);
			int closestMate = next;
			for(int i: mates) {
				if((end ^ i) < (end ^ closestMate)) {
					closestMate = i;
				}
			}
			next = closestMate;
		}
		list.add(next);
		return list;
	}
	
	public static int getNext(int start, int end, int power) {
		if(start == end) {
			return end;
		}
		List<Integer> mates = getMateShardsId(start, power);
		int closestMate = start;
		for(int i: mates) {
			if((end ^ i) < (end ^ closestMate)) {
				closestMate = i;
			}
		}
		return closestMate;
	}

}
