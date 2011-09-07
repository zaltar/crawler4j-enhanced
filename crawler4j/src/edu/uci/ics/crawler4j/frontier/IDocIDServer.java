package edu.uci.ics.crawler4j.frontier;

public interface IDocIDServer {

	/**
	 * Returns the docid of an already seen url.
	 * If url is not seen before, it will return -1
	 */
	public int getDocID(String url);

	public DocID getNewOrExistingDocID(String url);

	public int getDocCount();

}