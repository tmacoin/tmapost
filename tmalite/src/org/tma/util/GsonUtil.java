/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.BitSet;

import org.tma.peer.DisconnectResponse;
import org.tma.peer.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GsonUtil {
	
	private static final GsonUtil instance = new GsonUtil();
	private static final TmaLogger logger = TmaLogger.getLogger();
	
	private Gson gson = initializeGson();
	
	public static GsonUtil getInstance() {
		return instance;
	}
	
	private Gson initializeGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(PublicKey.class, new PublicKeySerialiser());
		builder.registerTypeAdapter(InetSocketAddress.class, new InetSocketAddressSerialiser());
		builder.registerTypeAdapter(BitSet.class, new BitSetSerialiser());
        Gson gson = builder.setLenient().create();
        return gson;
	}

	public Gson getGson() {
		return gson;
	}
	
	public void write(Message message, JsonWriter writer) throws IOException {
		synchronized (writer) {
			JsonClassMarker marker = new JsonClassMarker(message.getClass());
			getGson().toJson(marker, JsonClassMarker.class, writer);
			message.setTimestamp(System.currentTimeMillis());
			getGson().toJson(message, message.getClass(), writer);
			writer.flush();
		}
	}

	public Message read(JsonReader reader) {
		if(reader == null) {
			return null;
		}
		try {
			synchronized (reader) {
				JsonClassMarker marker = getGson().fromJson(reader, JsonClassMarker.class);
				if(marker == null || marker.getType() == null) {
					return null;
				}
				Message message = getGson().fromJson(reader, marker.getType());
				return message;
			}
		} catch (IllegalStateException e) {
			if(e.getMessage().startsWith("Expected BEGIN_OBJECT but was END_DOCUMENT")) {
				return new DisconnectResponse();
			}
			logger.error(e.getMessage(), e);
			throw e;
		} catch(JsonSyntaxException e) {
			if(e.getCause().getMessage().startsWith("Expected BEGIN_OBJECT but was END_DOCUMENT")) {
				return new DisconnectResponse();
			}
			if(e.getCause().getMessage().equals("Socket closed") || e.getCause().getMessage().equals("Connection reset")) {
				return new DisconnectResponse();
			}
			logger.error(e.getMessage(), e);
			throw e;
			
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

}
