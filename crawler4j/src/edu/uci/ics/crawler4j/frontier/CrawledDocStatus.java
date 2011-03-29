package edu.uci.ics.crawler4j.frontier;

public enum CrawledDocStatus {
	NOT_PROCESSED(0), SUCCESS(1), FAILED(2);
	
	private byte id;
	
	private CrawledDocStatus(int id) {
		this.id = (byte) id;
	}
	
	public byte getId() {
		return id;
	}
	
	public static CrawledDocStatus get(byte b) {
		for (CrawledDocStatus status : values()) {
			if (status.id == b) {
				return status;
			}
		}
		return null;
	}
}
