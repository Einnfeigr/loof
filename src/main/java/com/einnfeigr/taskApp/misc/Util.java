package com.einnfeigr.taskApp.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

public class Util {

	private static Properties props;	
	public static final String EXCEPTION_LOG_MESSAGE = "exception has been caught";

	public Util() {
		if(props == null) {
			props = new Properties();
			Map<String, String> env = System.getenv();
			if(env != null) {
				props.putAll(env);
			}
		}
	}
	
	public static <T> Map<String, T> arrayToMap(T[] t) {
		Map<String, T> map = new HashMap<>();
		for(int x = 0; x+1 < t.length; x += 2) {
			if(t[x+1] == null) {
				continue;
			}
			map.put(t[x].toString(), t[x+1]);
		}
		return map;
	}

	public static boolean isNumeric(String text) {
		for(char c : text.toCharArray()) {
			if(Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}
	
	public static <T> List<Entry<String, T>> getByContainsInKey(String text,
			Map<String, T> map) {
		List<Entry<String, T>> entryList = new ArrayList<>();
		for(Entry<String, T> entry : map.entrySet()) {
			if(entry.getKey().contains(text)) {
				entryList.add(entry);
			}
		}
		return entryList;
	}

	public static boolean validateName(String name) {
		if(name == null || name.length() < 1 || name.equals(" ")) {
			return false;
		}
		return true;
	}
		
	public static String generateCode(int length) {
		return generateCode(length, new Random());
	}
	
	public static String generateCode(int length, Random random) {
		StringBuilder sb = new StringBuilder();
		for(char r = (char)random.nextInt(); sb.length() < length; r = (char)random.nextInt()) {
			if(r >= '0' && r <= '9') {
				sb.append(r);
			} 
		}
		return sb.toString();
	}
	
}
