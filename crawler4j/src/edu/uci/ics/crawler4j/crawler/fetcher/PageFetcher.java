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

package edu.uci.ics.crawler4j.crawler.fetcher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import edu.uci.ics.crawler4j.cache.ICacheProvider;
import edu.uci.ics.crawler4j.crawler.IdleConnectionMonitorThread;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.configuration.ICrawlerSettings;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;

/**
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public final class PageFetcher implements IPageFetcher {
	private static final Logger logger = Logger.getLogger(PageFetcher.class);

	private ICacheProvider cache;
	private ThreadSafeClientConnManager connectionManager;

	private DefaultHttpClient httpclient;

	private Object mutex = PageFetcher.class.toString() + "_MUTEX";

	private int processedCount = 0;
	private long startOfPeriod = 0;
	private long lastFetchTime = 0;

	private final long politenessDelay;
	private final int maxDownloadSize;
	private final boolean show404Pages;
	private final boolean ignoreBinary;

	private IdleConnectionMonitorThread connectionMonitorThread = null;

	public PageFetcher(ICrawlerSettings config) {
		politenessDelay = config.getPolitenessDelay();
		maxDownloadSize = config.getMaxDownloadSize();
		show404Pages = config.getShow404Pages();
		ignoreBinary = !config.getIncludeBinaryContent();
		cache = config.getCacheProvider();
		
		HttpParams params = new BasicHttpParams();
		HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
		paramsBean.setVersion(HttpVersion.HTTP_1_1);
		paramsBean.setContentCharset("UTF-8");
		paramsBean.setUseExpectContinue(false);

		params.setParameter("http.useragent", config.getUserAgent());

		params.setIntParameter("http.socket.timeout",config.getSocketTimeout());

		params.setIntParameter("http.connection.timeout",
				config.getConnectionTimeout());

		params.setBooleanParameter("http.protocol.handle-redirects", false);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

		if (config.getAllowHttps()) {
			schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		}

		connectionManager = new ThreadSafeClientConnManager(schemeRegistry);
		connectionManager.setDefaultMaxPerRoute(config.getMaxConnectionsPerHost());
		connectionManager.setMaxTotal(config.getMaxTotalConnections());
		
		logger.setLevel(Level.INFO);
		httpclient = new DefaultHttpClient(connectionManager, params);
	}

	public synchronized void startConnectionMonitorThread() {
		if (connectionMonitorThread == null) {
			connectionMonitorThread = new IdleConnectionMonitorThread(connectionManager);
		}
		connectionMonitorThread.start();
	}

	public synchronized void stopConnectionMonitorThread() {
		if (connectionMonitorThread != null) {
			connectionManager.shutdown();
			connectionMonitorThread.shutdown();
		}
	}

	private void waitPolitenessDealyIfNeeded() throws InterruptedException {
		synchronized(mutex) {
			long now = (new Date()).getTime();
			if (now - startOfPeriod > 10000) {
				logger.info("Number of pages fetched per second: " + processedCount
						/ ((now - startOfPeriod) / 1000));
				processedCount = 0;
				startOfPeriod = now;
			}
			processedCount++;

			if (now - lastFetchTime < politenessDelay) {
				Thread.sleep(politenessDelay - (now - lastFetchTime));
			}
			lastFetchTime = (new Date()).getTime();
		}
	}
	
	@Override
	public int fetch(Page page) {
		String toFetchURL = page.getWebURL().getURL();
		HttpGet get = null;
		HttpEntity entity = null;
		try {
			get = new HttpGet(toFetchURL);
			
			waitPolitenessDealyIfNeeded();
			
			if (cache != null) {
				String eTag = cache.getCachedETag(toFetchURL);
				Calendar lastMod = cache.getLastModified(toFetchURL);
				if (eTag != null) {
					get.addHeader("If-None-Match", eTag);
				}
				
				if (lastMod != null) {
					get.addHeader("If-Modified-Since", DateUtils.formatDate(lastMod.getTime()));
				}
			}
			
			HttpResponse response = httpclient.execute(get);
			entity = response.getEntity();

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header header = response.getFirstHeader("Location");
				if (header != null) {
					String movedToUrl = header.getValue();
					//Handle redirects that are relative (violates RFC 1945)
					if (movedToUrl != null && movedToUrl.startsWith("/")) {
						movedToUrl = URLCanonicalizer.getCanonicalURL(movedToUrl, toFetchURL).toExternalForm();
					}
					page.setRedirectedURL(movedToUrl);
				}
				return PageFetchStatus.Moved;
			} else if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
				cache.setupCachedPage(page);
				page.setFromCache(true);
				return PageFetchStatus.NotModified;
			} else if (statusCode != HttpStatus.SC_OK) {
				if (statusCode != HttpStatus.SC_NOT_FOUND) {
					logger.info("Failed: " + response.getStatusLine().toString() + ", while fetching " + toFetchURL);
				} else if (show404Pages) {
					logger.info("Not Found: " + toFetchURL + " (Link found in doc#: "
							+ page.getWebURL().getParentDocid() + ")");
				}
				return statusCode;
			}

			if (entity == null) {
				get.abort();
				return PageFetchStatus.UnknownError;
			}
			
			long size = entity.getContentLength();
			if (size == -1) {
				Header length = response.getLastHeader("Content-Length");
				if (length != null) {
					size = Integer.parseInt(length.getValue());
				} else {
					size = -1;
				}
				
				if (size >= 0)
					logger.debug("getContentLength failed but header exists!?");
			}
			
			if (size > maxDownloadSize) {
				EntityUtils.consume(entity);
				return PageFetchStatus.PageTooBig;
			}

			boolean isBinary = false;
			String charset = "UTF-8";
			Header type = entity.getContentType();
			if (type != null) {
				int semicolonPos = type.getValue().indexOf(';');
				String typeStr;
				if (semicolonPos > 0) {
					typeStr = type.getValue().toLowerCase().substring(0, semicolonPos);
				} else {
					typeStr = type.getValue().toLowerCase();
				}
				page.setContentType(typeStr);
				
				if (!typeStr.startsWith("text/")) {
					isBinary = true;
					if (ignoreBinary) {
						return PageFetchStatus.PageIsBinary;
					}
				}
				if (!isBinary && typeStr.contains("charset=")) {
					charset = type.getValue().substring(typeStr.indexOf("charset=") + 8);
				}
			}
			
			parseCacheHeaders(page, response);

			if (loadPage(page, entity.getContent(), (int) size, isBinary, charset)) {
				page.setFromCache(false);
				return PageFetchStatus.OK;
			} else {
				return PageFetchStatus.PageLoadError;
			}
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage() + " while fetching " + toFetchURL
					+ " (link found in doc #" + page.getWebURL().getParentDocid() + ")");
			return PageFetchStatus.FatalTransportError;
		} catch (IllegalStateException e) {
			// ignoring exceptions that occur because of not registering https
			// and other schemes
		} catch (Exception e) {
			if (e.getMessage() == null) {
				logger.error("Error while fetching " + page.getWebURL().getURL());
			} else {
				logger.error(e.getMessage() + " while fetching " + page.getWebURL().getURL());
			}
		} finally {
			try {
				if (entity != null) {
					EntityUtils.consume(entity);
				} else if (get != null) {
					get.abort();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return PageFetchStatus.UnknownError;
	}
	
	private void parseCacheHeaders(final Page page, final HttpResponse response) {
		boolean canCache = true;
		Header cacheControl = response.getLastHeader("cache-control");
		if (cacheControl != null) {
			String[] cacheControls = cacheControl.getValue().split(",");
			for (String cc : cacheControls) {
				if (cc.equalsIgnoreCase("no-cache") ||
						cc.equalsIgnoreCase("no-store")) {
					canCache = false;
					break;
				}
			}
		}
		if (canCache) {
			Header lastMod = response.getLastHeader("last-modified");
			if (lastMod != null) {
				try {
					Calendar lastModCal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
					lastModCal.setTimeInMillis(DateUtils.parseDate(lastMod.getValue()).getTime());
					page.setLastModified(lastModCal);
				} catch (DateParseException e) {
					logger.debug("Unable to parse last modified date: " + lastMod.getValue(), e);
				}
			}
			Header etag = response.getLastHeader("etag");
			if (etag != null) 
				page.setETag(etag.getValue());
		}
	}
	
	private boolean loadPage(final Page p, final InputStream in, 
			final int totalsize, final boolean isBinary, final String encoding) {
		ByteBuffer bBuf;
		
		if (totalsize > 0) {
			bBuf = ByteBuffer.allocate(totalsize + 1024);
		} else {
			bBuf = ByteBuffer.allocate(maxDownloadSize);
		}
		final byte[] b = new byte[1024];
		int len;
		double finished = 0;
		try {
			while ((len = in.read(b)) != -1) {
				if (finished + b.length > bBuf.capacity()) {
					break;
				}
				bBuf.put(b, 0, len);
				finished += len;
			}
		} catch (final BufferOverflowException boe) {
			System.out.println("Page size exceeds maximum allowed.");
			return false;
		} catch (final Exception e) {
			System.err.println(e.getMessage());
			return false;
		}

		bBuf.flip();
		if (isBinary) {
			byte[] tmp = new byte[bBuf.limit()];
			bBuf.get(tmp);
			p.setBinaryData(tmp);
		} else {
			String html = Charset.forName(encoding).decode(bBuf).toString();
			if (html.length() == 0) {
				return false;
			}
			p.setHTML(html);
		}
		return true;
	}

	public void setProxy(String proxyHost, int proxyPort) {
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	public void setProxy(String proxyHost, int proxyPort, String username, String password) {
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort),
				new UsernamePasswordCredentials(username, password));
		setProxy(proxyHost, proxyPort);
	}

}
