package edu.uci.ics.crawler4j.crawler;

import edu.uci.ics.crawler4j.url.WebURL;

public interface IPageVisitValidator {
	public boolean canVisit(WebURL url);
}
