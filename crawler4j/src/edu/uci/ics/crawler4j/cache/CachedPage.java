package edu.uci.ics.crawler4j.cache;

import java.util.Calendar;

public class CachedPage {
	private String eTag;
	private Calendar lastModified;
	private String contentType;
	private String html;
	private byte[] binaryData;
	
	public String getETag() {
		return eTag;
	}
	public void setETag(String eTag) {
		this.eTag = eTag;
	}
	public Calendar getLastModified() {
		return lastModified;
	}
	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getHTML() {
		return html;
	}
	public void setHTML(String html) {
		this.html = html;
	}
	public byte[] getBinaryData() {
		return binaryData;
	}
	public void setBinaryData(byte[] binaryData) {
		this.binaryData = binaryData;
	}
}
