package ca.bc.gov.brmb.common.rest.endpoints.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.List;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.AbstractExecutionAwareRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.HeaderGroup;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestProxy {

	private final Logger logger = LoggerFactory.getLogger(RequestProxy.class);  

	/**
	 * These are the "hop-by-hop" headers that should not be copied.
	 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html I use an
	 * HttpClient HeaderGroup class instead of Set&lt;String&gt; because this
	 * approach does case insensitive lookup faster.
	 */
	private static final HeaderGroup hopByHopHeaders;
	static {
		hopByHopHeaders = new HeaderGroup();
		String[] headers = new String[] { "Connection", "Keep-Alive",
				"Proxy-Authenticate", "Proxy-Authorization", "TE", "Trailers",
				"Transfer-Encoding", "Upgrade" };
		for (String header : headers) {
			hopByHopHeaders.addHeader(new BasicHeader(header, null));
		}
	}

	private static final BitSet asciiQueryChars;
	static {
		char[] c_unreserved = "_-!.~'()*".toCharArray();// plus alphanum
		char[] c_punct = ",;:$&+=".toCharArray();
		char[] c_reserved = "?/[]@".toCharArray();// plus punct

		asciiQueryChars = new BitSet(128);
		for (char c = 'a'; c <= 'z'; c++) {
			asciiQueryChars.set(c);
		}
		for (char c = 'A'; c <= 'Z'; c++) {
			asciiQueryChars.set(c);
		}
		for (char c = '0'; c <= '9'; c++) {
			asciiQueryChars.set(c);
		}
		for (char c : c_unreserved) {
			asciiQueryChars.set(c);
		}
		for (char c : c_punct) {
			asciiQueryChars.set(c);
		}
		for (char c : c_reserved) {
			asciiQueryChars.set(c);
		}

		asciiQueryChars.set('%');// leave existing percent escapes in
										// place
	}
	
	private String cookiePrefix = "!Proxy!"+RequestProxy.class.getName();
	
	private String targetUri;
	private HttpHost targetHost;
	private HttpClient proxyClient;
	  
	private boolean doPreserveHost = false;
	private boolean doPreserveCookies = false;
	private boolean doForwardIP = true;
	private boolean doSendUrlFragment = true;
	private boolean doHandleRedirects = false;
	private int connectTimeout = -1;

	public RequestProxy(String targetUri) {
		if(targetUri==null||targetUri.trim().length()==0) {
			throw new IllegalArgumentException("targetUri cannot be null.");
		}
		this.targetUri = targetUri;

		URI targetUriObj;
		try {
			targetUriObj = new URI(targetUri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("targetUri must be a valid URI", e);
		}
		targetHost = URIUtils.extractHost(targetUriObj);
		
		if(cookiePrefix==null||cookiePrefix.trim().length()==0) {
			throw new IllegalArgumentException("cookiePrefix cannot be null.");
		}

		RequestConfig.Builder builder = RequestConfig.custom()
				.setRedirectsEnabled(doHandleRedirects)
				.setCookieSpec(CookieSpecs.IGNORE_COOKIES) // we handle them in
															// the servlet
															// instead
				.setConnectTimeout(connectTimeout);
		
		RequestConfig requestConfig = builder.build();
		
		proxyClient = HttpClientBuilder.create()
				.setDefaultRequestConfig(requestConfig).build();
	}

	public void proxy(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) throws ProxyException {
		logger.debug("<RequestProxy");

		HttpResponse proxyResponse = null;
		HttpRequest proxyRequest = null;
		try {

			// Make the Request
			// note: we won't transfer the protocol version because I'm not sure
			// it would truly be compatible
			String method = servletRequest.getMethod();
			String proxyRequestUri = rewriteUrlFromRequest(servletRequest);
			// spec: RFC 2616, sec 4.3: either of these two headers signal that
			// there is a message body.
			if (servletRequest.getHeader(HttpHeaders.CONTENT_LENGTH) != null
					|| servletRequest.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {
				proxyRequest = newProxyRequestWithEntity(method,
						proxyRequestUri, servletRequest);
			} else {
				proxyRequest = new BasicHttpRequest(method, proxyRequestUri);
			}

			copyRequestHeaders(servletRequest, proxyRequest);

			setXForwardedForHeader(servletRequest, proxyRequest);
			// Execute the request
			proxyResponse = doExecute(servletRequest, servletResponse,
					proxyRequest);

			// Process the response:

			// Pass the response code. This method with the "reason phrase" is
			// deprecated but it's the
			// only way to pass the reason along too.
			int statusCode = proxyResponse.getStatusLine().getStatusCode();
			// noinspection deprecation
			//.setStatus(statusCode, proxyResponse.getStatusLine().getReasonPhrase());
			servletResponse.setStatus(statusCode);

			// Copying response headers to make sure SESSIONID or other Cookie
			// which comes from the remote
			// server will be saved in client when the proxied url was
			// redirected to another one.
			// See issue
			// [#51](https://github.com/mitre/HTTP-Proxy-Servlet/issues/51)
			copyResponseHeaders(proxyResponse, servletRequest, servletResponse);

			if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
				// 304 needs special handling. See:
				// http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
				// Don't send body entity/content!
				servletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
			} else {
				// Send the content to the client
				copyResponseEntity(proxyResponse, servletResponse,
						proxyRequest, servletRequest);
			}

		} catch (Exception e) {
			
			// abort request, according to best practice with HttpClient
			if (proxyRequest!=null && proxyRequest instanceof AbstractExecutionAwareRequest) {
				AbstractExecutionAwareRequest executionAwareRequest = (AbstractExecutionAwareRequest) proxyRequest;
				executionAwareRequest.abort();
			}

			throw new ProxyException(e);

		} finally {
			// make sure the entire entity was consumed, so the connection is
			// released
			if (proxyResponse != null) {
				consumeQuietly(proxyResponse.getEntity());
			}
			// Note: Don't need to close servlet outputStream:
			// http://stackoverflow.com/questions/1159168/should-one-call-close-on-httpservletresponse-getoutputstream-getwriter
		}

		logger.debug(">RequestProxy");
	}

	/**
	 * HttpClient v4.1 doesn't have the
	 * {@link org.apache.http.util.EntityUtils#consumeQuietly(org.apache.http.HttpEntity)}
	 * method.
	 */
	private void consumeQuietly(HttpEntity entity) {
		try {
			EntityUtils.consume(entity);
		} catch (IOException e) {
			// do nothing
			logger.warn(e.getMessage(), e);
		}
	}

	/**
	 * Copy response body data (the entity) from the proxy to the servlet
	 * client.
	 */
	private static void copyResponseEntity(HttpResponse proxyResponse,
			HttpServletResponse servletResponse, HttpRequest proxyRequest,
			HttpServletRequest servletRequest) throws IOException {
		HttpEntity entity = proxyResponse.getEntity();
		if (entity != null) {
			OutputStream servletOutputStream = servletResponse
					.getOutputStream();
			entity.writeTo(servletOutputStream);
		}
	}

	/** Copy proxied response headers back to the servlet client. */
	private void copyResponseHeaders(HttpResponse proxyResponse,
			HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {
		for (Header header : proxyResponse.getAllHeaders()) {
			copyResponseHeader(servletRequest, servletResponse, header);
		}
	}

	/**
	 * Copy a proxied response header back to the servlet client. This is easily
	 * overwritten to filter out certain headers if desired.
	 */
	private void copyResponseHeader(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse, Header header) {
		String headerName = header.getName();
		if (hopByHopHeaders.containsHeader(headerName)) {
			return;
		}
		String headerValue = header.getValue();
		if (headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE)
				|| headerName
						.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE2)) {
			copyProxyCookie(servletRequest, servletResponse, headerValue);
		} else if (headerName.equalsIgnoreCase(HttpHeaders.LOCATION)) {
			// LOCATION Header may have to be rewritten.
			servletResponse.addHeader(headerName,
					rewriteUrlFromResponse(servletRequest, headerValue));
		} else {
			servletResponse.addHeader(headerName, headerValue);
		}
	}

	/**
	 * For a redirect response from the target server, this translates
	 * {@code theUrl} to redirect to and translates it to one the original client
	 * can use.
	 */
	private String rewriteUrlFromResponse(HttpServletRequest servletRequest,
			String theUrl) {
		
		if (theUrl.startsWith(targetUri)) {
			/*-
			 * The URL points back to the back-end server.
			 * Instead of returning it verbatim we replace the target path with our
			 * source path in a way that should instruct the original client to
			 * request the URL pointed through this Proxy.
			 * We do this by taking the current request and rewriting the path part
			 * using this servlet's absolute path and the path from the returned URL
			 * after the base target URL.
			 */
			StringBuffer curUrl = servletRequest.getRequestURL();// no query
			int pos;
			// Skip the protocol part
			if ((pos = curUrl.indexOf("://")) >= 0) {
				// Skip the authority part
				// + 3 to skip the separator between protocol and authority
				if ((pos = curUrl.indexOf("/", pos + 3)) >= 0) {
					// Trim everything after the authority part.
					curUrl.setLength(pos);
				}
			}
			// Context path starts with a / if it is not blank
			curUrl.append(servletRequest.getContextPath());
			// Servlet path starts with a / if it is not blank
			curUrl.append(servletRequest.getServletPath());
			curUrl.append(theUrl, targetUri.length(), theUrl.length());
			theUrl = curUrl.toString();
		}
		return theUrl;
	}

	/**
	 * Copy cookie from the proxy to the servlet client. Replaces cookie path to
	 * local path and renames cookie to avoid collisions.
	 */
	private void copyProxyCookie(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse, String headerValue) {
		List<HttpCookie> cookies = HttpCookie.parse(headerValue);
		String path = servletRequest.getContextPath(); // path starts with / or
														// is empty string
		path += servletRequest.getServletPath(); // servlet path starts with /
													// or is empty string
		if (path.isEmpty()) {
			path = "/";
		}

		for (HttpCookie cookie : cookies) {
			// set cookie name prefixed w/ a proxy value so it won't collide w/
			// other cookies
			String proxyCookieName = doPreserveCookies ? cookie.getName()
					: getCookieNamePrefix(cookie.getName()) + cookie.getName();
			Cookie servletCookie = new Cookie(proxyCookieName,
					cookie.getValue());
			servletCookie.setComment(cookie.getComment());
			servletCookie.setMaxAge((int) cookie.getMaxAge());
			servletCookie.setPath(path); // set to the path of the proxy servlet
			// don't set cookie domain
			servletCookie.setSecure(cookie.getSecure());
			servletCookie.setVersion(cookie.getVersion());
			servletResponse.addCookie(servletCookie);
		}
	}

	/** The string prefixing rewritten cookies. */
	private String getCookieNamePrefix(String name) {
		return this.cookiePrefix;
	}

	/**
	 * Copy a request header from the servlet client to the proxy request. This
	 * is easily overridden to filter out certain headers if desired.
	 */
	private void copyRequestHeader(HttpServletRequest servletRequest,
			HttpRequest proxyRequest, String headerName) {
		// Instead the content-length is effectively set via InputStreamEntity
		if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
			return;
		}
		if (hopByHopHeaders.containsHeader(headerName)) {
			return;
		}

		Enumeration<String> headers = servletRequest.getHeaders(headerName);
		while (headers.hasMoreElements()) {// sometimes more than one value
			String headerValue = headers.nextElement();
			// In case the proxy host is running multiple virtual servers,
			// rewrite the Host header to ensure that we get content from
			// the correct virtual server
			if (!doPreserveHost
					&& headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
				
				headerValue = targetHost.getHostName();
				if (targetHost.getPort() != -1) {
					headerValue += ":" + targetHost.getPort();
				}
			} else if (!doPreserveCookies
					&& headerName
							.equalsIgnoreCase(org.apache.http.cookie.SM.COOKIE)) {
				headerValue = getRealCookie(headerValue);
			}
			proxyRequest.addHeader(headerName, headerValue);
		}
	}

	/**
	 * Take any client cookies that were originally from the proxy and prepare
	 * them to send to the proxy. This relies on cookie headers being set
	 * correctly according to RFC 6265 Sec 5.4. This also blocks any local
	 * cookies from being sent to the proxy.
	 */
	private String getRealCookie(String cookieValue) {
		StringBuilder escapedCookie = new StringBuilder();
		String cookies[] = cookieValue.split("[;,]");
		for (String cookie : cookies) {
			String cookieSplit[] = cookie.split("=");
			if (cookieSplit.length == 2) {
				String cookieName = cookieSplit[0].trim();
				if (cookieName.startsWith(getCookieNamePrefix(cookieName))) {
					cookieName = cookieName.substring(getCookieNamePrefix(
							cookieName).length());
					if (escapedCookie.length() > 0) {
						escapedCookie.append("; ");
					}
					escapedCookie.append(cookieName).append("=")
							.append(cookieSplit[1].trim());
				}
			}
		}
		return escapedCookie.toString();
	}

	private HttpResponse doExecute(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse, HttpRequest proxyRequest)
			throws IOException {

		logger.info("proxy " + servletRequest.getMethod() + " uri: "
				+ servletRequest.getRequestURI() + " -- "
				+ proxyRequest.getRequestLine().getUri());
	
		return proxyClient.execute(targetHost, proxyRequest);
	}

	private void setXForwardedForHeader(HttpServletRequest servletRequest,
			HttpRequest proxyRequest) {
		if (doForwardIP) {
			String forHeaderName = "X-Forwarded-For";
			String forHeader = servletRequest.getRemoteAddr();
			String existingForHeader = servletRequest.getHeader(forHeaderName);
			if (existingForHeader != null) {
				forHeader = existingForHeader + ", " + forHeader;
			}
			proxyRequest.setHeader(forHeaderName, forHeader);

			String protoHeaderName = "X-Forwarded-Proto";
			String protoHeader = servletRequest.getScheme();
			proxyRequest.setHeader(protoHeaderName, protoHeader);
		}
	}

	/**
	 * Copy request headers from the servlet client to the proxy request. This
	 * is easily overridden to add your own.
	 */
	private void copyRequestHeaders(HttpServletRequest servletRequest,
			HttpRequest proxyRequest) {
		// Get an Enumeration of all of the header names sent by the client

		Enumeration<String> enumerationOfHeaderNames = servletRequest
				.getHeaderNames();
		while (enumerationOfHeaderNames.hasMoreElements()) {
			String headerName = enumerationOfHeaderNames.nextElement();
			copyRequestHeader(servletRequest, proxyRequest, headerName);
		}
	}

	private static HttpRequest newProxyRequestWithEntity(String method,
			String proxyRequestUri, HttpServletRequest servletRequest)
			throws IOException {
		HttpEntityEnclosingRequest eProxyRequest = new BasicHttpEntityEnclosingRequest(
				method, proxyRequestUri);
		// Add the input entity (streamed)
		// note: we don't bother ensuring we close the servletInputStream since
		// the container handles it
		eProxyRequest.setEntity(new InputStreamEntity(servletRequest
				.getInputStream(), getContentLength(servletRequest)));
		return eProxyRequest;
	}

	// Get the header value as a long in order to more correctly proxy very
	// large requests
	private static long getContentLength(HttpServletRequest request) {
		String contentLengthHeader = request.getHeader("Content-Length");
		if (contentLengthHeader != null) {
			return Long.parseLong(contentLengthHeader);
		}
		return -1L;
	}

	/**
	 * Reads the request URI from {@code servletRequest} and rewrites it,
	 * considering targetUri. It's used to make the new request.
	 */
	private String rewriteUrlFromRequest(HttpServletRequest servletRequest) {
		StringBuilder uri = new StringBuilder(500);
		uri.append(targetUri);
		// Handle the path given to the servlet
		if (servletRequest.getPathInfo() != null) {// ex: /my/path.html
			uri.append(encodeUriQuery(servletRequest.getPathInfo()));
		}
		// Handle the query string & fragment
		String queryString = servletRequest.getQueryString();// ex:(following
																// '?'):
																// name=value&foo=bar#fragment
		String fragment = null;
		// split off fragment from queryString, updating queryString if found
		if (queryString != null) {
			int fragIdx = queryString.indexOf('#');
			if (fragIdx >= 0) {
				fragment = queryString.substring(fragIdx + 1);
				queryString = queryString.substring(0, fragIdx);
			}
		}

		queryString = rewriteQueryStringFromRequest(servletRequest, queryString);
		if (queryString != null && queryString.length() > 0) {
			uri.append('?');
			uri.append(encodeUriQuery(queryString));
		}

		if (doSendUrlFragment && fragment != null) {
			uri.append('#');
			uri.append(encodeUriQuery(fragment));
		}
		return uri.toString();
	}

	private static String rewriteQueryStringFromRequest(
			HttpServletRequest servletRequest, String queryString) {
		return queryString;
	}

	/**
	 * Encodes characters in the query or fragment part of the URI.
	 *
	 * <p>
	 * Unfortunately, an incoming URI sometimes has characters disallowed by the
	 * spec. HttpClient insists that the outgoing proxied request has a valid
	 * URI because it uses Java's {@link URI}. To be more forgiving, we must
	 * escape the problematic characters. See the URI class for the spec.
	 *
	 * @param in
	 *            example: name=value&amp;foo=bar#fragment
	 */
	private static CharSequence encodeUriQuery(CharSequence in) {
		// Note that I can't simply use URI.java to encode because it will
		// escape pre-existing escaped things.
		StringBuilder outBuf = null;
		@SuppressWarnings("resource")
		Formatter formatter = null;				
		try {
			for (int i = 0; i < in.length(); i++) {
				char c = in.charAt(i);
				boolean escape = true;
				if (c < 128) {
					if (asciiQueryChars.get(c)) {
						escape = false;
					}
				} else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {// not-ascii
					escape = false;
				}
				if (!escape) {
					if (outBuf != null)
						outBuf.append(c);
				} else {
					
	
					// escape
					if (outBuf == null) {
						outBuf = new StringBuilder(in.length() + 5 * 3);
						outBuf.append(in, 0, i);
						formatter = new Formatter(outBuf);
					}
					// leading %, 0 padded, width 2, capital hex
					formatter.format("%%%02X", Integer.valueOf(c));
				}
			}
		
		} finally {
			if(formatter!=null) {
				formatter.close();
			}
		}
		return outBuf != null ? outBuf : in;
	}

	public boolean isDoPreserveHost() {
		return doPreserveHost;
	}

	public void setDoPreserveHost(boolean doPreserveHost) {
		this.doPreserveHost = doPreserveHost;
	}

	public boolean isDoPreserveCookies() {
		return doPreserveCookies;
	}

	public void setDoPreserveCookies(boolean doPreserveCookies) {
		this.doPreserveCookies = doPreserveCookies;
	}

	public boolean isDoForwardIP() {
		return doForwardIP;
	}

	public void setDoForwardIP(boolean doForwardIP) {
		this.doForwardIP = doForwardIP;
	}

	public boolean isDoSendUrlFragment() {
		return doSendUrlFragment;
	}

	public void setDoSendUrlFragment(boolean doSendUrlFragment) {
		this.doSendUrlFragment = doSendUrlFragment;
	}

	public boolean isDoHandleRedirects() {
		return doHandleRedirects;
	}

	public void setDoHandleRedirects(boolean doHandleRedirects) {
		this.doHandleRedirects = doHandleRedirects;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public String getCookiePrefix() {
		return cookiePrefix;
	}

	public void setCookiePrefix(String cookiePrefix) {
		this.cookiePrefix = cookiePrefix;
	}

}
