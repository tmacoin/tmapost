/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.web.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.tma.blockchain.Wallet;
import org.tma.persistance.Encryptor;
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

	
	public PasswordUtil() {

	}
	
	public boolean loadKeys(String passphrase) throws Exception {
		try (
				InputStream is = new FileInputStream(Constants.KEYS);
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
		) {
			String line;
			int i = 0;
			while ((line = in.readLine()) != null) {
				List<String> list =  new ArrayList<String>(Arrays.asList(line.split(",")));
				int size = list.size();
				if(size != 4 && size != 2) {
					continue;
				}
				Wallet wallet = new Wallet();
				if(size == 2) {
					list.add(0, Integer.toString(i));
					list.add(0, Wallets.TMA);
				}
				
				String application = list.get(0);
				String name = list.get(1);
				PublicKey publicKey = StringUtil.loadPublicKey(list.get(2));
				PrivateKey privateKey = StringUtil.loadPrivateKey(encryptor.decrypt(passphrase, Base58.decode(list.get(3))));
				if(privateKey == null) {
					logger.debug("Passphrase entered was not correct. Try again.");
					return false;
				}
				wallet.setPrivateKey(privateKey);
				wallet.setPublicKey(publicKey);
				wallets.putWallet(application, name, wallet);
				i++;
			}
		}
		if(wallets.getNames(Wallets.TMA).isEmpty()) {
			generateKey(Wallets.TMA, Wallets.WALLET_NAME, passphrase);
			return true;
		}
		saveKeys(passphrase);
		return true;
	}
	
	public void saveKeys(String passphrase) throws Exception {
		File file = new File(Constants.KEYS);
		try {
			if(file.exists()) {
				Files.copy(file.toPath(), new File(Constants.KEYS + ".backup").toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		file.delete();
		try (
				OutputStream os = new FileOutputStream(Constants.KEYS);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
			) {
				for (String application : wallets.getApplications()) {
					for(String name: wallets.getNames(application) ) {
						Wallet wallet = wallets.getWallet(application, name);
						String publicKey = Base58.encode(wallet.getPublicKey().getEncoded());
						String privateKey = Base58.encode(encryptor.encrypt(passphrase, wallet.getPrivateKey().getEncoded()));
						out.write(application + "," + name + "," + publicKey + "," + privateKey);
						out.newLine();
					}
					
				}
				out.flush();
			}
	}
	
	public void generateKey(String application, String name, String passphrase) {
		Wallet wallet = new Wallet();
		wallet.generateKeyPair();
		wallets.putWallet(application, name, wallet);
		logger.debug("New key was generated for application " + application + " with name " + name + " and address " + wallet.getTmaAddress());
		try {
			saveKeys(passphrase);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
