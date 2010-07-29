package edu.uci.ics.crawler4j.example.imagecrawler;

import edu.uci.ics.crawler4j.crawler.CrawlController;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

/*
* IMPORTANT: Make sure that you update crawler4j.properties file and 
*            set crawler.include_images to true           
*/

public class Controller {

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("Needed parameters: ");
			System.out
					.println("        rootFolder (it will contain intermediate crawl data)");
			System.out
					.println("        numberOfCralwers (number of concurrent threads)");
			System.out
					.println("        storageFolder (a folder for storing downloaded images)");
			return;
		}
		String rootFolder = args[0];
		int numberOfCrawlers = Integer.parseInt(args[1]);
		String storageFolder = args[2];

		String[] crawlDomains = new String[] { "http://uci.edu/" };

		CrawlController controller = new CrawlController(rootFolder);
		for (String domain : crawlDomains) {
			controller.addSeed(domain);
		}

		// Be polite:
		// Make sure that we don't send more than 5 requests per second (200
		// milliseconds between requests).
		controller.setPolitenessDelay(200);

		// Do you need to set a proxy?
		// If so, you can uncomment the following line
		// controller.setProxy("proxyserver.example.com", 8080);
		// OR
		// controller.setProxy("proxyserver.example.com", 8080, username,
		// password);

		MyImageCrawler.configure(crawlDomains, storageFolder);

		controller.start(MyImageCrawler.class, numberOfCrawlers);
	}

}
