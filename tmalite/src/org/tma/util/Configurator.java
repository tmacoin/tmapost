/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Configurator {

	private static final TmaLogger logger = TmaLogger.getLogger();

	private static Configurator instance = new Configurator();

	private Properties props = null;

	private Configurator() {
		props = new Properties();
		try {
			Path filePath = Paths.get(Constants.FILES_DIRECTORY + "config/tma.properties");
			if (!Files.exists(filePath)) {
				try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
					writer.newLine();
					writer.close();
				}
			}
			props.load(new FileInputStream(Constants.FILES_DIRECTORY + "config/tma.properties"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static Configurator getInstance() {
		return instance;
	}

	public String getProperty(String key) {
		return (String) props.get(key);
	}
	
	public int getIntProperty(String key) {
		String str = getProperty(key);
		if(str == null) {
			return 0;
		}
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int getIntProperty(String key, int defaultValue) {
		int result = getIntProperty(key);
		result = result == 0 ? defaultValue : result;
		return result;
	}
	
	public boolean getBooleanProperty(String key) {
		String str = getProperty(key);
		return "true".equals(str);
	}
}