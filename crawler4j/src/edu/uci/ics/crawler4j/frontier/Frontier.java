package edu.uci.ics.crawler4j.frontier;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;

import edu.uci.ics.crawler4j.url.WebURL;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public final class Frontier {
	
	private static final Logger logger = Logger.getLogger(Frontier.class
			.getName());
	
	private static WorkQueues workQueues;

	private static Object mutex = Frontier.class.toString() + "_Mutex";

	private static Object waitingList = Frontier.class.toString() + "_WaitingList";
	
	private static boolean isFinished = false;

	public static void init(Environment env) {
		try {
			workQueues = new WorkQueues(env);
		} catch (DatabaseException e) {
			logger.error("Error while initializing the Frontier: "
					+ e.getMessage());
			workQueues = null;
		}
	}

	public static void scheduleAll(ArrayList<WebURL> urls) {
		synchronized (mutex) {
			Iterator<WebURL> it = urls.iterator();
			while (it.hasNext()) {
				WebURL url = it.next();				
				try {
					workQueues.put(url);
				} catch (DatabaseException e) {
					logger.error("Error while puting the url in the work queue.");
				}
			}
			synchronized (waitingList) {
				waitingList.notifyAll();
			}
		}
	}
	
	public static void schedule(WebURL url) {
		synchronized (mutex) {							
			try {
				workQueues.put(url);
			} catch (DatabaseException e) {
				logger.error("Error while puting the url in the work queue.");
			}
		}
	}

	public static void getNextURLs(int max, ArrayList<WebURL> result) {
		while (true) {
			synchronized (mutex) {
				try {						
					ArrayList<WebURL> curResults = workQueues
							.get(max);
					workQueues.delete(curResults.size());
					result.addAll(curResults);
				} catch (DatabaseException e) {
					logger.error("Error while getting next urls: "
									+ e.getMessage());
					e.printStackTrace();
				}
				if (result.size() > 0) {
					return;
				}
			}
			try {
				synchronized (waitingList) {
					waitingList.wait();
				}
			} catch (InterruptedException e) {
			}
			if (isFinished) {
				return;
			}
		}
	}
	
	public static long getQueueLength() {
		return workQueues.getQueueLength();
	}
	
	public static void sync() {
		workQueues.sync();
		DocIDServer.sync();
	}
	
	public static boolean isFinished() {
		return isFinished;
	}

	public static void close() {		
		sync();		
		workQueues.close();
		
	}
	
	public static void finish() {
		isFinished = true;
		synchronized (waitingList) {
			waitingList.notifyAll();
		}
	}
}
