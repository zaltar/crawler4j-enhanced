package edu.uci.ics.crawler4j.crawler.exceptions;

public class CrawlerBuildError extends Exception {
	private static final long serialVersionUID = 2170552573598877774L;

	public CrawlerBuildError(String msg) {
		super(msg);
	}
	
	public CrawlerBuildError(String msg, Throwable t) {
		super(msg, t);
	}
}
