package edu.uci.ics.crawler4j.frontier;

public final class DocID {
	private int id;
	private boolean isNew;
	
	public DocID(int id, boolean isNew) {
		this.id = id;
		this.isNew = isNew;
	}

	public int getId() {
		return id;
	}
	
	public boolean isNew() {
		return isNew;
	}
}
