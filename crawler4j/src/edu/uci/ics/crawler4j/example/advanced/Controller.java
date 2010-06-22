package edu.uci.ics.crawler4j.example.advanced;

import java.util.ArrayList;

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
			controller.addSeed("http://www.ics.uci.edu/");
			controller.start(MyCrawler.class, numberOfCrawlers);	
			
			ArrayList<Object> crawlersLocalData = controller.getCrawlersLocalData();
			long totalLinks = 0;
			long totalTextSize = 0;
			int totalProcessedPages = 0;
			for (Object localData : crawlersLocalData) {
				CrawlStat stat = (CrawlStat) localData;
				totalLinks += stat.getTotalLinks();
				totalTextSize += stat.getTotalTextSize();
				totalProcessedPages += stat.getTotalProcessedPages();
			}
			System.out.println("Aggregated Statistics:");
			System.out.println("   Processed Pages: " + totalProcessedPages);
			System.out.println("   Total Links found: " + totalLinks);
			System.out.println("   Total Text Size: " + totalTextSize);
		}

}

