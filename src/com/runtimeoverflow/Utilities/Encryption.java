package com.runtimeoverflow.Utilities;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
	//Function to encrypt a string with the passed key
	public static String encrypt(String text, String key) {
		try {
			//Hashing the key using SHA-256
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			SecretKeySpec keySpec = new SecretKeySpec(digest.digest(key.getBytes(StandardCharsets.UTF_8)), "AES");
			
			//Encrypting the string with the hashed key using AES and base64 encoding the result
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			return new String(Base64.getEncoder().encode(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	//Function to decrypt a base64 encoded string with the passed key
	public static String decrypt(String encrypted, String key) {
		try {
			//Hashing the key using SHA-256
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			SecretKeySpec keySpec = new SecretKeySpec(digest.digest(key.getBytes(StandardCharsets.UTF_8)), "AES");
			
			//Decode the encrypted string with base64 and decrypting it with the hashed key using AES
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
