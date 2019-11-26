/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post.key;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.tma.blockchain.Wallet;
import org.tma.persistance.Encryptor;
import org.tma.post.Caller;
import org.tma.post.Constants;
import org.tma.post.Wallets;
import org.tma.util.Base58;
import org.tma.util.StringUtil;

public class PasswordUtil {
	
	static {
		Security.setProperty("crypto.policy", "unlimited");
		Security.addProvider(new BouncyCastleProvider());
	}
	
	private static final Wallets wallets = Wallets.getInstance();
	private static final Encryptor encryptor = new Encryptor();
	private static final Logger logger = LogManager.getLogger();
	
	private Caller caller;
	
	public PasswordUtil(Caller caller) {
		this.caller = caller;
	}
	
	public boolean loadKeys(String passphrase) throws Exception {
		try (
				InputStream is = new FileInputStream(Constants.KEYS);
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
		) {
			String line;
			while ((line = in.readLine()) != null) {
				String[] strs = line.split(",");
				if(strs.length != 3) {
					continue;
				}
				Wallet wallet = new Wallet();
				String key = strs[0];
				PublicKey publicKey = StringUtil.loadPublicKey(strs[1]);
				PrivateKey privateKey = StringUtil.loadPrivateKey(encryptor.decrypt(passphrase, Base58.decode(strs[2])));
				if(privateKey == null) {
					caller.log("Passphrase entered was not correct. Try again.");
					return false;
				}
				wallet.setPrivateKey(privateKey);
				wallet.setPublicKey(publicKey);
				wallets.putWallet(key, wallet);
			}
		}
		return true;
	}
	
	public void saveKeys(String passphrase) throws Exception {
		File file = new File(Constants.KEYS);
		file.delete();
		try (
				OutputStream os = new FileOutputStream(Constants.KEYS);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
			) {
				for (String key : wallets.getKeys()) {
					Wallet wallet = wallets.getWallet(key);
					String publicKey = Base58.encode(wallet.getPublicKey().getEncoded());
					String privateKey = Base58.encode(encryptor.encrypt(passphrase, wallet.getPrivateKey().getEncoded()));
					out.write(key + "," + publicKey + "," + privateKey);
					out.newLine();
				}
				out.flush();
			}
	}
	
	public void generateKey(String key, String passphrase, String confirmPassword) {
		Wallet wallet = new Wallet();
		wallet.generateKeyPair();
		wallets.putWallet(key, wallet);
		try {
			saveKeys(passphrase);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
