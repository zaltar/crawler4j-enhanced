package edu.uci.ics.crawler4j.cache;

import java.util.Calendar;

import edu.uci.ics.crawler4j.crawler.Page;

public interface ICacheProvider {
	
	public String getCachedETag(String url);
	public Calendar getLastModified(String url);
	public void getCachedPage(Page page);
	
}
