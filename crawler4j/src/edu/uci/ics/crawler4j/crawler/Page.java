package edu.uci.ics.crawler4j.crawler;

import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import edu.uci.ics.crawler4j.url.WebURL;

public class Page {
	
	private WebURL url;
	
	private String html;
	
	private String text;
	
	private String title;
	
	private ArrayList<WebURL> urls;

	private ByteBuffer bBuf;
	
	public boolean load(final InputStream in, final int totalsize) {
		if (totalsize > 0) {
			this.bBuf = ByteBuffer.allocate(totalsize + 1024);
		} else {
			this.bBuf = ByteBuffer.allocate(PageFetcher.MAX_DOWNLOAD_SIZE); 
		}
		final byte[] b = new byte[1024];
		int len;
		double finished = 0;
		try {
			while ((len = in.read(b)) != -1) {
				if (finished + b.length > this.bBuf.capacity()) {
					break;
				}
				this.bBuf.put(b, 0, len);
				finished += len;				
			}
		} catch (final BufferOverflowException boe) {
			System.out.println("Page size exceeds maximum allowed.");			
			return false;
		} catch (final Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		this.html = "";
		this.bBuf.flip();
		this.html += Charset.forName("UTF-8").decode(this.bBuf);
		this.bBuf.clear();
		if (!this.html.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public Page(WebURL url) {
		this.url = url;
	}

	public String getHTML() {
		return this.html;
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

	public ArrayList<WebURL> getURLs() {
		return urls;
	}

	public void setURLs(ArrayList<WebURL> urls) {
		this.urls = urls;
	}

	public WebURL getWebURL() {
		return url;
	}

	public void setWebURL(WebURL url) {
		this.url = url;
	}

}
