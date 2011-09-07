package edu.uci.ics.crawler4j.crawler.configuration;

import java.util.Properties;

public final class SettingsFromPropertiesBuilder  {
	private Properties props;
	
	public SettingsFromPropertiesBuilder(Properties properties)
	{
		this.props = properties;
	}
	
	public CrawlerSettings parseSettings() {
		return mergeSettings(new CrawlerSettings());
	}
	
	public CrawlerSettings mergeSettings(CrawlerSettings settings) {
		String tmpStr;
		Integer tmpInt;
		Boolean tmpBool;
		
		if ((tmpBool = getBooleanProperty("crawler.include_binary_content")) != null)
			settings.setIncludeBinaryContent(tmpBool.booleanValue());
		
		if ((tmpInt = getIntProperty("crawler.max_depth")) != null)
			settings.setMaxDepth(tmpInt.shortValue());
		
		if ((tmpBool = getBooleanProperty("fetcher.foolow_redirects")) != null)
			settings.setFollowRedirects(tmpBool.booleanValue());
		
		if ((tmpInt = getIntProperty("fetcher.max_outlinks")) != null)
			settings.setMaxOutlinks(tmpInt.intValue());
		
		if ((tmpBool = getBooleanProperty("crawler.include_images")) != null)
			settings.setIncludeImages(tmpBool.booleanValue());
		
		if ((tmpInt = getIntProperty("fetcher.default_politeness_delay")) != null)
			settings.setPolitenessDelay(tmpInt.intValue());
		
		if ((tmpInt = getIntProperty("fetcher.max_download_size")) != null)
			settings.setMaxDownloadSize(tmpInt.intValue());
			
		if ((tmpBool = getBooleanProperty("logging.show_404_pages")) != null)
			settings.setShow404Pages(tmpBool.booleanValue());
		
		if ((tmpStr = getStringProperty("fetcher.user_agent")) != null)
			settings.setUserAgent(tmpStr);
		
		if ((tmpInt = getIntProperty("fetcher.socket_timeout")) != null)
			settings.setSocketTimeout(tmpInt.intValue());
		
		if ((tmpInt = getIntProperty("fetcher.connection_timeout")) != null)
			settings.setConnectionTimeout(tmpInt.intValue());
		
		if ((tmpInt = getIntProperty("fetcher.max_connections_per_host")) != null)
			settings.setMaxConnectionsPerHost(tmpInt.intValue());
		
		if ((tmpInt = getIntProperty("max_total_connections")) != null)
			settings.setMaxTotalConnections(tmpInt.intValue());
		
		if ((tmpBool = getBooleanProperty("fetcher.crawl_https")) != null)
			settings.setAllowHttps(tmpBool.booleanValue());
		
		if ((tmpBool = getBooleanProperty("crawler.enable_resume")) != null)
			settings.setEnableResume(tmpBool.booleanValue());
		
		if ((tmpStr = getStringProperty("crawler.storage_folder")) != null)
			settings.setStorageFolder(tmpStr);
		
		if ((tmpInt = getIntProperty("crawler.max_pages_to_fetch")) != null)
			settings.setMaxPagesToFetch(tmpInt.intValue());
		
		if ((tmpInt = getIntProperty("crawler.robotstxt.max_map_size")) != null)
			settings.setRobotstxtMapSize(tmpInt.intValue());
		
		if ((tmpBool = getBooleanProperty("crawler.obey_robotstxt")) != null)
			settings.setObeyRobotstxt(tmpBool.booleanValue());
		
		return settings;
	}
	
	private String getStringProperty(String key) {
		String tmp;
		if ((tmp = props.getProperty(key)) == null)
			return null;

		return tmp;
	}
	
	private Integer getIntProperty(String key) {
		String tmp;
		if ((tmp = props.getProperty(key)) == null)
			return null;

		try {
			return Integer.valueOf(tmp);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
	
	private Boolean getBooleanProperty(String key) {
		String tmp;
		if ((tmp = props.getProperty(key)) == null)
			return null;

		return Boolean.valueOf(tmp);
	}
}
