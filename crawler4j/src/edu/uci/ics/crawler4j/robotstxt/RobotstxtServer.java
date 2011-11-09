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

package edu.uci.ics.crawler4j.robotstxt;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.configuration.ICrawlerSettings;
import edu.uci.ics.crawler4j.crawler.fetcher.IPageFetcher;
import edu.uci.ics.crawler4j.crawler.fetcher.PageFetchStatus;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */


public class RobotstxtServer {
	
	private Map<String, HostDirectives> host2directives = new HashMap<String, HostDirectives>();
	
	private final String userAgentName;
	private final int maxMapSize;
	private final Object mutex = RobotstxtServer.class.toString() + "_MUTEX"; 
	
	private boolean active;
	private IPageFetcher pageFetcher;
	
	public RobotstxtServer(ICrawlerSettings config) {
		userAgentName = config.getUserAgent();
		maxMapSize = config.getRobotstxtMapSize();
		active = config.getObeyRobotstxt();
		pageFetcher = config.getPageFetcher();
	}
	
	public boolean allows(String check) {
		if (!active) {
			return true;
		}
		try {
			URL url = new URL(check);
			String host = url.getHost().toLowerCase();
			String path = url.getPath();
			
			HostDirectives directives = host2directives.get(host);
			if (directives == null) {
				directives = fetchDirectives(host);
			} 
			return directives.allows(path);			
		} catch (MalformedURLException e) {			
			e.printStackTrace();
		}
		return true;
	}
	
	private HostDirectives fetchDirectives(String host) {
		WebURL robotsTxt = new WebURL();
		robotsTxt.setURL("http://" + host + "/robots.txt");
		Page page = new Page(robotsTxt);
		int statusCode = pageFetcher.fetch(page);
		HostDirectives directives = null;
		if (statusCode == PageFetchStatus.OK && !page.isBinary()) {
			directives = RobotstxtParser.parse(page.getHTML(), userAgentName);			
		}
		if (directives == null) {
			// We still need to have this object to keep track of the time we fetched it
			directives = new HostDirectives();
		}
		synchronized (mutex) {
			if (host2directives.size() == maxMapSize) {
				String minHost = null;
				long minAccessTime = Long.MAX_VALUE;
				for (Entry<String, HostDirectives> entry : host2directives.entrySet()) {
					if (entry.getValue().getLastAccessTime() < minAccessTime) {
						minAccessTime = entry.getValue().getLastAccessTime();
						minHost = entry.getKey();
					}					
				}
				host2directives.remove(minHost);
			}
			host2directives.put(host, directives);
		}
		return directives;
	}
}
