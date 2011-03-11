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

import com.sleepycat.je.*;

import edu.uci.ics.crawler4j.util.Util;

/**
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
