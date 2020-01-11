/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class InetSocketAddressSerialiser implements JsonSerializer<InetSocketAddress>, JsonDeserializer<InetSocketAddress> {

	public JsonElement serialize(InetSocketAddress inetSocketAddress, Type type, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
	    jsonObject.addProperty("host", inetSocketAddress.getHostName());
	    jsonObject.addProperty("port", inetSocketAddress.getPort());
	    return jsonObject;
	}

	public InetSocketAddress deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
	    JsonElement jsonHost = jsonObject.get("host");
	    String host = jsonHost.getAsString();
	    JsonElement jsonPort = jsonObject.get("port");
	    int port = jsonPort.getAsInt();
		return new InetSocketAddress(host, port);
	}

}
