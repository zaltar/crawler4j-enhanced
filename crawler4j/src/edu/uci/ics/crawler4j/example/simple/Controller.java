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

package edu.uci.ics.crawler4j.example.simple;

import edu.uci.ics.crawler4j.crawler.CrawlController;

/**
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public class Controller {

		public static void main(String[] args) throws Exception {
			if (args.length < 2) {
				System.out.println("Please specify 'root folder' and 'number of crawlers'.");
				return;
			}
			String rootFolder = args[0];
			int numberOfCrawlers = Integer.parseInt(args[1]);
			
			CrawlController controller = new CrawlController(rootFolder);		
			controller.addSeed("http://www.cnn.com/");
			
			// Be polite:
			// Make sure that we don't send more than 5 requests per second (200 milliseconds between requests).
			controller.setPolitenessDelay(200);
			
			// Optional:
			// You can set the maximum crawl depth here.
			// The default value is -1 for unlimited depth
			controller.setMaximumCrawlDepth(3);
			
			// Optional:
			// You can set the maximum number of pages to crawl.
			// The default value is -1 for unlimited depth
			controller.setMaximumPagesToFetch(500);
			
			// Do you need to set a proxy?
			// If so, you can uncomment the following line
			// controller.setProxy("proxyserver.example.com", 8080);
			// OR
			// controller.setProxy("proxyserver.example.com", 8080, username, password);
			
			controller.start(MyCrawler.class, numberOfCrawlers);
		}

}

