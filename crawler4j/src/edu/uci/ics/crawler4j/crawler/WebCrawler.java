package edu.uci.ics.crawler4j.crawler;

import java.util.*;

import org.apache.log4j.Logger;

import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public class WebCrawler implements Runnable {

	private static final Logger logger = Logger.getLogger(WebCrawler.class
			.getName());

	private Thread myThread;

	private final static int PROCESS_OK = -12;

	private HTMLParser htmlParser;
	
	int myid;
	
	private CrawlController myController;

	public CrawlController getMyController() {
		return myController;
	}

	public void setMyController(CrawlController myController) {
		this.myController = myController;
	}

	public WebCrawler() {
		htmlParser = new HTMLParser();		
	}
	
	public WebCrawler(int myid) {
		this.myid = myid;
	}
	
	public void setMyId(int myid) {
		this.myid = myid;
	}
	
	public int getMyId() {
		return myid;
	}
	
	public void onStart() {
		
	}
	
	public void onBeforeExit() {
		
	}
	
	public Object getMyLocalData() {
		return null;
	}

	public void run() {
		onStart();
		while (true) {
			ArrayList<WebURL> assignedURLs = new ArrayList<WebURL>(50);
			Frontier.getNextURLs(50, assignedURLs);
			if (assignedURLs.size() == 0) {
				if (Frontier.isFinished()) {
					return;
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				for (WebURL curURL : assignedURLs) {
					if (curURL != null) {
						preProcessPage(curURL);
					}
				}
			}
		}
	}

	public boolean shouldVisit(WebURL url) {		
		return true;
	}

	public void visit(Page page) {
		
	}

	private int preProcessPage(WebURL curURL) {
		if (curURL == null) {
			return -1;
		}
		Page page = new Page(curURL);
		int statusCode = PageFetcher.fetch(page);
		// The page might have been redirected. So we have to refresh curURL
		curURL = page.getWebURL();
		int docid = curURL.getDocid();
		if (statusCode != PageFetchStatus.OK) {
			if (statusCode == PageFetchStatus.PageTooBig) {
				logger.error("Page was bigger than max allowed size: " + curURL.getURL());
			} 
			return statusCode;
		}

		try {
			htmlParser.parse(page.getHTML(), curURL.getURL());
			page.setText(htmlParser.getText());
			page.setTitle(htmlParser.getTitle());

			Iterator<String> it = htmlParser.getLinks().iterator();
			ArrayList<WebURL> toSchedule = new ArrayList<WebURL>();
			ArrayList<WebURL> toList = new ArrayList<WebURL>();
			while (it.hasNext()) {
				String url = it.next();				
				if (url != null) {
					int newdocid = DocIDServer.getDocID(url);
					if (newdocid > 0) {
						if (newdocid != docid) {
							toList.add(new WebURL(url, newdocid));
						}
					} else {
						toList.add(new WebURL(url, -newdocid));
						WebURL cur = new WebURL(url, -newdocid);
						if (shouldVisit(cur)) {
							toSchedule.add(cur);
						}
					}
				}
			}
			Frontier.scheduleAll(toSchedule);
			page.setURLs(toList);
			visit(page);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage() + ", while processing: "
					+ curURL.getURL());
		}
		return PROCESS_OK;
	}

	public Thread getThread() {
		return myThread;
	}

	public void setThread(Thread myThread) {
		this.myThread = myThread;
	}
}
