package edu.uci.ics.crawler4j.frontier;

import java.util.List;

import edu.uci.ics.crawler4j.url.WebURL;

public interface ICrawlState {

	public void scheduleAll(List<WebURL> urls);

	public void schedule(WebURL url);

	public void getNextURLs(int max, List<WebURL> result);

	public void setProcessed(WebURL webURL);

	public IDocIDServer getDocIDServer();

	public void close();

}