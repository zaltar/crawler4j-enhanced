package edu.uci.ics.crawler4j.example;

import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public class MyCrawler extends WebCrawler {

	Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
			+ "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	public MyCrawler() {
	}

	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (filters.matcher(href).matches()) {
			return false;
		}
		if (href.startsWith("http://en.wikipedia.org/")) {
			return true;
		}
		return false;
	}
	
	public void visit(Page page) {
		// You can get the docid, url, text or links as follows:
		// int docid = page.getWebURL().getDocid();
        // String url = page.getWebURL().getURL();         
        // String text = page.getText();
        // ArrayList<WebURL> links = page.getURLs();
	}	
}
