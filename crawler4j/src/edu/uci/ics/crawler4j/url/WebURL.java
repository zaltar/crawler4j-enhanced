package edu.uci.ics.crawler4j.url;

import java.io.*;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */


@Entity
public final class WebURL implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	private String url;

	private int docid;
	
	private int parentDocid;
	
	public WebURL(String url, int docid) {
		this.url = url;
		this.docid = docid;
	}
	
	public WebURL(String url) {
		this(url, 0);
	}

	public int getDocid() {
		return docid;
	}

	public void setDocid(int docid) {
		this.docid = docid;
	}

	public String getURL() {
		return url;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		WebURL url2 = (WebURL) o;
		if (url == null) {
			return false;
		}
		return url.equals(url2.getURL());

	}

	public String toString() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}
	
	public int getParentDocid() {
		return parentDocid;
	}

	public void setParentDocid(int parentDocid) {
		this.parentDocid = parentDocid;
	}
}
