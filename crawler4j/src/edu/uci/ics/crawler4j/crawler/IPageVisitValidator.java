package edu.uci.ics.crawler4j.crawler;

public interface IPageVisitValidator {
	boolean canVisit(String url);
}
