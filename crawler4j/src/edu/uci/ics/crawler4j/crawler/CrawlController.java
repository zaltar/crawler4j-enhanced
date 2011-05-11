/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.uci.ics.crawler4j.crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.IO;

/**
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public final class CrawlController {
	private static final Logger logger = Logger.getLogger(CrawlController.class.getName());
	
	private List<WebCrawler> crawlers = new ArrayList<WebCrawler>();
	private Frontier frontier;
	private Environment env;
	private List<Object> crawlersLocalData = new ArrayList<Object>();
	private ICrawlComplete completeCallback = null;
	private AtomicBoolean running = new AtomicBoolean(false);
	
	public List<Object> getCrawlersLocalData() {
		return crawlersLocalData;
	}
	
	public void setCompleteCallback(ICrawlComplete value) {
		completeCallback = value;
	}

	public CrawlController(String storageFolder) {
		this(storageFolder, Configurations.getBooleanProperty("crawler.enable_resume", true));
	}
	
	public CrawlController(String storageFolder, boolean resumable) {
		File folder = new File(storageFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(resumable);
		envConfig.setLocking(resumable);

		File envHome = new File(storageFolder + "/frontier");
		if (!envHome.exists()) {
			envHome.mkdir();
		}
		if (!resumable) {
			IO.deleteFolderContents(envHome);
		}

		env = new Environment(envHome, envConfig);
		frontier = new Frontier(env, resumable);
		DocIDServer.init(env, resumable);

		PageFetcher.startConnectionMonitorThread();
	}

	public <T extends WebCrawler> void start(final Class<T> _c, int numberOfCrawlers) throws Exception {
		start(new Callable<WebCrawler>() {
			public WebCrawler call() {
				try {
					return _c.newInstance();
				} catch (Exception e) {
					return null;
				}
			}
		}, numberOfCrawlers);
	}
	
	public void start(Callable<WebCrawler> crawlerFactory, int numberOfCrawlers) throws Exception {
		if (!running.getAndSet(true)) {
			crawlersLocalData.clear();
			frontier.setThreads(numberOfCrawlers);
			try {
				for (int i = 1; i <= numberOfCrawlers; i++) {
					WebCrawler crawler = crawlerFactory.call();
					Thread thread = new Thread(crawler, "Crawler " + i);
					crawler.setThread(thread);
					crawler.setMyId(i);
					crawler.setMyController(this);
					thread.start();
					crawlers.add(crawler);
					logger.info("Crawler " + i + " started.");
				}
			} catch (Exception e) {
				logger.error("Exception created crawler thread.", e);
				frontier.finish();
				throw e;
			}
			
			final CrawlController that = this;
			new Thread(new Runnable() {
				public void run() {
					waitForFinish();
					if (completeCallback != null) {
						completeCallback.crawlComplete(that);
					}
				}
			}).start();
		} else {
			throw new Exception("Already started!");
		}
	}
	
	public void waitForFinish() {
		for (WebCrawler c : crawlers) {
			while (c.getThread().isAlive()) {
				try {
					c.getThread().join();
				} catch (InterruptedException ie) { }
			}
			crawlersLocalData.add(c.getMyLocalData());
			logger.info("Crawler " + c.getMyId() + " ended.");
		}
		PageFetcher.stopConnectionMonitorThread();
		logger.info("Crawler complete");
		frontier.close();
		running.set(false);
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

		WebURL webUrl = new WebURL();
		webUrl.setURL(canonicalUrl);
		docid = DocIDServer.getNewDocID(canonicalUrl);
		webUrl.setDocid(docid);
		webUrl.setDepth((short) 0);
		if (!RobotstxtServer.allows(webUrl)) {
			logger.info("Robots.txt does not allow this seed: " + pageUrl);
		} else {
			frontier.schedule(webUrl);
		}
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

	public void setMaximumCrawlDepth(int depth) throws NumberFormatException {
		if (depth < -1 || depth > Short.MAX_VALUE) {
			throw new NumberFormatException("Maximum crawl depth should be either a positive number or -1 for unlimited depth.");
		}
		WebCrawler.setMaximumCrawlDepth((short) depth);
	}

	public void setMaximumPagesToFetch(int max) {
		frontier.setMaximumPagesToFetch(max);
	}

	public void setProxy(String proxyHost, int proxyPort) {
		PageFetcher.setProxy(proxyHost, proxyPort);
	}

	public static void setProxy(String proxyHost, int proxyPort, String username, String password) {
		PageFetcher.setProxy(proxyHost, proxyPort, username, password);
	}

	public Frontier getFrontier() {
		return frontier;
	}
}
