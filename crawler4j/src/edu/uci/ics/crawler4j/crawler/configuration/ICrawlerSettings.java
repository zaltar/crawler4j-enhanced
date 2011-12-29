package edu.uci.ics.crawler4j.crawler.configuration;

import edu.uci.ics.crawler4j.crawler.IPageVisitValidator;
import edu.uci.ics.crawler4j.crawler.IPageVisited;
import edu.uci.ics.crawler4j.crawler.fetcher.IPageFetcher;
import edu.uci.ics.crawler4j.extractor.PageParserManager;
import edu.uci.ics.crawler4j.frontier.ICrawlState;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.cache.ICacheProvider;

public interface ICrawlerSettings extends Cloneable {
	boolean getIncludeBinaryContent();
	short getMaxDepth();
	boolean getFollowRedirects();
	int getMaxOutlinks();
	boolean getIncludeImages();
	int getPolitenessDelay();
	int getMaxDownloadSize();
	boolean getShow404Pages();
	String getUserAgent();
	int getSocketTimeout();
	int getConnectionTimeout();
	int getMaxConnectionsPerHost();
	int getMaxTotalConnections();
	boolean getAllowHttps();
	boolean getEnableResume();
	String getStorageFolder();
	int getMaxPagesToFetch();
	int getRobotstxtMapSize();
	boolean getObeyRobotstxt();
	int getNumberOfCrawlerThreads();
	IPageVisitValidator getPageVisitValidator();
	PageParserManager getPageParserManager();
	ICrawlState getCrawlState();
	IPageFetcher getPageFetcher();
	RobotstxtServer getRobotstxtServer();
	IPageVisited getPageVisitedCallback();
	ICacheProvider getCacheProvider();
	Object clone() throws CloneNotSupportedException;
}
