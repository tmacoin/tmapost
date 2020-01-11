/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.util;

import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PublicKeySerialiser implements JsonSerializer<PublicKey>, JsonDeserializer<PublicKey> {
	
	private static final TmaLogger logger = TmaLogger.getLogger();

	public JsonElement serialize(PublicKey publicKey, Type type, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
	    jsonObject.addProperty("encoded", Base58.encode(publicKey.getEncoded()));
	    return jsonObject;
	}

	public PublicKey deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

	    JsonElement jsonEncoded = jsonObject.get("encoded");
	    String encoded = jsonEncoded.getAsString();
	    PublicKey publicKey = null;
	    try {
	    	publicKey = StringUtil.loadPublicKey(encoded);
		} catch (GeneralSecurityException e) {
			logger.error(e.getMessage(), e);
		}
	    
		return publicKey;
	}

}
