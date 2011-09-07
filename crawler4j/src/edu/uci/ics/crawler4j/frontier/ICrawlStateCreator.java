package edu.uci.ics.crawler4j.frontier;

import edu.uci.ics.crawler4j.crawler.configuration.ICrawlerSettings;

public interface ICrawlStateCreator {
	public ICrawlState getCrawlState(ICrawlerSettings config);
}
