package edu.uci.ics.crawler4j.crawler;

import java.util.Properties;

public final class Configurations {

	private static Properties prop = new Properties();

	public static String getStringProperty(String key) {
		return prop.getProperty(key);
	}

	public static int getIntProperty(String key) {
		return Integer.parseInt(prop.getProperty(key));
	}

	public static long getLongProperty(String key) {
		return Long.parseLong(prop.getProperty(key));
	}

	static {
		try {
			prop.load(Configurations.class.getClassLoader()
					.getResourceAsStream("config.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
