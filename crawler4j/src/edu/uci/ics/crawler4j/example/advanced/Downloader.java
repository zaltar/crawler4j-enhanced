package edu.uci.ics.crawler4j.example.advanced;

import edu.uci.ics.crawler4j.crawler.HTMLParser;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.PageFetchStatus;
import edu.uci.ics.crawler4j.crawler.PageFetcher;
import edu.uci.ics.crawler4j.url.WebURL;

public class Downloader {

	// This class is not currently used and is only a demonstration of how 
	// the crawler4j infrastructure can be used to download a single page
	// and extract its title and text
	
	private HTMLParser htmlParser = new HTMLParser();

	public Page download(String url) {
		WebURL curURL = new WebURL(url, 0);
		Page page = new Page(curURL);
		page.setWebURL(curURL);
		int statusCode = PageFetcher.fetch(page);
		if (statusCode == PageFetchStatus.OK) {
			try {
				htmlParser.parse(page.getHTML(), curURL.getURL());
				page.setText(htmlParser.getText());
				page.setTitle(htmlParser.getTitle());
				return page;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		Downloader myDownloader = new Downloader();
		Page page = myDownloader.download("http://ics.uci.edu");
		if (page != null) {
			System.out.println(page.getText());
		}
	}
}
