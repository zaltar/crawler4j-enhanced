package edu.uci.ics.crawler4j.frontier;

import java.util.ArrayList;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.OperationStatus;

import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.Util;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */


public final class WorkQueues {

	private Database pendingURLsDB = null;

	private WebURLTupleBinding webURLBinding;

	private static Object mutex = "WorkQueues_Mutex";

	public WorkQueues(Environment env) throws DatabaseException {
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setTransactional(false);
		dbConfig.setDeferredWrite(true);
		pendingURLsDB = env.openDatabase(null, "PendingURLs", dbConfig);
		webURLBinding = new WebURLTupleBinding();
	}

	public ArrayList<WebURL> get(int max) throws DatabaseException {
		synchronized (mutex) {
			int matches = 0;
			ArrayList<WebURL> results = new ArrayList<WebURL>(max);

			Cursor cursor = null;
			OperationStatus result = null;
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry value = new DatabaseEntry();
			try {
				cursor = pendingURLsDB.openCursor(null, null);
				result = cursor.getFirst(key, value, null);

				while (matches < max && result == OperationStatus.SUCCESS) {
					if (value.getData().length > 0) {
						WebURL curi = (WebURL) webURLBinding
								.entryToObject(value);
						results.add(curi);
						matches++;
					}
					result = cursor.getNext(key, value, null);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			return results;
		}
	}

	public void delete(int count) throws DatabaseException {
		synchronized (mutex) {
			int matches = 0;

			Cursor cursor = null;
			OperationStatus result = null;
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry value = new DatabaseEntry();
			try {
				cursor = pendingURLsDB.openCursor(null, null);
				result = cursor.getFirst(key, value, null);

				while (matches < count && result == OperationStatus.SUCCESS) {
					cursor.delete();
					matches++;
					result = cursor.getNext(key, value, null);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		}
	}

	public void put(WebURL curi) throws DatabaseException {
		byte[] keyData = Util.int2ByteArray(curi.getDocid());
		DatabaseEntry value = new DatabaseEntry();
		webURLBinding.objectToEntry(curi, value);
		pendingURLsDB.put(null, new DatabaseEntry(keyData), value);
	}

	public void sync() {
		if (pendingURLsDB == null) {
			return;
		}
		try {
			pendingURLsDB.sync();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			pendingURLsDB.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	private Cursor cursor;

	public void closeCursor() {
		try {
			cursor.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public WebURL getFirst() {
		OperationStatus result = null;
		DatabaseEntry data = new DatabaseEntry();
		DatabaseEntry key = new DatabaseEntry();
		try {
			cursor = pendingURLsDB.openCursor(null, null);
			result = cursor.getFirst(key, data, null);
			if (result == OperationStatus.SUCCESS) {
				if (data.getData().length > 0) {
					return (WebURL) webURLBinding.entryToObject(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getQueueLength() {
		try {
			return pendingURLsDB.count();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public WebURL getNext() {
		OperationStatus result = null;
		DatabaseEntry data = new DatabaseEntry();
		DatabaseEntry key = new DatabaseEntry();
		try {
			result = cursor.getNext(key, data, null);
			if (result == OperationStatus.SUCCESS) {
				if (data.getData().length > 0) {
					return (WebURL) webURLBinding.entryToObject(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
