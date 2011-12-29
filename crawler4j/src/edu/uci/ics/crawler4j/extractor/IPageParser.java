package edu.uci.ics.crawler4j.extractor;

import java.util.Map;
import edu.uci.ics.crawler4j.crawler.Page;

public interface IPageParser {
	void parse(Page page);
	Map<String, String> getLinks();
}