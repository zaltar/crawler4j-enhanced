package edu.uci.ics.crawler4j.extractor;

import java.util.HashMap;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.configuration.ICrawlerSettings;

public class PageParserManager {
	private HashMap<String, IPageParser> parsers = new HashMap<String, IPageParser>();
	private HTMLParser defaultTextParser;
	
	public PageParserManager(ICrawlerSettings config,  HashMap<String, IPageParser> parsers) {
		this.parsers = new HashMap<String, IPageParser>(parsers);
		defaultTextParser = new HTMLParser(config);
	}
	
	public IPageParser getParser(Page page) {
		IPageParser parser = null;
		
		if (page.getContentType() != null && page.getContentType().length() > 0)
			parser = parsers.get(page.getContentType());
			
		if (parser == null && !page.isBinary())
			parser = defaultTextParser;
		
		return parser;
	}
}
