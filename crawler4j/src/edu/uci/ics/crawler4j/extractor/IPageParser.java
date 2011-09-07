package edu.uci.ics.crawler4j.extractor;

import java.util.Set;

import edu.uci.ics.crawler4j.crawler.Page;

public interface IPageParser {

	public void parse(Page page);

	public Set<String> getLinks();

}