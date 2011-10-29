package edu.uci.ics.crawler4j.cache;

import java.util.Calendar;
import java.util.Set;
import edu.uci.ics.crawler4j.crawler.Page;

public interface ICacheProvider {
	
	public String getCachedETag(String url);
	public Calendar getLastModified(String url);
	
	/*
	 * This is called when a server responds that our cached page is still valid.
	 * This function should set any of the page properties that would have been set
	 * if the page were actually downloaded.  This includes the content type, 
	 * etag header, last modified header, and the binary or html data depending.
	 */
	public void setupCachedPage(Page page);
	
	/*
	 * This is called to potentially load cached links from the cache provider. If
	 * links are not cached, returning null will cause the default parser to reparse
	 * the page content.
	 */
	public Set<String> getCachedLinks(Page page);
}
