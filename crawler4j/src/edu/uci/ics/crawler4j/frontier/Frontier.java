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

package edu.uci.ics.crawler4j.frontier;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import edu.uci.ics.crawler4j.crawler.configuration.ICrawlerSettings;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.IO;

/**
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public final class Frontier implements ICrawlState {
	private final static Logger logger = Logger.getLogger(Frontier.class);

	private WorkQueues workQueues;
	private InProcessPagesDB inprocessPages;

	private int maxPagesToFetch;

	private AtomicInteger scheduledPages = new AtomicInteger();
	
	private DocIDServer docIdServer;
	private Environment env;

	public Frontier(ICrawlerSettings config) {
		boolean resumable = config.getEnableResume();
		String storageFolder = config.getStorageFolder();
		maxPagesToFetch = config.getMaxPagesToFetch();
		
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
		
		try {
			workQueues = new WorkQueues(env, "PendingURLsDB", resumable);
			if (resumable) {
				inprocessPages = new InProcessPagesDB(env);
				long docCount = inprocessPages.getLength();
				if (docCount > 0) {
					logger.info("Rescheduling " + docCount + " URLs from previous crawl.");
					while (true) {
						List<WebURL> urls = inprocessPages.get(100);
						if (urls.size() == 0) {
							break;
						}
						scheduleAll(urls);
					}
				}
			} else {
				inprocessPages = null;
				scheduledPages.set(0);
			}			
		} catch (DatabaseException e) {
			logger.error("Error while initializing the Frontier: " + e.getMessage());
			workQueues = null;
			throw e;
		}
		
		docIdServer = new DocIDServer(env, resumable);
	}

	@Override
	public void scheduleAll(List<WebURL> urls) {
		Iterator<WebURL> it = urls.iterator();
		while (it.hasNext()) {
			WebURL url = it.next();
			if (maxPagesToFetch < 0 || scheduledPages.get() < maxPagesToFetch) {					
				try {
					workQueues.put(url);
					scheduledPages.incrementAndGet();
				} catch (DatabaseException e) {
					logger.error("Error while puting the url in the work queue.");
				}
			}
		}
	}

	@Override
	public void schedule(WebURL url) {
		try {
			if (maxPagesToFetch < 0 || scheduledPages.get() < maxPagesToFetch) {
				workQueues.put(url);
				scheduledPages.incrementAndGet();
			}
		} catch (DatabaseException e) {
			logger.error("Error while puting the url in the work queue.");
		}
	}

	@Override
	public void getNextURLs(int max, List<WebURL> result) {
		try {
			List<WebURL> curResults = workQueues.get(max);
			if (inprocessPages != null) {
				for (WebURL curPage : curResults) {
					inprocessPages.put(curPage);
				}
			}
			result.addAll(curResults);					
		} catch (DatabaseException e) {
			logger.error("Error while getting next urls: " + e.getMessage());
			e.printStackTrace();
		}
		if (result.size() > 0) {
			return;
		}
	}
	
	@Override
	public void setProcessed(WebURL webURL) {
		if (inprocessPages != null) {
			if (!inprocessPages.removeURL(webURL)) {
				logger.warn("Could not remove: " + webURL.getURL() + " from list of processed pages.");
			}
		}
	}

	@Override
	public IDocIDServer getDocIDServer() {
		return docIdServer;
	}
	
	public void sync() {
		workQueues.sync();
		docIdServer.sync();
	}

	@Override
	public void close() {
		sync();
		if (workQueues != null) {
			workQueues.close();
			workQueues = null;
		}
		if (inprocessPages != null) {
			inprocessPages.close();
			inprocessPages = null;
		}
		if (docIdServer != null) {
			docIdServer.close();
			docIdServer = null;
		}
		if (env != null) {
			env.close();
			env = null;
		}
	}
}
