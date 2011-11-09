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

package edu.uci.ics.crawler4j.example.advanced;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Pattern;
import edu.uci.ics.crawler4j.crawler.IPageVisitValidator;
import edu.uci.ics.crawler4j.crawler.IPageVisited;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler implements IPageVisited, IPageVisitValidator {

	Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
			+ "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	CrawlStat myCrawlStat;

	public MyCrawler() {
		myCrawlStat = new CrawlStat();
	}

	public boolean canVisit(String url) {
		String href = url.toLowerCase();
		if (filters.matcher(href).matches()) {
			return false;
		}
		if (href.startsWith("http://www.ics.uci.edu/")) {
			return true;
		}
		return false;
	}
	
	public void visited(Page page) {	
		myCrawlStat.incProcessedPages();
		List<WebURL> links = page.getURLs();		
		myCrawlStat.incTotalLinks(links.size());
		try {
			myCrawlStat.incTotalTextSize(page.getText().getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
		}
	}	

	public CrawlStat getMyStats() {
		return myCrawlStat;
	}
}
