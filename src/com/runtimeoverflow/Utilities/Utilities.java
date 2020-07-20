package com.runtimeoverflow.Utilities;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JTextArea;

public class Utilities {
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
	
	//Utility method for calculation the amount of rows for the body
	public static int countLines(JTextArea textArea) {
		int count = 1;
		
		//Get an array for each word separated by a space
		String[] words = textArea.getText().split(" ");
		String currentLine = "";
		
		//Iterate through all words (Note that a word gets separated by a string so iOS\nAndroid would be considered one word)
		for(String wordWithMaybeANewLine : words) {
			//Split the word again , this time using \n as a separator
			for(int i = 0; i < wordWithMaybeANewLine.split("\n").length; i++) {
				String word = wordWithMaybeANewLine.split("\n")[i];
				
				//If one word is longer than a line (split this word into multiple parts for each line)
				if(textArea.getFontMetrics(textArea.getFont()).stringWidth(word) > textArea.getWidth()) {
					ArrayList<String> splitWord = new ArrayList<String>();
					char[] chars = word.toCharArray();
					
					//Iterate through all characters
					String split = "";
					for(int i2 = 0; i2 < chars.length; i2++) {
						String letter = Character.toString(chars[i2]);
						
						//If there is still space for another character add it to this split
						if(textArea.getFontMetrics(textArea.getFont()).stringWidth(split + letter) <= textArea.getWidth()) {
							split += letter;
						} else {
							//If there is no space for another character, add this split to the array and start a new split with the character
							splitWord.add(split);
							
							//Quick test if the rest of the characters fit into one line
							String rest = new String(Arrays.copyOfRange(chars, i2, chars.length));
							if(textArea.getFontMetrics(textArea.getFont()).stringWidth(rest) <= textArea.getWidth()) {
								//If it fits, add it to the slits array and break the loop (This saves iterating through the rest of the characters)
								splitWord.add(rest);
								break;
							}
							
							split = letter;
						}
					}
					
					//Each entry in the array is one line except for the last one
					count += splitWord.size() - 1;
					
					//Add the last entry to the current line (there may fit another word in this line)
					currentLine = splitWord.get(splitWord.size() - 1);
				}
				
				//If the word doesn't fit in the line, raise the line count and add this word to the next line
				if(textArea.getFontMetrics(textArea.getFont()).stringWidth(currentLine + " " + word) > textArea.getWidth() || i > 0) {
					currentLine = word;
					count++;
				} else {
					//If the word fits in this line, add it to the current line
					currentLine += " " + word;
				}
			}
		}
		
		return count;
	}
	
	//Creates a relative string from the date property
	public static String stringFromDate(Calendar date) {
		Calendar now = Calendar.getInstance();
		
		Calendar copy = Calendar.getInstance();
		
		//If the date is today
		if(date.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) && date.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
			//Return "now" if less than a minute passed
			copy.setTimeInMillis(date.getTimeInMillis());
			copy.add(Calendar.MINUTE, 1);
			if(copy.after(now)) {
				return "now";
			}
			
			//Return "Xm ago" if less than an hour passed
			copy.setTimeInMillis(date.getTimeInMillis());
			copy.add(Calendar.HOUR, 1);
			if(copy.after(now)) {
				return Integer.toString((int)((now.getTimeInMillis() - date.getTimeInMillis()) / 1000 / 60)) + "m ago";
			}
			
			//Return "Xh ago" if less than 4 hours passed
			copy.setTimeInMillis(date.getTimeInMillis());
			copy.add(Calendar.HOUR, 4);
			if(copy.after(now)) {
				return Integer.toString((int)((now.getTimeInMillis() - date.getTimeInMillis()) / 1000 / 60 / 60)) + "h ago";
			}
			
			//Return "HH:mm" if it was today
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
			return sdf.format(date.getTime());
		}
		
		//Return "Yesterday, HH:mm" if it was yesterday
		copy.setTimeInMillis(date.getTimeInMillis());
		copy.add(Calendar.DAY_OF_YEAR, 1);
		if(copy.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) && copy.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
			return "Yesterday, " + sdf.format(date.getTime());
		}
		
		//Return "EEE HH:mm" if less than a week passed
		copy.setTimeInMillis(date.getTimeInMillis());
		copy.add(Calendar.DAY_OF_YEAR, 7);
		copy.set(Calendar.HOUR_OF_DAY, 0);
		copy.set(Calendar.MINUTE, 0);
		copy.set(Calendar.SECOND, 0);
		if(copy.after(now)) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE HH:mm", Locale.ENGLISH);
			return sdf.format(date.getTime());
		}
		
		//Return "Xw ago" if less than a month passed
		copy.setTimeInMillis(date.getTimeInMillis());
		copy.add(Calendar.MONTH, 1);
		copy.set(Calendar.HOUR_OF_DAY, 0);
		copy.set(Calendar.MINUTE, 0);
		copy.set(Calendar.SECOND, 0);
		if(copy.after(now)) {
			return Integer.toString((int)((now.getTimeInMillis() - date.getTimeInMillis()) / 1000 / 60 / 60 / 24 / 7)) + "w ago";
		}
		
		//Return "Xmo ago" if less than a year passed
		copy.setTimeInMillis(date.getTimeInMillis());
		copy.add(Calendar.YEAR, 1);
		copy.set(Calendar.HOUR_OF_DAY, 0);
		copy.set(Calendar.MINUTE, 0);
		copy.set(Calendar.SECOND, 0);
		if(copy.after(now)) {
			return Integer.toString((now.get(Calendar.YEAR) * 12 + now.get(Calendar.MONTH)) - (date.get(Calendar.YEAR) * 12 + date.get(Calendar.MONTH))) + "mo ago";
		}
		
		//Return "Xy ago" if more than a year passed
		return Integer.toString(now.get(Calendar.YEAR) - date.get(Calendar.YEAR)) + "y ago";
	}
}
