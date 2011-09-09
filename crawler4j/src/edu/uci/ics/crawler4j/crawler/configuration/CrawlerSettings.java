package edu.uci.ics.crawler4j.crawler.configuration;

import edu.uci.ics.crawler4j.extractor.PageParserManager;
import edu.uci.ics.crawler4j.frontier.ICrawlState;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.cache.ICacheProvider;
import edu.uci.ics.crawler4j.crawler.IPageVisitValidator;
import edu.uci.ics.crawler4j.crawler.IPageVisited;
import edu.uci.ics.crawler4j.crawler.fetcher.IPageFetcher;

public final class CrawlerSettings implements ICrawlerSettings {
	private String userAgent, storageFolder;
	private short maxDepth;
	private int maxOutlinks, maxDownloadSize, socketTimeout, connectionTimeout;
	private int maxTotalConnections, maxPagesToFetch, robotstxtMapSize;
	private int maxConnectionsPerHost, politenessDelay, crawlerThreads;
	private boolean includeBinaryContent, followRedirects, includeImages, show404Pages;
	private boolean allowHttps, enableResume, obeyRobotstxt;
	private IPageVisitValidator visitValidator;
	private IPageFetcher pageFetcher;
	private ICrawlState crawlState;
	private RobotstxtServer robotstxtServer;
	private PageParserManager pageParserManager;
	private IPageVisited pageVisitedCallback;
	private ICacheProvider cacheProvider;
	
	public Object clone() throws CloneNotSupportedException {
		CrawlerSettings clone = (CrawlerSettings)super.clone();
		//Reset instance specific classes
		clone.visitValidator = null;
		clone.pageFetcher = null;
		clone.crawlState = null;
		clone.robotstxtServer = null;
		clone.pageParserManager = null;
		clone.pageVisitedCallback = null;
		return clone;
	}
	
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getStorageFolder() {
		return storageFolder;
	}
	public void setStorageFolder(String storageFolder) {
		this.storageFolder = storageFolder;
	}
	public short getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(short maxDepth) {
		this.maxDepth = maxDepth;
	}
	public int getMaxOutlinks() {
		return maxOutlinks;
	}
	public void setMaxOutlinks(int maxOutlinks) {
		this.maxOutlinks = maxOutlinks;
	}
	public int getMaxDownloadSize() {
		return maxDownloadSize;
	}
	public void setMaxDownloadSize(int maxDownloadSize) {
		this.maxDownloadSize = maxDownloadSize;
	}
	public int getSocketTimeout() {
		return socketTimeout;
	}
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public int getMaxTotalConnections() {
		return maxTotalConnections;
	}
	public void setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}
	public int getMaxPagesToFetch() {
		return maxPagesToFetch;
	}
	public void setMaxPagesToFetch(int maxPagesToFetch) {
		this.maxPagesToFetch = maxPagesToFetch;
	}
	public int getRobotstxtMapSize() {
		return robotstxtMapSize;
	}
	public void setRobotstxtMapSize(int robotstxtMapSize) {
		this.robotstxtMapSize = robotstxtMapSize;
	}
	public boolean getIncludeBinaryContent() {
		return includeBinaryContent;
	}
	public void setIncludeBinaryContent(boolean includeBinaryContent) {
		this.includeBinaryContent = includeBinaryContent;
	}
	public boolean getFollowRedirects() {
		return followRedirects;
	}
	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}
	public boolean getIncludeImages() {
		return includeImages;
	}
	public void setIncludeImages(boolean includeImages) {
		this.includeImages = includeImages;
	}
	public boolean getShow404Pages() {
		return show404Pages;
	}
	public void setShow404Pages(boolean show404Pages) {
		this.show404Pages = show404Pages;
	}
	public boolean getAllowHttps() {
		return allowHttps;
	}
	public void setAllowHttps(boolean allowHttps) {
		this.allowHttps = allowHttps;
	}
	public boolean getEnableResume() {
		return enableResume;
	}
	public void setEnableResume(boolean enableResume) {
		this.enableResume = enableResume;
	}
	public boolean getObeyRobotstxt() {
		return obeyRobotstxt;
	}
	public void setObeyRobotstxt(boolean obeyRobotstxt) {
		this.obeyRobotstxt = obeyRobotstxt;
	}
	public int getMaxConnectionsPerHost() {
		return maxConnectionsPerHost;
	}
	public void setMaxConnectionsPerHost(int maxConnectionsPerHost) {
		this.maxConnectionsPerHost = maxConnectionsPerHost;
	}
	public void setPolitenessDelay(int politenessDelay) {
		this.politenessDelay = politenessDelay;
	}
	public int getPolitenessDelay() {
		return politenessDelay;
	}
	public int getNumberOfCrawlerThreads() {
		return crawlerThreads;
	}
	public void setNumberOfCrawlerThreads(int crawlerThreads) {
		this.crawlerThreads = crawlerThreads;
	}
	public void setPageVisitValidator(IPageVisitValidator visitValidator) {
		this.visitValidator = visitValidator;
	}
	public IPageVisitValidator getPageVisitValidator() {
		return visitValidator;
	}
	public void setPageFetcher(IPageFetcher pageFetcher) {
		this.pageFetcher = pageFetcher;
	}
	public IPageFetcher getPageFetcher() {
		return pageFetcher;
	}
	public void setCrawlState(ICrawlState crawlState) {
		this.crawlState = crawlState;
	}
	public ICrawlState getCrawlState() {
		return crawlState;
	}

	public void setRobotstxtServer(RobotstxtServer robotstxtServer) {
		this.robotstxtServer = robotstxtServer;
	}

	public RobotstxtServer getRobotstxtServer() {
		return robotstxtServer;
	}

	public void setPageParserManager(PageParserManager pageParserManager) {
		this.pageParserManager = pageParserManager;
	}

	public PageParserManager getPageParserManager() {
		return pageParserManager;
	}

	public void setPageVisitedCallback(IPageVisited pageVisitedCallback) {
		this.pageVisitedCallback = pageVisitedCallback;
	}

	public IPageVisited getPageVisitedCallback() {
		return pageVisitedCallback;
	}

	public void setCacheProvider(ICacheProvider cacheProvider) {
		this.cacheProvider = cacheProvider;
	}

	public ICacheProvider getCacheProvider() {
		return cacheProvider;
	}
}
