package edu.uci.ics.crawler4j.frontier;

import com.sleepycat.je.*;

import edu.uci.ics.crawler4j.util.Util;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */


public final class DocIDServer {

	private static Database docIDsDB = null;

	private static Object mutex = "DocIDServer_Mutex";
	
	private static int lastDocID = 0;
	
	public static void init(Environment env) throws DatabaseException {
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setTransactional(false);
		dbConfig.setDeferredWrite(true);
		docIDsDB = env.openDatabase(null, "DocIDs", dbConfig);		
	}

	public static synchronized int getDocID(String url) {
		if (docIDsDB == null) {
			return -1;
		}
		OperationStatus result = null;
		DatabaseEntry value = new DatabaseEntry();
		try {
			DatabaseEntry key = new DatabaseEntry(url.getBytes());
			result = docIDsDB.get(null, key, value, null);

			if (result == OperationStatus.SUCCESS && value.getData().length > 0) {
				return Util.byteArray2Int(value.getData());
			} else {
				lastDocID++;				
				value = new DatabaseEntry(Util.int2ByteArray(lastDocID));
				docIDsDB.put(null, key, value);				
				return -lastDocID;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static int getDocCount() {
		try {
			return (int) docIDsDB.count();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static void putDocID(String url, int docid) {
		synchronized (mutex) {
			try {
				docIDsDB.put(null, new DatabaseEntry(url.getBytes()),
						new DatabaseEntry(Util.int2ByteArray(docid)));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void sync() {
		if (docIDsDB == null) {
			return;
		}
		try {
			docIDsDB.sync();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public static void close() {
		try {
			docIDsDB.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
}
