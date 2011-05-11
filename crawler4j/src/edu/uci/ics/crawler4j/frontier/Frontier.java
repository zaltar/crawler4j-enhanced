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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;

import edu.uci.ics.crawler4j.crawler.Configurations;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public final class Frontier {

	private final Logger logger = Logger.getLogger(Frontier.class.getName());

	private WorkQueues workQueues;
	private InProcessPagesDB inprocessPages;

	private Object waitingList = Frontier.class.toString() + "_WaitingList";

	private volatile boolean isFinished = false;

	private int maxPagesToFetch = Configurations.getIntProperty("crawler.max_pages_to_fetch", -1);

	private AtomicInteger scheduledPages = new AtomicInteger();
	
	private int threads;
	
	private int waitingThreads;

	public Frontier(Environment env, boolean resumable) {
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
	}

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
			synchronized (waitingList) {
				waitingList.notifyAll();
			}
	}

	public void schedule(WebURL url) {
		try {
			if (maxPagesToFetch < 0 || scheduledPages.get() < maxPagesToFetch) {
				workQueues.put(url);
				scheduledPages.incrementAndGet();
			}
		} catch (DatabaseException e) {
			logger.error("Error while puting the url in the work queue.");
		}
		synchronized (waitingList) {
			waitingList.notify();
		}
	}

	public void getNextURLs(int max, List<WebURL> result) {
		while (!isFinished) {
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
			
			synchronized (waitingList) {
				waitingThreads++;
				if (waitingThreads == threads) {
					//We are all done!
					this.finish();
					return;
				}
				try {
					waitingList.wait();
				} catch (InterruptedException e) {
					//loop around and try again if we're not finished
				}
				waitingThreads--;
			}
		}
	}
	
	public void setProcessed(WebURL webURL) {
		if (inprocessPages != null) {
			if (!inprocessPages.removeURL(webURL)) {
				logger.warn("Could not remove: " + webURL.getURL() + " from list of processed pages.");
			}
		}
	}

	public long getQueueLength() {
		return workQueues.getLength();
	}

	public long getNumberOfAssignedPages() {
		return inprocessPages.getLength();
	}
	
	public void sync() {
		workQueues.sync();
		DocIDServer.sync();
	}

	public boolean isFinished() {
		return isFinished;
	}
	
	public void setMaximumPagesToFetch(int max) {
		maxPagesToFetch = max;
	}

	public void close() {
		sync();
		workQueues.close();
		DocIDServer.close();
	}

	public void finish() {
		isFinished = true;
		synchronized (waitingList) {
			waitingList.notifyAll();
		}
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public int getThreads() {
		return threads;
	}
}
