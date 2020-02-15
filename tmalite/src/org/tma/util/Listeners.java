/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Listeners {
	
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Listeners instance = new Listeners();
	
	private static final Map<String, Listener> map = new HashMap<String, Listener>();
	private static final Map<Class<? extends Event>, Set<EventListener>> listeners = new HashMap<Class<? extends Event>, Set<EventListener>>();
	
	public static Listeners getInstance() {
		return instance;
	}
	
	public void addListener(String key, Listener listener) {
		map.put(key, listener);
	}
	
	public void addEventListener(Class<? extends Event> eventClass, EventListener listener) {
		Set<EventListener> set = listeners.get(eventClass);
		if(set == null) {
			set = new HashSet<EventListener>();
			listeners.put(eventClass, set);
		}
		set.add(listener);
	}
	
	public void sendEvent(final Event event) {
		ThreadExecutor.getInstance().execute(new TmaRunnable("Process event " + event.getClass()) {
			public void doRun() {
				Set<EventListener> set = listeners.get(event.getClass());
				if (set == null) {
					return;
				}
				for (EventListener listener : set) {
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
