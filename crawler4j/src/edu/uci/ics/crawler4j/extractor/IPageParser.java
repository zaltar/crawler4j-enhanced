package edu.uci.ics.crawler4j.extractor;

import java.util.Map;
import edu.uci.ics.crawler4j.crawler.Page;

public interface IPageParser {

	public void parse(Page page);

	public Map<String, String> getLinks();

}