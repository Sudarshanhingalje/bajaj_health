package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGenerator {

	// here we created the file path destination for it 
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java -jar DestinationHashGenerator.jar <240345920089> <\"C:\\Users\\HP\\Desktop\\test.json\">");
			System.exit(1);
		}
		
		
		// it will take two arguments from here 
		String prnNumber = args[0].toLowerCase();
		String jsonFilePath = args[1];

		try {
			String destinationValue = findDestinationValue(jsonFilePath);
			String randomString = generateRandomString();
			String hashInput = prnNumber + destinationValue + randomString;
			String md5Hash = generateMD5Hash(hashInput);

			System.out.println(md5Hash + ";" + randomString);
		} 
		
		catch (IOException e) {
			System.out.println("Error reading JSON file: " + e.getMessage());
		} 
		
		catch (NoSuchAlgorithmException e) {
			System.out.println("Error generating MD5 hash: " + e.getMessage());
		}
	}

	private static String findDestinationValue(String jsonFilePath) throws IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));
		
		return traverseJson(rootNode);
	}

	private static String traverseJson(JsonNode node) {
		if (node.isObject()) {
			if (node.has("destination")) {
				return node.get("destination").asText();
			}
			for (JsonNode childNode : node) {
				String result = traverseJson(childNode);
				if (result != null) {
					return result;
				}
			}
		} else if (node.isArray()) {
			for (JsonNode arrayElement : node) {
				String result = traverseJson(arrayElement);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	private static String generateRandomString() {
		//give string is here 
		String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder(8);

		Random random = new Random();
		for (int i = 0; i < 8; i++) 
		{
			sb.append(alphanumeric.charAt(random.nextInt(alphanumeric.length())));
		}
		return sb.toString();
	}

	private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");

		byte[] messageDigest = md.digest(input.getBytes());
		BigInteger no = new BigInteger(1, messageDigest);
		String hashtext = no.toString(16);

		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}
		return hashtext;
	}
}