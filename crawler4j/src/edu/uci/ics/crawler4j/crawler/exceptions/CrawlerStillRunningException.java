package edu.uci.ics.crawler4j.crawler.exceptions;

public class CrawlerStillRunningException extends Exception {
	private static final long serialVersionUID = -2298285331724424695L;

	public CrawlerStillRunningException(String message) {
		super(message);
	}
}
