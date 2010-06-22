package edu.uci.ics.crawler4j.crawler;

import java.io.File;
import java.lang.Thread.State;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.IO;

/**
 * Copyright (C) 2010
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public final class CrawlController {

	private static final Logger logger = Logger.getLogger(CrawlController.class
			.getName());

	private Environment env;

	private ArrayList<Object> crawlersLocalData = new ArrayList<Object>();

	public ArrayList<Object> getCrawlersLocalData() {
		return crawlersLocalData;
	}

	ArrayList<Thread> threads;

	public CrawlController(String storageFolder) throws Exception {
		File folder = new File(storageFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(false);
		envConfig.setLocking(false);

		File envHome = new File(storageFolder + "/frontier");
		if (!envHome.exists()) {
			envHome.mkdir();
		}
		IO.deleteFolderContents(envHome);

		env = new Environment(envHome, envConfig);
		Frontier.init(env);
		DocIDServer.init(env);
		
		PageFetcher.startConnectionMonitorThread();
	}

	public <T extends WebCrawler> void start(Class<T> _c, int numberOfCrawlers) {
		try {
			crawlersLocalData.clear();
			threads = new ArrayList<Thread>();
			ArrayList<T> crawlers = new ArrayList<T>();
			int numberofCrawlers = numberOfCrawlers;
			for (int i = 1; i <= numberofCrawlers; i++) {
				T crawler = _c.newInstance();
				Thread thread = new Thread(crawler, "Crawler " + i);
				crawler.setThread(thread);
				crawler.setMyId(i);
				crawler.setMyController(this);
				thread.start();
				crawlers.add(crawler);
				threads.add(thread);
				logger.info("Crawler " + i + " started.");
			}
			while (true) {
				sleep(10);
				boolean someoneIsWorking = false;
				for (int i = 0; i < threads.size(); i++) {
					Thread thread = threads.get(i);
					if (!thread.isAlive()) {
						logger.info("Thread " + i
								+ " was dead, I'll recreate it.");
						T crawler = _c.newInstance();
						thread = new Thread(crawler, "Crawler " + (i + 1));
						threads.remove(i);
						threads.add(i, thread);
						crawler.setThread(thread);
						crawler.setMyId(i + 1);
						crawler.setMyController(this);
						thread.start();
						crawlers.remove(i);
						crawlers.add(i, crawler);
					} else if (thread.getState() == State.RUNNABLE) {
						someoneIsWorking = true;
					}
				}
				if (!someoneIsWorking) {
					// Make sure again that none of the threads are alive.					
					sleep(40);
					
					if (!isAnyThreadWorking()) {
						long queueLength = Frontier.getQueueLength();
						if (queueLength > 0) {							
							continue;
						}
						sleep(60);
						queueLength = Frontier.getQueueLength();
						if (queueLength > 0) {							
							continue;
						}
						Frontier.close();
						logger.info("All of the crawlers are stopped. Finishing the process.");
						for (T crawler : crawlers) {
							crawler.onBeforeExit();
							crawlersLocalData.add(crawler.getMyLocalData());
						}
						
						// At this step, frontier notifies the threads that were waiting for new URLs and they should stop
						// We will wait a few seconds for them and then return.
						Frontier.finish();
						sleep(10);
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (Exception e) {
		}
	}
	
	private boolean isAnyThreadWorking() {
		boolean someoneIsWorking = false;
		for (int i = 0; i < threads.size(); i++) {
			Thread thread = threads.get(i);
			if (thread.isAlive()
					&& thread.getState() == State.RUNNABLE) {
				someoneIsWorking = true;
			}
		}
		return someoneIsWorking;
	}

	public void addSeed(String pageUrl) {
		String canonicalUrl = URLCanonicalizer.getCanonicalURL(pageUrl);
		if (canonicalUrl == null) {
			logger.error("Invalid seed URL: " + pageUrl);
			return;
		}
		int docid = DocIDServer.getDocID(canonicalUrl);
		if (docid > 0) {
			// This URL is already seen.
			return;
		}
		WebURL URL = new WebURL(canonicalUrl, -docid);
		Frontier.schedule(URL);
	}
	
	public void setPolitenessDelay(int milliseconds) {
		if (milliseconds < 0) {
			return;
		}
		if (milliseconds > 10000) {
			milliseconds = 10000;
		}
		PageFetcher.setPolitenessDelay(milliseconds);
	}
	
	public void setProxy(String proxyHost, int proxyPort) {
		PageFetcher.setProxy(proxyHost, proxyPort);
	}
	
	public static void setProxy(String proxyHost, int proxyPort,
			String username, String password) {
		PageFetcher.setProxy(proxyHost, proxyPort, username, password);
	}
}
