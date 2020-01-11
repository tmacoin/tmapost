/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExecutor {
	
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static ThreadExecutor instance = new ThreadExecutor();
	private ExecutorService exService = Executors.newCachedThreadPool();
	
	public static ThreadExecutor getInstance() {
		return instance;
	}

	public void execute(Runnable runnable) {
		exService.submit(runnable);
	}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {

		}
	}
	
	public static void logPid(int port) {
		try {
			String jvmName = ManagementFactory.getRuntimeMXBean().getName();
			logger.debug("jvmName={}", jvmName);
			File file = new File("./log/" + port + ".txt");
			file.getParentFile().mkdirs();
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream os = new FileOutputStream(file);
			Writer w = new OutputStreamWriter(os, StandardCharsets.UTF_8);
			w.write(jvmName);
			w.flush();
			w.close();
			os.close();
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}

}
