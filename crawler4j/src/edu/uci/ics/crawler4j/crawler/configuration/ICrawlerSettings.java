package edu.uci.ics.crawler4j.crawler.configuration;

import edu.uci.ics.crawler4j.crawler.IPageVisitValidator;
import edu.uci.ics.crawler4j.crawler.IPageVisited;
import edu.uci.ics.crawler4j.crawler.fetcher.IPageFetcher;
import edu.uci.ics.crawler4j.extractor.IPageParser;
import edu.uci.ics.crawler4j.extractor.PageParserManager;
import edu.uci.ics.crawler4j.frontier.ICrawlState;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public interface ICrawlerSettings extends Cloneable {

	public boolean getIncludeBinaryContent();
	
	public short getMaxDepth();
	
	public boolean getFollowRedirects();
	
	public int getMaxOutlinks();
	
	public boolean getIncludeImages();
	
	public int getPolitenessDelay();
	
	public int getMaxDownloadSize();
	
	public boolean getShow404Pages();
	
	public String getUserAgent();
	
	public int getSocketTimeout();
	
	public int getConnectionTimeout();
	
	public int getMaxConnectionsPerHost();
	
	public int getMaxTotalConnections();
	
	public boolean getAllowHttps();
	
	public boolean getEnableResume();
	
	public String getStorageFolder();
	
	public int getMaxPagesToFetch();
	
	public int getRobotstxtMapSize();
	
	public boolean getObeyRobotstxt();
	
	public int getNumberOfCrawlerThreads();
	
	public IPageVisitValidator getPageVisitValidator();
	
	public PageParserManager getPageParserManager();
	
	public ICrawlState getCrawlState();
	
	public IPageFetcher getPageFetcher();
	
	public RobotstxtServer getRobotstxtServer();
	
	public IPageVisited getPageVisitedCallback();
	
	public Object clone() throws CloneNotSupportedException;
}
