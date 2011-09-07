package edu.uci.ics.crawler4j.extractor;

import it.unimi.dsi.lang.MutableString;
import it.unimi.dsi.parser.Attribute;
import it.unimi.dsi.parser.Element;
import it.unimi.dsi.parser.callback.LinkExtractor;
import java.util.Map;

/**
 * This class is an extension of
 * it.unimi.dsi.parser.callback.LinkExtractor with support for extracting
 * image sources.
 */

public class HTMLLinkExtractor extends LinkExtractor {
	private boolean includeImagesSources = false;

	@Override
	public boolean startElement(final Element element,
			final Map<Attribute, MutableString> attrMap) {
		Object s;
		
		if (includeImagesSources && element == Element.IMG) {
			s = attrMap.get(Attribute.SRC);
			if (s != null) {
				urls.add(s.toString());
			}
			return true;
		}
		
		return super.startElement(element, attrMap);
	}

	public boolean isIncludeImagesSources() {
		return includeImagesSources;
	}

	public void setIncludeImagesSources(boolean includeImagesSources) {
		this.includeImagesSources = includeImagesSources;
	}
}
