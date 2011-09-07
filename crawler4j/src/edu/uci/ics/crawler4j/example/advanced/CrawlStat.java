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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CrawlStat {
	private AtomicInteger totalProcessedPages = new AtomicInteger();
	private AtomicLong totalLinks = new AtomicLong();
	private AtomicLong totalTextSize = new AtomicLong();

	
	public int getTotalProcessedPages() {
		return totalProcessedPages.get();
	}
	
	public void incProcessedPages() {
		this.totalProcessedPages.incrementAndGet();
	}

	public long getTotalLinks() {
		return totalLinks.get();
	}

	public long getTotalTextSize() {
		return totalTextSize.get();
	}
	
	public void incTotalLinks(int count) {
		this.totalLinks.addAndGet(count);
	}
	
	public void incTotalTextSize(int count) {
		this.totalTextSize.addAndGet(count);
	}

}
