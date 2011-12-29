package edu.uci.ics.crawler4j.crawler.fetcher;

import edu.uci.ics.crawler4j.crawler.configuration.ICrawlerSettings;

public interface IPageFetcherCreator {
	IPageFetcher getPageFetcher(ICrawlerSettings config);
}
