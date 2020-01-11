/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.io.Serializable;
import java.math.BigDecimal;

public class Coin implements Serializable {

	private static final long serialVersionUID = 8947997011552162831L;
	public static Coin ONE = new Coin(new BigDecimal("1000000000").longValue() );
	public static Coin ZERO = new Coin(0);
	public static Coin SATOSHI = new Coin(1);
	
	private long value;
	
	public Coin() {

	}

	public Coin(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	public Coin multiply(double amount) {

		return new Coin(   BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(value)).longValue()   );
	}

	public String toString() {
		return Long.toString(value);
	}

	public Coin subtract(Coin amount) {
		return new Coin(value - amount.getValue());
	}

	public Coin add(Coin amount) {
		return new Coin(value + amount.getValue());
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return this.value == ((Coin) o).value;
	}
	
	public boolean less(Coin coin) {
		return value < coin.getValue();
	}
	
	public boolean greaterOrEqual(Coin coin) {
		return value >= coin.getValue();
	}
	
	public String toNumberOfCoins() {
		return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(ONE.getValue())).toPlainString();
	}

}
