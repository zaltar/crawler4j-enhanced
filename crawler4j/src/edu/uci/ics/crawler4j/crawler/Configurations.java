package edu.uci.ics.crawler4j.crawler;

import java.util.Properties;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public final class Configurations {

	private static Properties prop = new Properties();

	public static String getStringProperty(String key, String defaultValue) {
		if (prop == null) {
			return defaultValue;
		}
		return prop.getProperty(key);
	}

	public static int getIntProperty(String key, int defaultValue) {
		if (prop == null) {
			return defaultValue;
		}
		return Integer.parseInt(prop.getProperty(key));
	}

	public static long getLongProperty(String key, long defaultValue) {
		if (prop == null) {
			return defaultValue;
		}
		return Long.parseLong(prop.getProperty(key));
	}
	
	public static boolean getBooleanProperty(String key, boolean defaultValue) {
		if (prop == null) {
			return defaultValue;
		}
		return prop.getProperty(key).toLowerCase().trim().equals("true");
	}

	static {
		try {
			prop.load(Configurations.class.getClassLoader()
					.getResourceAsStream("crawler4j.properties"));
		} catch (Exception e) {
			prop = null;
			System.err.println("WARNING: Could not find crawler4j.properties file in class path. I will use the default values.");
		}
	}
}
