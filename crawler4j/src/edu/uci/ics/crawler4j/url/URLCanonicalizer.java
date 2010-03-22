package edu.uci.ics.crawler4j.url;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */


public final class URLCanonicalizer {

	public static String getCanonicalURL(String url) {
		URL canonicalURL = getCanonicalURL(url, null);
		if (canonicalURL != null) {
			return canonicalURL.toExternalForm();
		}
		return null;
	}

	public static URL getCanonicalURL(String href, String context) {
		if (href.contains("#")) {
            href = href.substring(0, href.indexOf("#"));
        }
		href = href.replace(" ", "%20");
        try {
        	if (context == null) {
        		return new URL(href);
        	} else {
        		return new URL(new URL(context), href);
        	}
        } catch (MalformedURLException ex) {
            return null;
        }
	}
}
