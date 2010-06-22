package edu.uci.ics.crawler4j.example.advanced;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

	Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
			+ "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	CrawlStat myCrawlStat;

	public MyCrawler() {
		myCrawlStat = new CrawlStat();
	}

	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (filters.matcher(href).matches()) {
			return false;
		}
		if (href.startsWith("http://www.ics.uci.edu/")) {
			return true;
		}
		return false;
	}
	
	public void visit(Page page) {	
		myCrawlStat.incProcessedPages();
		ArrayList<WebURL> links = page.getURLs();		
		myCrawlStat.incTotalLinks(links.size());
		try {
			myCrawlStat.incTotalTextSize(page.getText().getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
		}
		// We dump this crawler statistics after processing every 50 pages
		if (myCrawlStat.getTotalProcessedPages() % 50 == 0) {
			dumpMyData();
		}
	}	
	
	// This function is called by controller to get the local data of this crawler when job is finished
	public Object getMyLocalData() {
		return myCrawlStat;
	}
	
	// This function is called by controller before finishing the job.
	// You can put whatever stuff you need here.
	public void onBeforeExit() {
		dumpMyData();
	}
	
	public void dumpMyData() {
		int myId = getMyId();
		// This is just an example. Therefore I print on screen. You may probably want to write in a text file. 
		System.out.println("Crawler " + myId + "> Processed Pages: " + myCrawlStat.getTotalProcessedPages());
		System.out.println("Crawler " + myId + "> Total Links Found: " + myCrawlStat.getTotalLinks());
		System.out.println("Crawler " + myId + "> Total Text Size: " + myCrawlStat.getTotalTextSize());
	}
}
