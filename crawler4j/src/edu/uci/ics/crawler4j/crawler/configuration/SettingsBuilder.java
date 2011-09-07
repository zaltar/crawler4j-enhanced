package edu.uci.ics.crawler4j.crawler.configuration;

import java.util.Properties;
import edu.uci.ics.crawler4j.crawler.exceptions.CrawlerBuildError;

public class SettingsBuilder {
	private CrawlerSettings settings = new CrawlerSettings();
	
	public SettingsBuilder() {
		settings.setIncludeBinaryContent(false);
		settings.setMaxDepth((short)-1);
		settings.setFollowRedirects(true);
		settings.setMaxOutlinks(5000);
		settings.setIncludeImages(false);
		settings.setPolitenessDelay(200);
		settings.setMaxDownloadSize(1048576);
		settings.setShow404Pages(true);
		settings.setUserAgent("crawler4j");
		settings.setSocketTimeout(20000);
		settings.setConnectionTimeout(30000);
		settings.setMaxConnectionsPerHost(100);
		settings.setMaxTotalConnections(100);
		settings.setAllowHttps(false);
		settings.setEnableResume(true);
		settings.setStorageFolder("./db");
		settings.setMaxPagesToFetch(-1);
		settings.setRobotstxtMapSize(100);
		settings.setObeyRobotstxt(false);
		settings.setNumberOfCrawlerThreads(10);
	}
	
	public SettingsBuilder loadProperties(Properties p) {
		settings = new SettingsFromPropertiesBuilder(p).parseSettings();
		return this;
	}
	
	public SettingsBuilder mergeSettingsFromProperties(Properties p) {
		settings = new SettingsFromPropertiesBuilder(p).mergeSettings(settings);
		return this;
	}
	
	public SettingsBuilder setUserAgent(String userAgent) {
		settings.setUserAgent(userAgent);
		return this;
	}
	public SettingsBuilder setStorageFolder(String storageFolder) {
		settings.setStorageFolder(storageFolder);
		return this;
	}
	public SettingsBuilder setMaxDepth(short maxDepth) {
		settings.setMaxDepth(maxDepth);
		return this;
	}
	public SettingsBuilder setMaxOutlinks(int maxOutlinks) {
		settings.setMaxOutlinks(maxOutlinks);
		return this;
	}
	public SettingsBuilder setMaxDownloadSize(int maxDownloadSize) {
		settings.setMaxDownloadSize(maxDownloadSize);
		return this;
	}
	public SettingsBuilder setSocketTimeout(int socketTimeout) {
		settings.setSocketTimeout(socketTimeout);
		return this;
	}
	public SettingsBuilder setConnectionTimeout(int connectionTimeout) {
		settings.setConnectionTimeout(connectionTimeout);
		return this;
	}
	public SettingsBuilder setMaxTotalConnections(int maxTotalConnections) {
		settings.setMaxTotalConnections(maxTotalConnections);
		return this;
	}
	public SettingsBuilder setMaxPagesToFetch(int maxPagesToFetch) {
		settings.setMaxPagesToFetch(maxPagesToFetch);
		return this;
	}
	public SettingsBuilder setRobotstxtMapSize(int robotstxtMapSize) {
		settings.setRobotstxtMapSize(robotstxtMapSize);
		return this;
	}
	public SettingsBuilder setIncludeBinaryContent(boolean includeBinaryContent) {
		settings.setIncludeBinaryContent(includeBinaryContent);
		return this;
	}
	public SettingsBuilder setFollowRedirects(boolean followRedirects) {
		settings.setFollowRedirects(followRedirects);
		return this;
	}
	public SettingsBuilder setIncludeImages(boolean includeImages) {
		settings.setIncludeImages(includeImages);
		return this;
	}
	public SettingsBuilder setShow404Pages(boolean show404Pages) {
		settings.setShow404Pages(show404Pages);
		return this;
	}
	public SettingsBuilder setAllowHttps(boolean allowHttps) {
		settings.setAllowHttps(allowHttps);
		return this;
	}
	public SettingsBuilder setEnableResume(boolean enableResume) {
		settings.setEnableResume(enableResume);
		return this;
	}
	public SettingsBuilder setObeyRobotstxt(boolean obeyRobotstxt) {
		settings.setObeyRobotstxt(obeyRobotstxt);
		return this;
	}
	public SettingsBuilder setMaxConnectionsPerHost(int maxConnectionsPerHost) {
		settings.setMaxConnectionsPerHost(maxConnectionsPerHost);
		return this;
	}
	public SettingsBuilder setPolitenessDelay(int politenessDelay) {
		settings.setPolitenessDelay(politenessDelay);
		return this;
	}
	public SettingsBuilder setNumberOfCrawlerThreads(int threads) {
		settings.setNumberOfCrawlerThreads(threads);
		return this;
	}
	
	public CrawlerSettings build() throws CrawlerBuildError {
		try {
			return (CrawlerSettings)settings.clone();
		} catch (CloneNotSupportedException e) {
			throw new CrawlerBuildError("Unable to build crawler settings due to clone error.", e);
		}
	}
}
