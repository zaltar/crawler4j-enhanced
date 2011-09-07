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

package edu.uci.ics.crawler4j.crawler;

import java.util.List;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public class Page {

	private WebURL url;
	
	private String redirectedURL;

	private String html;

	// Data for textual content
	private String text;
	private String title;
	
	private String contentType;

	// binary data (e.g, image content)
	// It's null for html pages
	private byte[] binaryData;

	private List<WebURL> urls;

	public Page(WebURL url) {
		this.url = new WebURL(url);
	}

	public String getHTML() {
		return this.html;
	}
	
	public void setHTML(String html) {
		this.html = html;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<WebURL> getURLs() {
		return urls;
	}

	public void setURLs(List<WebURL> urls) {
		this.urls = urls;
	}

	public WebURL getWebURL() {
		return url;
	}

	public void setWebURL(WebURL url) {
		this.url = url;
	}

	// Image or other non-textual pages
	public boolean isBinary() {
		return binaryData != null;
	}

	public byte[] getBinaryData() {
		return binaryData;
	}
	
	public void setBinaryData(byte[] data) {
		binaryData = data;
	}

	public void setRedirectedURL(String redirectedURL) {
		this.redirectedURL = redirectedURL;
	}

	public String getRedirectedURL() {
		return redirectedURL;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

}
