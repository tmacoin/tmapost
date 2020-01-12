/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listeners {
	
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Listeners instance = new Listeners();
	
	private static final Map<String, Listener> map = new HashMap<String, Listener>();
	private static final Map<Class<? extends Event>, List<EventListener>> listeners = new HashMap<Class<? extends Event>, List<EventListener>>();
	
	public static Listeners getInstance() {
		return instance;
	}
	
	public void addListener(String key, Listener listener) {
		map.put(key, listener);
	}
	
	public void addEventListener(Class<? extends Event> eventClass, EventListener listener) {
		List<EventListener> list = listeners.get(eventClass);
		if(list == null) {
			list = new ArrayList<EventListener>();
			listeners.put(eventClass, list);
		}
		list.add(listener);
	}
	
	public void sendEvent(final Event event) {
		ThreadExecutor.getInstance().execute(new TmaRunnable("Process event " + event.getClass()) {
			public void doRun() {
				List<EventListener> list = listeners.get(event.getClass());
				if (list == null) {
					return;
				}
				for (EventListener listener : list) {
					try {
						listener.onEvent(event);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		});
	}
	
	public void call(String message) {
		for(Listener listener: map.values()) {
			listener.call(message);
		}
	}

}
