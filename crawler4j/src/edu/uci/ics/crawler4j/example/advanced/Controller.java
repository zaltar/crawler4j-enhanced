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

import edu.uci.ics.crawler4j.crawler.CrawlBuilder;
import edu.uci.ics.crawler4j.crawler.CrawlerController;
import edu.uci.ics.crawler4j.crawler.ICrawlComplete;
import edu.uci.ics.crawler4j.crawler.configuration.SettingsBuilder;

public class Controller {
	
		/*
		 * NOTE: You should first look at the simple example
		 * for a description of the options and configs. 
		 */

		public static void main(String[] args) throws Exception {
			if (args.length < 2) {
				System.out.println("Please specify 'root folder' and 'number of crawlers'.");
				return;
			}
			String rootFolder = args[0];
			int numberOfCrawlers = Integer.parseInt(args[1]);
			
			final MyCrawler myCrawler = new MyCrawler();
			
			CrawlerController controller = new CrawlBuilder()
				.setSettings(new SettingsBuilder()
					.setStorageFolder(rootFolder)
					.setNumberOfCrawlerThreads(numberOfCrawlers))
				.setPageVisitedCallback(null)
				.setPageVisitValidator(null)
				.build();
			
			controller.addSeed("http://www.ics.uci.edu/");
			controller.run(new ICrawlComplete() {	
				public void crawlComplete(CrawlerController c) {
					CrawlStat stats = myCrawler.getMyStats();
					System.out.println("Aggregated Statistics:");
					System.out.println("   Processed Pages: " + stats.getTotalProcessedPages());
					System.out.println("   Total Links found: " + stats.getTotalLinks());
					System.out.println("   Total Text Size: " + stats.getTotalTextSize());
				}
			});
		}

}

