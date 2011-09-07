package edu.uci.ics.crawler4j.crawler.exceptions;

public class CrawlerBuildError extends Exception {
	public CrawlerBuildError(String msg) {
		super(msg);
	}
	
	public CrawlerBuildError(String msg, Throwable t) {
		super(msg, t);
	}
}
