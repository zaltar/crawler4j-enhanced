package edu.uci.ics.crawler4j.crawler;

import java.util.HashMap;
import edu.uci.ics.crawler4j.cache.ICacheProvider;
import edu.uci.ics.crawler4j.crawler.configuration.CrawlerSettings;
import edu.uci.ics.crawler4j.crawler.configuration.SettingsBuilder;
import edu.uci.ics.crawler4j.crawler.exceptions.CrawlerBuildError;
import edu.uci.ics.crawler4j.crawler.fetcher.IPageFetcherCreator;
import edu.uci.ics.crawler4j.crawler.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.extractor.IPageParser;
import edu.uci.ics.crawler4j.extractor.PageParserManager;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.frontier.ICrawlStateCreator;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlBuilder {
	private SettingsBuilder settingsBuilder = new SettingsBuilder();
	private IPageFetcherCreator fetcherCreator;
	private ICrawlStateCreator crawlStateCreator;
	private IPageVisitValidator visitValidator;
	private IPageVisited pageVisited;
	private ICacheProvider cacheProvider;
	private HashMap<String, IPageParser> pageParsers = new HashMap<String, IPageParser>();
	
	public CrawlBuilder() {
	}
	
	public CrawlBuilder setSettings(SettingsBuilder settingsBuilder) {
		this.settingsBuilder = settingsBuilder;
		return this;
	}
	
	public CrawlBuilder addPageParser(String mimeType, IPageParser parser) {
		pageParsers.put(mimeType, parser);
		return this;
	}
	
	public CrawlBuilder setPageVisitValidator(IPageVisitValidator visitValidator) {
		this.visitValidator = visitValidator;
		return this;
	}
	
	public CrawlBuilder setPageFetcherCreator(IPageFetcherCreator pageFetcherCreator) {
		this.fetcherCreator = pageFetcherCreator;
		return this;
	}
	
	public CrawlBuilder setCrawlStateCreator(ICrawlStateCreator crawlStateCreator) {
		this.crawlStateCreator = crawlStateCreator;
		return this;
	}
	
	public CrawlBuilder setPageVisitedCallback(IPageVisited pageVisited) {
		this.pageVisited = pageVisited;
		return this;
	};

	public CrawlBuilder setCacheProvider(ICacheProvider cache) {
		this.cacheProvider = cache;
		return this;
	}
	
	public CrawlerController build() throws CrawlerBuildError {
		CrawlerSettings settings = settingsBuilder.build();
		
		settings.setPageVisitValidator(visitValidator);
		settings.setPageVisitedCallback(pageVisited);
		settings.setCacheProvider(cacheProvider);
		
		if (crawlStateCreator != null)
			settings.setCrawlState(crawlStateCreator.getCrawlState(settings));
		else
			settings.setCrawlState(new Frontier(settings));
		
		if (fetcherCreator != null)
			settings.setPageFetcher(fetcherCreator.getPageFetcher(settings));
		else
			settings.setPageFetcher(new PageFetcher(settings));
		
		settings.setRobotstxtServer(new RobotstxtServer(settings));
		
		settings.setPageParserManager(new PageParserManager(settings, pageParsers));
		
		return new CrawlerController(settings);
	}
}
