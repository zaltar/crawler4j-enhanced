package edu.uci.ics.crawler4j.crawler;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import edu.uci.ics.crawler4j.crawler.configuration.ICrawlerSettings;
import edu.uci.ics.crawler4j.crawler.exceptions.CrawlerStillRunningException;
import edu.uci.ics.crawler4j.frontier.DocID;
import edu.uci.ics.crawler4j.frontier.ICrawlState;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;

public final class CrawlerController {
	private static final Logger logger = Logger.getLogger(CrawlerController.class);
	private ArrayList<CrawlRunner> crawlers;
	private ICrawlerSettings config;
	private volatile boolean isFinished = true;
	private ICrawlState frontier;
	private ICrawlComplete finishedCallback = null;
	
	public CrawlerController(ICrawlerSettings config) {
		this.config = config;
		this.frontier = config.getCrawlState();
		crawlers = new ArrayList<CrawlRunner>(config.getNumberOfCrawlerThreads());
		for (int i = 0; i < config.getNumberOfCrawlerThreads(); i++) {
			crawlers.add(new CrawlRunner(config));
		}
	}
	
	public synchronized void run() throws CrawlerStillRunningException {
		run(null);
	}
	
	public synchronized void run(ICrawlComplete finishedCallback) throws CrawlerStillRunningException {
		if (isFinished) {
			this.finishedCallback = finishedCallback;
			URLManager urlManager = new URLManager(crawlers.size());
			isFinished = false;
			
			logger.debug("Starting " + crawlers.size() + " crawlers.");
			
			//Thread startup
			for (CrawlRunner c : crawlers) {
				c.start(urlManager);
			}
		} else 
			throw new CrawlerStillRunningException("Crawler still running!");
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	private void finished() {
		logger.debug("Received finished from a crawler. Waiting for them all to stop.");
		for (CrawlRunner c : crawlers) {
			c.stop();
		}
		
		if (finishedCallback != null)
			finishedCallback.crawlComplete(this);
			
		isFinished = true;
		logger.debug("Crawler threads all finished.");
	}
	
	public void addSeed(String pageUrl) {
		String canonicalUrl = URLCanonicalizer.getCanonicalURL(pageUrl);
		if (canonicalUrl == null) {
			logger.error("Invalid seed URL: " + pageUrl);
			return;
		}
		ICrawlState crawlState = config.getCrawlState();
		DocID docid = crawlState.getDocIDServer().getNewOrExistingDocID(canonicalUrl);
		if (!docid.isNew()) {
			// This URL is already seen.
			return;
		}

		WebURL webUrl = new WebURL();
		webUrl.setURL(canonicalUrl);
		webUrl.setDocid(docid.getId());
		webUrl.setDepth((short) 0);
		if (!config.getRobotstxtServer().allows(webUrl)) {
			logger.info("Robots.txt does not allow this seed: " + pageUrl);
		} else {
			crawlState.schedule(webUrl);
		}
	}
	
	/* 
	 * This inner class gives an interface to the CrawlRunner
	 * threads to safely add/remove WebURLs in a synchronized
	 * manner with the frontier database.
	 */
	public class URLManager {
		private int waitingThreads = 0;
		private final int threads;
		private volatile boolean noMoreUrls = false;
		private final Object urlWaitingLock = new Object();
		
		public URLManager(int threads) {
			this.threads = threads;
		}
		
		public void getNextURLs(int max, List<WebURL> result) {
			while (!noMoreUrls) {
				frontier.getNextURLs(max, result);
				if (result.size() > 0)
					return;
				
				synchronized (urlWaitingLock) {
					waitingThreads++;
					if (waitingThreads == threads) {
						noMoreUrls = true;
						
						//Let everyone know we're done
						urlWaitingLock.notifyAll();
						
						//Spawn a new thread to wait for everyone to finish
						//and trigger the finished callback
						new Thread(new Runnable() {
							@Override
							public void run() {
								finished();
							}
						}).start();
						return;
					}
					try {
						urlWaitingLock.wait();
					} catch (InterruptedException e) {
						//loop around and try again if we're not finished
					}
					waitingThreads--;
				}
			}
		}
		
		public void setProcessed(WebURL webURL) {
			frontier.setProcessed(webURL);
		}
		
		public void scheduleAll(List<WebURL> urls) {
			frontier.scheduleAll(urls);
			synchronized (urlWaitingLock) {
				urlWaitingLock.notifyAll();
			}
		}
		
		public void schedule(WebURL url) {
			frontier.schedule(url);
			synchronized (urlWaitingLock) {
				urlWaitingLock.notifyAll();
			}
		}
	}
}
