package edu.uci.ics.crawler4j.crawler.fetcher;

import edu.uci.ics.crawler4j.crawler.Page;

public interface IPageFetcher {
	int fetch(Page page);
}