package com.einnfeigr.taskApp.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class Util {

	private static Properties props;	
	public static final String EXCEPTION_LOG_MESSAGE = 
			"exception has been caught";

	public Util() {
		if(props == null) {
			props = new Properties();
			Map<String, String> env = System.getenv();
			if(env != null) {
				props.putAll(env);
			}
		}
	}
	
	public String getVar(String key) {
		if(!props.containsKey(key)) {
			return null;
		}
		return props.get(key).toString();
	}
	
	public void setVar(String key, String val) {
		props.put(key, val);
	}
	
	public void saveVar(String key, String val) {
		
	}
	
	public static List<Cloneable> cloneList(List<Cloneable> list) {
		List<Cloneable> cloned = new ArrayList<>();
		for(Cloneable cloneable : list) {
			cloned.add(cloneable);
		}
		return cloned;
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
	
	public static String getLocalizedMonthNameByNum(int number) {
		switch(number) {
		case(1):
			return "Январь";
		case(2):
			return "Февраль";
		case(3):
			return "Март";
		case(4):
			return "Апрель";
		case(5):
			return "Май";
		case(6):
			return "Июнь";
		case(7):
			return "Июль";
		case(8):
			return "Август";
		case(9):
			return "Сентябрь";
		case(10):
			return "Октябрь";
		case(11):
			return "Ноябрь";
		case(12):
			return "Декабрь";
		default:
			return "-";
		}
	}
	
	public static boolean validateName(String name) {
		if(name == null || name.length() < 1 || name.equals(" ")) {
			return false;
		}
		return true;
	}
	
	
}
