package org.tma.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jcajce.provider.digest.SHA3.Digest256;

public class StringUtil {
	
	private static final TmaLogger logger = TmaLogger.getLogger();
	public static final byte[] MAINNET = new byte[]{0, 0};
	public static final byte[] BLOCKCHAIN_TYPE = MAINNET;

	public static boolean equals(String str1, String str2) {
		if(str1 != null) {
			return str1.equals(str2);
		}
		return str2 == null;
	}
	
	public static String trim(String str) {
		if(str == null) {
			return null;
		}
		return str.trim();
	}
	
	public static String trimToBlank(String str) {
		if(str == null) {
			return "";
		}
		return str.trim();
	}
	
	public static String trimToNull(String str) {
		String result = str;
		if(result == null) {
			return null;
		}
		result = result.trim();
		result = "".equals(result)? null: result;
		return result;
	}
	
	public static String truncate(String str, int limit) {
		String result = str;
		if(result == null) {
			return null;
		}
		if (result.length() > limit) {
			result = result.substring(0, limit);
		}
		return result;
	}

	public static boolean isEmpty(String text) {
		return trimToNull(text) == null;
	}
	
	public static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
		    formatter.format("%02x", b);
		}
		String hex = formatter.toString();
		formatter.close();
		return hex;
	}
	
	public static byte[] fromHexString(String s) {
		int length = s.length() / 2;
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			bytes[i] = (byte) ((Character.digit(s.charAt(i * 2), 16) << 4) | Character.digit(s.charAt((i * 2) + 1), 16));
		}
		return bytes;
	}
	
	public static int compareVersions(String version1, String version2) {
		if(version1 == null || "".equals(version1)) {
			return -1;
		}
		String[] split = version1.split("\\.");
		int[] ver1 = new int[] {Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])};
		split = version2.split("\\.");
		int[] ver2 = new int[] {Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])};
		if(ver1[0] > ver2[0]) {
			return 1;
		}
		if(ver1[0] < ver2[0]) {
			return -1;
		}
		if(ver1[1] > ver2[1]) {
			return 1;
		}
		if(ver1[1] < ver2[1]) {
			return -1;
		}
		if(ver1[2] > ver2[2]) {
			return 1;
		}
		if(ver1[2] < ver2[2]) {
			return -1;
		}
		return 0;
	}
	
	public static PublicKey loadPublicKey(String key58) throws GeneralSecurityException {
	    byte[] clear = Base58.decode(key58);
	    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(clear);
	    KeyFactory fact = KeyFactory.getInstance("ECDSA");
	    PublicKey key = fact.generatePublic(keySpec);
	    Arrays.fill(clear, (byte) 0);
	    return key;
	}
	
	public static int getShard(String address, int power) {
		if(power == 0) {
			return 0;
		}
		byte[] bytes = Base58.decode(address);
		String str = "";
		for(byte b: bytes) {
			str = str + Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		}
		str = str.substring(0, power);
		return Integer.valueOf(str, 2);
	}
	
	public static boolean isTmaAddressValid(String tmaAddress) {
		tmaAddress = trim(tmaAddress);
		if("".equals(tmaAddress) || tmaAddress == null) {
			return false;
		}
		try {
			byte[] addressBytes = Base58.decode(tmaAddress);
			byte[] ripemdHash = new byte[addressBytes.length - 4];
			System.arraycopy(addressBytes, 0, ripemdHash, 0, ripemdHash.length);
			
			MessageDigest sha3 = new Digest256();
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

			byte[] sha256Hash = sha256.digest(sha3.digest(ripemdHash));

			return ripemdHash[ripemdHash.length - 2] == BLOCKCHAIN_TYPE[0] 
					&& ripemdHash[ripemdHash.length - 1] == BLOCKCHAIN_TYPE[1] 
					&& sha256Hash[0] == addressBytes[ripemdHash.length]
					&& sha256Hash[1] == addressBytes[ripemdHash.length + 1] 
					&& sha256Hash[2] == addressBytes[ripemdHash.length + 2] 
					&& sha256Hash[3] == addressBytes[ripemdHash.length + 3];
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
	
	public static String applySha256(String input) {

		try {
			byte[] hash = getBytesSha256(input);
	        
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static byte[] getBytesSha256(String input) throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("SHA-256").digest(new Digest256().digest(input.getBytes(StandardCharsets.UTF_8)));
	}
	
	public static String getStringFromKey(Key key) {
		byte[] publicKey = key.getEncoded();
		return getTmaAddressFromBytes(publicKey);
	}
	
	private static String getTmaAddressFromBytes(byte[] bytes) {
		RIPEMD160Digest ripemd160 = new RIPEMD160Digest();
		MessageDigest sha256;
		MessageDigest sha3 = new Digest256();
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		byte[] sha256Hash = sha256.digest(sha3.digest(bytes));

		byte[] ripemdHash = new byte[ripemd160.getDigestSize() + 2];
		ripemd160.update(sha256Hash, 0, sha256Hash.length);
		ripemd160.doFinal(ripemdHash, 0);

		// Set version bytes
		ripemdHash[ripemdHash.length-2] = BLOCKCHAIN_TYPE[0];
		ripemdHash[ripemdHash.length-1] = BLOCKCHAIN_TYPE[1];

		sha256Hash = sha3.digest(ripemdHash);
		sha256Hash = sha256.digest(sha256Hash);

		byte[] addressBytes = new byte[ripemdHash.length + 4];

		System.arraycopy(ripemdHash, 0, addressBytes, 0, ripemdHash.length);
		System.arraycopy(sha256Hash, 0, addressBytes, (ripemdHash.length), 4);

		return Base58.encode(addressBytes);
	}
	
	public static String getMerkleTreeRoot(List<String> strings) {
		Collections.sort(strings);
		int count = strings.size();
		
		List<String> previousTreeLayer = new ArrayList<String>();
		for(String string : strings) {
			previousTreeLayer.add(string);
		}
		List<String> treeLayer = previousTreeLayer;
		
		while(count > 1) {
			treeLayer = new ArrayList<String>();
			for(int left=0; left < previousTreeLayer.size(); left += 2) {
				int right = Math.min(left + 1, previousTreeLayer.size() - 1);
				treeLayer.add(applySha256(previousTreeLayer.get(left) + previousTreeLayer.get(right)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}
	
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		//windows-1252
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes(StandardCharsets.UTF_8));
			boolean result = ecdsaVerify.verify(signature);
			if(!result) {
				logger.debug("UTF_8 encoding failed will try windows-1252");
				ecdsaVerify.update(data.getBytes("windows-1252"));
				result = ecdsaVerify.verify(signature);
			}
			return result;
		} catch (Exception e) {
			logger.error("publicKey={}, data={}, signature={}", publicKey, data, toHexString(signature));
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	//Applies ECDSA Signature and returns the result ( as bytes ).
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes(StandardCharsets.UTF_8);
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}
	
	public static PrivateKey loadPrivateKey(byte[] clear) throws GeneralSecurityException {
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
			KeyFactory fact = KeyFactory.getInstance("ECDSA");
			PrivateKey key = fact.generatePrivate(keySpec);
			Arrays.fill(clear, (byte) 0);
			return key;
		} catch (InvalidKeySpecException e) {
			
		}
		return null;
	}
	
	public static String getTmaAddressFromString(String str) {
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		return getTmaAddressFromBytes(bytes);
	}
	
	public static int getShardForNonTmaAddress(String input, int power) {
		if(power == 0) {
			return 0;
		}
		byte[] bytes = null;
		try {
			bytes = StringUtil.getBytesSha256(input);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
		String str = "";
		for(byte b: bytes) {
			str = str + Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		}
		str = str.substring(0, power);
		return Integer.valueOf(str, 2);
	}

}
