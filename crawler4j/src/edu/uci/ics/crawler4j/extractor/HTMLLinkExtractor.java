package edu.uci.ics.crawler4j.extractor;

import it.unimi.dsi.lang.MutableString;
import it.unimi.dsi.parser.Attribute;
import it.unimi.dsi.parser.Element;
import it.unimi.dsi.parser.callback.DefaultCallback;
import java.util.Map;
import it.unimi.dsi.parser.BulletParser;
import it.unimi.dsi.util.TextPattern;
import java.util.HashMap;

/*		 
 * DSI utilities
 *
 * Copyright (C) 2005-2010 Sebastiano Vigna 
 *
 *  This library is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by the Free
 *  Software Foundation; either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 *
 * This class is an extension of
 * it.unimi.dsi.parser.callback.LinkExtractor with support for extracting
 * image sources and link names
 */

public class HTMLLinkExtractor extends DefaultCallback {
	/** The pattern prefixing the URL in a <samp>META </samp> <samp>HTTP-EQUIV </samp> element of refresh type. */
	private static final TextPattern URLEQUAL_PATTERN = new TextPattern( "URL=", TextPattern.CASE_INSENSITIVE );

	/** The URLs resulting from the parsing process. */
	public final HashMap<String, String> urls = new HashMap<>();

	/** The URL contained in the first <samp>META </samp> <samp>HTTP-EQUIV </samp> element of refresh type (if any). */
	private String metaRefresh = null;

	/** The URL contained in the first <samp>META </samp> <samp>HTTP-EQUIV </samp> element of location type (if any). */
	private String metaLocation = null;

	/** The URL contained in the first <samp>BASE </samp> element (if any). */
	private String base = null;

	/** Are we including img urls? */
	private boolean includeImagesSources = false;

	/** Used to grab the test of a link */
	private boolean inLink = false;
	private String linkHref;
	private StringBuilder linkName = new StringBuilder();
	
	/**
	 * Configure the parser to parse elements and certain attributes.
	 * 
	 * <p>
	 * The required attributes are <samp>SRC </samp>, <samp>HREF </samp>, <samp>HTTP-EQUIV </samp>, and <samp>CONTENT
	 * </samp>.
	 *  
	 */

	public void configure( final BulletParser parser ) {
		parser.parseTags(true);
		parser.parseText(true);
		parser.parseAttributes( true );
		parser.parseAttribute( Attribute.SRC );
		parser.parseAttribute( Attribute.HREF );
		parser.parseAttribute( Attribute.HTTP_EQUIV );
		parser.parseAttribute( Attribute.CONTENT );
		parser.parseAttribute( Attribute.ALT );
	}

	public void startDocument() {
		urls.clear();
		base = metaLocation = metaRefresh = null;
	}
	
	@Override
	public boolean characters( final char[] textUnused, final int offsetUnused, 
			final int lengthUnused, final boolean flowBrokenUnused) {
		
		if (inLink) {
			linkName.append(textUnused, offsetUnused, lengthUnused);
		}
		
		return true;
	}

	@Override
	public boolean endElement(Element elementUnused) {
		if (inLink && elementUnused == Element.A) {
			urls.put(linkHref, linkName.toString().trim());
			linkName.setLength(0);
			inLink = false;
		}
		
		return true;
	}
	
	public boolean startElement( final Element element, final Map<Attribute,MutableString> attrMap ) {
		Object s;

		if (includeImagesSources && element == Element.IMG) {
			s = attrMap.get(Attribute.SRC);
			if (s != null) {
				Object alt = attrMap.get(Attribute.ALT);
				urls.put(s.toString(), alt == null ? null : alt.toString());
			}
		}
		else if ( element == Element.A ) {
			//If for some reason we hit another a tag before the last one closed, forget the text...
			if (inLink) {
				System.out.println("Closing tag early for " + linkHref);
				urls.put(linkHref, null);
				inLink = false;
			}
			s = attrMap.get( Attribute.HREF );
			if ( s != null ) {
				linkHref = s.toString();
				inLink = true;
				linkName.setLength(0);
			}
		}
		else if ( element == Element.AREA || element == Element.LINK ) {
			s = attrMap.get( Attribute.HREF );
			if ( s != null )
				urls.put(s.toString(), null);
		}
		// IFRAME or FRAME + SRC
		else if ( element == Element.IFRAME || element == Element.FRAME || element == Element.EMBED ) {
			s = attrMap.get( Attribute.SRC );
			if ( s != null )
				urls.put( s.toString(), null );
		}
		// BASE + HREF (change context!)
		else if ( element == Element.BASE && base == null ) {
			s = attrMap.get( Attribute.HREF );
			if ( s != null )
				base = s.toString();
		}
		// META REFRESH/LOCATION
		else if ( element == Element.META ) {
			final MutableString equiv = attrMap.get( Attribute.HTTP_EQUIV );
			final MutableString content = attrMap.get( Attribute.CONTENT );
			if ( equiv != null && content != null ) {
				equiv.toLowerCase();

				// http-equiv="refresh" content="0;URL=http://foo.bar/..."
				if ( equiv.equals( "refresh" ) && ( metaRefresh == null ) ) {

					final int pos = URLEQUAL_PATTERN.search( content );
					if ( pos != -1 )
						metaRefresh = content.substring( pos + URLEQUAL_PATTERN.length() ).toString();
				}

				// http-equiv="location" content="http://foo.bar/..."
				if ( equiv.equals( "location" ) && ( metaLocation == null ) )
					metaLocation = attrMap.get( Attribute.CONTENT ).toString();
			}
		}

		return true;
	}

	/**
	 * Returns the URL specified by <samp>META </samp> <samp>HTTP-EQUIV </samp> elements of location type. More
	 * precisely, this method returns a non- <code>null</code> result iff there is at least one <samp>META HTTP-EQUIV
	 * </samp> element specifying a location URL (if there is more than one, we keep the first one).
	 * 
	 * @return the first URL specified by a <samp>META </samp> <samp>HTTP-EQUIV </samp> elements of location type, or
	 *         <code>null</code>.
	 */
	public String metaLocation() {
		return metaLocation;
	}

	/**
	 * Returns the URL specified by the <samp>BASE </samp> element. More precisely, this method returns a non-
	 * <code>null</code> result iff there is at least one <samp>BASE </samp> element specifying a derelativisation URL
	 * (if there is more than one, we keep the first one).
	 * 
	 * @return the first URL specified by a <samp>BASE </samp> element, or <code>null</code>.
	 */
	public String base() {
		return base;
	}

	/**
	 * Returns the URL specified by <samp>META </samp> <samp>HTTP-EQUIV </samp> elements of refresh type. More
	 * precisely, this method returns a non- <code>null</code> result iff there is at least one <samp>META HTTP-EQUIV
	 * </samp> element specifying a refresh URL (if there is more than one, we keep the first one).
	 * 
	 * @return the first URL specified by a <samp>META </samp> <samp>HTTP-EQUIV </samp> elements of refresh type, or
	 *         <code>null</code>.
	 */
	public String metaRefresh() {
		return metaRefresh;
	}
	
	public boolean isIncludeImagesSources() {
		return includeImagesSources;
	}

	public void setIncludeImagesSources(boolean includeImagesSources) {
		this.includeImagesSources = includeImagesSources;
	}
}
