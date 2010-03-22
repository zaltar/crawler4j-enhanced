package edu.uci.ics.crawler4j.example;

import edu.uci.ics.crawler4j.crawler.CrawlController;

public class Controller {

		public static void main(String[] args) throws Exception {
			if (args.length < 2) {
				System.out.println("Please specify 'root folder' and 'number of crawlers'.");
				return;
			}
			String rootFolder = args[0];
			int numberOfCrawlers = Integer.parseInt(args[1]);
			
			CrawlController controller = new CrawlController(rootFolder);		
			controller.addSeed("http://en.wikipedia.org/");

			// Be polite:
			// Make sure that we don't send more than 5 requests per second (200 milliseconds between requests).
			controller.setPolitenessDelay(200);
			
			controller.start(MyCrawler.class, numberOfCrawlers);
		}

}

