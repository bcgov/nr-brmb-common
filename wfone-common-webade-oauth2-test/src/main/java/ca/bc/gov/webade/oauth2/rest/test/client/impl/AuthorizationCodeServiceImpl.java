package ca.bc.gov.webade.oauth2.rest.test.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.brmb.common.webade.oauth2.token.client.Oauth2ClientException;
import ca.bc.gov.brmb.common.webade.oauth2.token.client.resource.AccessToken;
import ca.bc.gov.webade.oauth2.rest.test.client.AuthorizationCodeService;
import ca.bc.gov.webade.oauth2.rest.test.resource.AuthorizationCode;

/**
 * This class exists to provide OAUTH2 utilities for integration tests.
 * 
 * @author phowells
 * 
 */
public class AuthorizationCodeServiceImpl implements AuthorizationCodeService {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationCodeServiceImpl.class);

	public static final String REQUEST_ID_HEADER_PARAMETER = "org.apache.logging.log4j.request.id.header";
	public static final String DEFAULT_REQUEST_ID_HEADER = "requestId";

	public static final String LOG4J_REQUEST_ID_MDC_KEY_PARAMETER = "org.apache.logging.log4j.request.id.mdc.key";
	public static final String DEFAULT_REQUEST_IDLOG4J_MDC_KEY = "requestId";

	private String requestIdHeader;
	private String log4jRequestIdMdcKey;

	private String clientId;
	private String authorizeUrl;

	public AuthorizationCodeServiceImpl(String clientId, String authorizeUrl) {
		logger.debug("<AuthorizationCodeServiceImpl");

		this.requestIdHeader = DEFAULT_REQUEST_ID_HEADER;
		this.log4jRequestIdMdcKey = DEFAULT_REQUEST_IDLOG4J_MDC_KEY;

		this.clientId = clientId;
		this.authorizeUrl = authorizeUrl;

		logger.debug(">AuthorizationCodeServiceImpl");
	}

	@Override
	public AuthorizationCode getAuthorizationCode(String scope, String redirectUri, Long organizationId, String siteMinderUserType, String siteMinderUserIdentifier, String siteMinderAuthoritativePartyIdentifier, String username, String secret) throws Oauth2ClientException {
		return getAuthorizationCode(scope,
				redirectUri, organizationId, siteMinderUserType,
				siteMinderUserIdentifier, siteMinderAuthoritativePartyIdentifier,
				username, secret,
				null);
	}

	@Override
	public AuthorizationCode getAuthorizationCode(String scope,
			String redirectUri, Long organizationId, String siteMinderUserType,
			String siteMinderUserIdentifier, String siteMinderAuthoritativePartyIdentifier,
			String username, String secret,
			String state) throws Oauth2ClientException {
		logger.debug("<getAuthorizationCode");

		AuthorizationCode result = null;

		try {

			long startTime = System.currentTimeMillis();

			URL url = new URL(
					authorizeUrl
							+ '?'
							+ ((clientId == null) ? "" : "client_id="
									+ URLEncoder.encode(clientId, "UTF-8")
									+ '&')
							+ ((scope == null) ? "" : "scope="
									+ URLEncoder.encode(scope, "UTF-8") + '&')
							+ ((organizationId == null) ? ""
									: "user_oauth_organization="
											+ organizationId + '&')
							+ ((redirectUri == null) ? "" : "redirect_uri="
									+ URLEncoder.encode(redirectUri, "UTF-8")
									+ '&')
							+ ((state == null) ? "" : "state="
									+ URLEncoder.encode(state, "UTF-8") + '&')
							+ "response_type=code"
							+ "&ca.bc.gov.webade.developer.filter.USERGUID="+siteMinderUserIdentifier
							+ "&ca.bc.gov.webade.developer.filter.PASSWORD="+secret
							//+ "&disableDeveloperFilter=true"
							);
			logger.debug("url=" + url);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);

			try {
				conn.setRequestMethod("GET");
				
				if(log4jRequestIdMdcKey!=null&&log4jRequestIdMdcKey.trim().length()!=0&&requestIdHeader!=null&&requestIdHeader.trim().length()!=0) {
					String requestId = (String) ThreadContext.get(log4jRequestIdMdcKey);
					if(requestId!=null) {
						logger.debug("requestId=" + requestId);
						conn.setRequestProperty(requestIdHeader, requestId);
					}
				}

				conn.setDoInput(true);
				conn.setDoOutput(true);

				int responseCode = conn.getResponseCode();
				logger.info(responseCode + ":" + conn.getResponseMessage());

				if (responseCode == 200) {
					// OK indicates that we are being prompted for approval or
					// organization selection

					String htmlPage = getResponseString(conn);
					logger.debug(htmlPage);

					throw new java.lang.IllegalStateException(
							"Unexpected HTML response.");

				} else if (responseCode == 302) {
					// Redirect indicates that we have our authorization code or we have hit the Siteminder Login page
					
					String location = conn.getHeaderField("location");
					logger.debug("location=" + location);

					Set<Cookie> cookies = getCookies(conn);

					if(location.startsWith(redirectUri)) {
						// We are being redirected to our redirectUrl so we either have our code or we have an error
						
						result = handleOauthRedirect(responseCode, location);
						
					} else {
						// We will assume that we are being redirected by Siteminder
						
						cookies = handleSiteminderRedirect(location, cookies, siteMinderUserType, username, secret, redirectUri);
						
						result = authorize(url, cookies, redirectUri);
					}

				} else if (responseCode > 399) {

					String responseString = getErrorString(conn);
					logger.debug(responseString);

					throw new Oauth2ClientException(responseString,
							responseCode);
				}

			} finally {
				conn.disconnect();
			}

			long duration = System.currentTimeMillis() - startTime;

			double seconds = (double) duration / (double) 1000;

			logger.info("processing time: " + seconds + " seconds");

		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		logger.debug(">getAuthorizationCode " + result);
		return result;
	}

	private static Set<Cookie> getCookies(HttpURLConnection conn) {
		Set<Cookie> result = new HashSet<Cookie>();
		
		Map<String, List<String>> headerFields = conn.getHeaderFields();
		for(String key:headerFields.keySet()) {
			List<String> values = headerFields.get(key);
			for(String value:values) {
				if("Set-Cookie".equals(key)) {
					logger.debug(key+"="+value);
					String cookie = value.substring(0, value.indexOf(";"));
			        String cookieName = cookie.substring(0, cookie.indexOf("="));
			        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
			        result.add(new Cookie(cookieName, cookieValue));
				}
			}
		}
		
		return result;
	}

	private static String getResponseString(HttpURLConnection conn) throws IOException {
		String result = null;
		
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			is = conn.getInputStream();

			byte[] b = new byte[1024];
			int read = -1;
			while ((read = is.read(b)) != -1) {
				baos.write(b, 0, read);
			}
			is.close();
			is = null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
		
		result = baos.toString();
		
		return result;
	}
	
	private static String getErrorString(HttpURLConnection conn) throws IOException {
		String result = null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(InputStream is = conn.getErrorStream()) {
			
			if(is!=null) {
	
				byte[] b = new byte[1024];
				int read = -1;
				while ((read = is.read(b)) != -1) {
					baos.write(b, 0, read);
				}
			}
		}
		
		result = baos.toString();
		
		return result;
	}

	private AuthorizationCode authorize(URL url, Set<Cookie> cookies, String redirectUri) throws IOException, Oauth2ClientException {
		logger.debug("<authorize");

		AuthorizationCode result = null;

			long startTime = System.currentTimeMillis();

			logger.debug("url=" + url);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);

			try {
				conn.setRequestMethod("GET");
				
				if(log4jRequestIdMdcKey!=null&&log4jRequestIdMdcKey.trim().length()!=0&&requestIdHeader!=null&&requestIdHeader.trim().length()!=0) {
					String requestId = (String) ThreadContext.get(log4jRequestIdMdcKey);
					if(requestId!=null) {
						logger.debug("requestId=" + requestId);
						conn.setRequestProperty(requestIdHeader, requestId);
					}
				}
				
				if(cookies!=null) {
					logger.debug("cookies=" + toString(cookies));
					conn.setRequestProperty("Cookie", toString(cookies));
				}

				conn.setDoInput(true);
				conn.setDoOutput(true);

				int responseCode = conn.getResponseCode();
				logger.info(responseCode + ":" + conn.getResponseMessage());

				if (responseCode == 200) {
					// OK indicates that we are being prompted for approval or
					// organization selection

					String responseString = getResponseString(conn);
					logger.debug(responseString);

					throw new java.lang.IllegalStateException(
							"Unexpected HTML response.");

				} else if (responseCode == 302) {
					// Redirect indicates that we have our authorization code
					
					String location = conn.getHeaderField("location");
					logger.debug("location=" + location);

					cookies = new HashSet<Cookie>();
					
					Map<String, List<String>> headerFields = conn.getHeaderFields();
					for(String key:headerFields.keySet()) {
						List<String> values = headerFields.get(key);
						for(String value:values) {
							if("Set-Cookie".equals(key)) {
								logger.debug(key+"="+value);
								String cookie = value.substring(0, value.indexOf(";"));
						        String cookieName = cookie.substring(0, cookie.indexOf("="));
						        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
						        cookies.add(new Cookie(cookieName, cookieValue));
							}
						}
					}

					if(location.startsWith(redirectUri)) {
						// We are being redirected to our redirectUrl so we either have our code or we have an error
						
						result = handleOauthRedirect(responseCode, location);
						
					} else {
						
						String responseString = getResponseString(conn);
						logger.debug(responseString);

						throw new java.lang.IllegalStateException(
								"Unexpected HTML response.");
					}

				} else if (responseCode > 399) {

					String responseString = getErrorString(conn);
					logger.debug(responseString);

					throw new Oauth2ClientException(responseString,
							responseCode);
				}

			} finally {
				conn.disconnect();
			}

			long duration = System.currentTimeMillis() - startTime;

			double seconds = (double) duration / (double) 1000;

			logger.info("processing time: " + seconds + " seconds");

		logger.debug(">authorize " + result);
		return result;
	}

	private AccessToken authorizeImplicit(URL url, Set<Cookie> cookies, String redirectUri) throws IOException, Oauth2ClientException {
		logger.debug("<authorizeImplicit");

		AccessToken result = null;

			long startTime = System.currentTimeMillis();

			logger.debug("url=" + url);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);

			try {
				conn.setRequestMethod("GET");
				
				if(log4jRequestIdMdcKey!=null&&log4jRequestIdMdcKey.trim().length()!=0&&requestIdHeader!=null&&requestIdHeader.trim().length()!=0) {
					String requestId = (String) ThreadContext.get(log4jRequestIdMdcKey);
					if(requestId!=null) {
						logger.debug("requestId=" + requestId);
						conn.setRequestProperty(requestIdHeader, requestId);
					}
				}
				
				if(cookies!=null) {
					logger.debug("cookies=" + toString(cookies));
					conn.setRequestProperty("Cookie", toString(cookies));
				}

				conn.setDoInput(true);
				conn.setDoOutput(true);

				int responseCode = conn.getResponseCode();
				logger.info(responseCode + ":" + conn.getResponseMessage());

				if (responseCode == 200) {
					// OK indicates that we are being prompted for approval or
					// organization selection

					String responseString = getResponseString(conn);
					logger.debug(responseString);

					throw new java.lang.IllegalStateException(
							"Unexpected HTML response.");

				} else if (responseCode == 302) {
					// Redirect indicates that we have our authorization code
					
					String location = conn.getHeaderField("location");
					logger.debug("location=" + location);

					cookies = new HashSet<Cookie>();
					
					Map<String, List<String>> headerFields = conn.getHeaderFields();
					for(String key:headerFields.keySet()) {
						List<String> values = headerFields.get(key);
						for(String value:values) {
							if("Set-Cookie".equals(key)) {
								logger.debug(key+"="+value);
								String cookie = value.substring(0, value.indexOf(";"));
						        String cookieName = cookie.substring(0, cookie.indexOf("="));
						        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
						        cookies.add(new Cookie(cookieName, cookieValue));
							}
						}
					}

					if(location.startsWith(redirectUri)) {
						// We are being redirected to our redirectUrl so we either have our code or we have an error
						
						result = handleImplicitOauthRedirect(responseCode, location);
						
					} else {
						
						String responseString = getResponseString(conn);
						logger.debug(responseString);

						throw new java.lang.IllegalStateException(
								"Unexpected HTML response.");
					}

				} else if (responseCode > 399) {

					String responseString = getErrorString(conn);
					logger.debug(responseString);

					throw new Oauth2ClientException(responseString,
							responseCode);
				}

			} finally {
				conn.disconnect();
			}

			long duration = System.currentTimeMillis() - startTime;

			double seconds = (double) duration / (double) 1000;

			logger.info("processing time: " + seconds + " seconds");

		logger.debug(">authorizeImplicit " + result);
		return result;
	}

	private static AuthorizationCode handleOauthRedirect(int responseCode, String location) throws MalformedURLException, Oauth2ClientException {
		logger.debug("<handleOauthRedirect");

		AuthorizationCode result = null;
		
		URL locationUrl = new URL(location);
		String query = locationUrl.getQuery();
		logger.debug("query=" + query);

		AuthorizationCodeResponse response = new AuthorizationCodeResponse(parseUrlQueryString(query));
		if (response.error == null) {
			result = new AuthorizationCode(response.code,response.state);
		} else {
			throw new Oauth2ClientException(responseCode,
					response.error, response.errorDescription,
					response.errorUri, response.state);
		}
		
		logger.debug(">handleOauthRedirect "+result);
		return result;
	}

	private static AccessToken handleImplicitOauthRedirect(int responseCode, String location) throws MalformedURLException, Oauth2ClientException {
		logger.debug("<handleImplicitOauthRedirect");

		AccessToken result = null;
		
		URL locationUrl = new URL(location);
		String query = locationUrl.getQuery();
		logger.debug("query=" + query);
		
		String fragment = locationUrl.getRef();
		logger.debug("fragment=" + fragment);

		ImplicitResponse response = new ImplicitResponse(
				parseUrlQueryString(fragment));
		if (response.error == null) {
			result = new AccessToken(response.accessToken,
					response.tokenType,
					Long.valueOf(response.expiresIn),
					response.scope, response.state);
		} else {
			throw new Oauth2ClientException(responseCode,
					response.error, response.errorDescription,
					response.errorUri, response.state);
		}
		
		logger.debug(">handleImplicitOauthRedirect "+result);
		return result;
	}
	
	private Set<Cookie> handleSiteminderRedirect(String location, Set<Cookie> cookies, String userType, String username, String secret, String redirectUri) throws IOException, Oauth2ClientException {
		logger.debug("<handleSiteminderRedirect");

		Set<Cookie> result = null;
		
			long startTime = System.currentTimeMillis();
			
			Pattern pattern = Pattern.compile(".+(/[a-zA-Z]+/)logon.cgi\\?.+");
			Matcher matcher = pattern.matcher(location);
			
			if(matcher.find()) {
				String match = matcher.group(1);
				
				if(INTERNAL.equals(userType)) {
					location = location.replace(match, "/int/");
				} else {
					location = location.replace(match, "/capBceid/");
				}
				
			} else {
				throw new IllegalArgumentException("Unsupported Siteminder login URL: "+location);
			}
			
			URL url = new URL(location);
			logger.debug("url=" + url);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);

			try {
				conn.setRequestMethod("GET");
				
				if(log4jRequestIdMdcKey!=null&&log4jRequestIdMdcKey.trim().length()!=0&&requestIdHeader!=null&&requestIdHeader.trim().length()!=0) {
					String requestId = (String) ThreadContext.get(log4jRequestIdMdcKey);
					if(requestId!=null) {
						logger.debug("requestId=" + requestId);
						conn.setRequestProperty(requestIdHeader, requestId);
					}
				}
				
				if(cookies!=null) {
					logger.debug("cookies=" + toString(cookies));
					conn.setRequestProperty("Cookie", toString(cookies));
				}

				conn.setDoInput(true);
				conn.setDoOutput(true);

				int responseCode = conn.getResponseCode();
				logger.info(responseCode + ":" + conn.getResponseMessage());
				
				if (responseCode < 500) {
					// OK indicates that we are being prompted for approval or
					// organization selection

					String responseString = getResponseString(conn);
					logger.debug(responseString);
					
					Map<String, List<String>> headerFields = conn.getHeaderFields();
					for(String key:headerFields.keySet()) {
						List<String> values = headerFields.get(key);
						for(String value:values) {
							if("Set-Cookie".equals(key)) {
								logger.debug(key+"="+value);
								String cookie = value.substring(0, value.indexOf(";"));
						        String cookieName = cookie.substring(0, cookie.indexOf("="));
						        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
						        cookies.add(new Cookie(cookieName, cookieValue));
							}
						}
					}
					
					if(responseString.indexOf("preLogon.cgi")!=-1) {
						
						result = siteminderPreLogin(url, responseString, cookies, username, secret, redirectUri);
						
					} else {
						throw new IllegalStateException("Unknown Siteminder response.");
					}
					
				} else {

					String responseString = getErrorString(conn);
					logger.debug(responseString);

					throw new Oauth2ClientException(responseString, responseCode);
				}
				
			} finally {
				conn.disconnect();
			}

			long duration = System.currentTimeMillis() - startTime;

			double seconds = (double) duration / (double) 1000;

			logger.info("processing time: " + seconds + " seconds");
		
		logger.debug(">handleSiteminderRedirect "+result);
		return result;
	}

	private static String toString(Set<Cookie> cookies) {
		String result = "";
		
		for(Cookie cookie:cookies) {
			result += cookie.getName()+"="+cookie.getValue()+"; ";
		}
		
		return result;
	}

	private Set<Cookie> siteminderLogin(URL parentUrl, String htmlPage, Set<Cookie> cookies, String username, String secret, String redirectUri) throws IOException, Oauth2ClientException {
		logger.debug("<siteminderLogin");

		Set<Cookie> result = null;
		
		long startTime = System.currentTimeMillis();
		
		String action = getHtmlValue("<form[^>]+action\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", htmlPage);
	    String smenc = getHtmlValue("<input type=hidden name=\"SMENC\" value=\"(\\S+)\">", htmlPage);
	    String smlocale = getHtmlValue("<input type=hidden name=\"SMLOCALE\" value=\"(\\S+)\">", htmlPage);
	    String target = getHtmlValue("<input type=hidden name=target value=\"(\\S+)\">", htmlPage);
	    String smauthreason = getHtmlValue("<input type=hidden name=smauthreason value=\"(\\S+)\">", htmlPage);
	    String smagentname = getHtmlValue("<input type=hidden name=smagentname value=\"(\\S*)\">", htmlPage);

		URL url = new URL(parentUrl.getProtocol(), parentUrl.getHost(), parentUrl.getPort(), action);
		logger.debug("url=" + url);

		String urlParameters  = 
				"SMENC="+smenc+
				"&SMLOCALE="+smlocale+
				"&target="+target+
				"&smauthreason="+smauthreason+
				"&smagentname="+smagentname+
				"&user="+username+
				"&password="+secret;
		
		byte[] postData       = urlParameters.getBytes("UTF-8");
		int    postDataLength = postData.length;
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setInstanceFollowRedirects(false);

		try {
			conn.setRequestMethod("POST");
			
			if(log4jRequestIdMdcKey!=null&&log4jRequestIdMdcKey.trim().length()!=0&&requestIdHeader!=null&&requestIdHeader.trim().length()!=0) {
				String requestId = (String) ThreadContext.get(log4jRequestIdMdcKey);
				if(requestId!=null) {
					logger.debug("requestId=" + requestId);
					conn.setRequestProperty(requestIdHeader, requestId);
				}
			}
			
			if(cookies!=null) {
				logger.debug("cookies=" + toString(cookies));
				conn.setRequestProperty("Cookie", toString(cookies));
			}

			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
			conn.setUseCaches( false );
			
			StringReader sr = new StringReader(urlParameters);
			
			try (OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream())) {
				
				char[] c = new char[1024];
				int read = -1;
				while ((read = sr.read(c)) != -1) {
					os.write(c, 0, read);
				}
			}

			int responseCode = conn.getResponseCode();
			logger.info(responseCode + ":" + conn.getResponseMessage());
			
			if (responseCode == 200) {

				String responseString = getResponseString(conn);
				logger.debug(responseString);

				throw new IllegalStateException("Unexpected HTML response.");

			} else if (responseCode == 302) {
				
				String location = conn.getHeaderField("location");
				logger.debug("location=" + location);
				
				Map<String, List<String>> headerFields = conn.getHeaderFields();
				for(String key:headerFields.keySet()) {
					List<String> values = headerFields.get(key);
					for(String value:values) {
						if("Set-Cookie".equals(key)) {
							logger.debug(key+"="+value);
							String cookie = value.substring(0, value.indexOf(";"));
					        String cookieName = cookie.substring(0, cookie.indexOf("="));
					        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
					        cookies.add(new Cookie(cookieName, cookieValue));
						}
					}
				}

				if(location.indexOf("error.cgi")!=-1) {
					handleSiteminderErrorRedirect(url, location, cookies);
				} else {
					result = siteminderPostLogin(url, location, cookies);
				}
				
			} else {

				String responseString = getErrorString(conn);
				logger.debug(responseString);

				throw new Oauth2ClientException(responseString, responseCode);
			}
			
		} finally {
			conn.disconnect();
		}

		long duration = System.currentTimeMillis() - startTime;

		double seconds = (double) duration / (double) 1000;

		logger.info("processing time: " + seconds + " seconds");
		
		logger.debug(">siteminderLogin "+result);
		return result;
	}
	
	private Set<Cookie> siteminderPostLogin(URL parentUrl, String action, Set<Cookie> cookies) throws IOException, Oauth2ClientException {
		logger.debug("<siteminderPostLogin");
		
		Set<Cookie> result = null;
		
		long startTime = System.currentTimeMillis();
		
		URL url = new URL(parentUrl.getProtocol(), parentUrl.getHost(), parentUrl.getPort(), action);
		logger.debug("url=" + url);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setInstanceFollowRedirects(false);

		try {
			conn.setRequestMethod("GET");
			
			if(log4jRequestIdMdcKey!=null&&log4jRequestIdMdcKey.trim().length()!=0&&requestIdHeader!=null&&requestIdHeader.trim().length()!=0) {
				String requestId = (String) ThreadContext.get(log4jRequestIdMdcKey);
				if(requestId!=null) {
					logger.debug("requestId=" + requestId);
					conn.setRequestProperty(requestIdHeader, requestId);
				}
			}
			
			if(cookies!=null) {
				logger.debug("cookies=" + toString(cookies));
				conn.setRequestProperty("Cookie", toString(cookies));
			}

			conn.setDoInput(true);
			conn.setDoOutput(true);

			int responseCode = conn.getResponseCode();
			logger.info(responseCode + ":" + conn.getResponseMessage());
			
			if(responseCode == 200) {
				
				String responseString = getResponseString(conn);
				logger.debug(responseString);
				
				Map<String, List<String>> headerFields = conn.getHeaderFields();
				for(String key:headerFields.keySet()) {
					List<String> values = headerFields.get(key);
					for(String value:values) {
						if("Set-Cookie".equals(key)) {
							logger.debug(key+"="+value);
							String cookie = value.substring(0, value.indexOf(";"));
					        String cookieName = cookie.substring(0, cookie.indexOf("="));
					        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
					        cookies.add(new Cookie(cookieName, cookieValue));
						}
					}
				}
				
				// Successful Login
				result = cookies;
				
			} else if (responseCode == 302) {
				// Redirect indicates that we have our authorization code or we have hit the Siteminder Login page

				String responseString = getResponseString(conn);
				logger.debug(responseString);
				
				String location = conn.getHeaderField("location");
				logger.debug("location=" + location);
				
				Map<String, List<String>> headerFields = conn.getHeaderFields();
				for(String key:headerFields.keySet()) {
					List<String> values = headerFields.get(key);
					for(String value:values) {
						if("Set-Cookie".equals(key)) {
							logger.debug(key+"="+value);
							String cookie = value.substring(0, value.indexOf(";"));
					        String cookieName = cookie.substring(0, cookie.indexOf("="));
					        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
					        cookies.add(new Cookie(cookieName, cookieValue));
						}
					}
				}
				
				if(location.indexOf("error.cgi")!=-1) {
					handleSiteminderErrorRedirect(url, location, cookies);
				} else {
					throw new Oauth2ClientException("Siteminder Login Failed.", 401);
				}
				
			} else {

				String responseString = getErrorString(conn);
				logger.debug(responseString);

				throw new Oauth2ClientException(responseString, responseCode);
			}
			
		} finally {
			conn.disconnect();
		}

		long duration = System.currentTimeMillis() - startTime;

		double seconds = (double) duration / (double) 1000;

		logger.info("processing time: " + seconds + " seconds");
		
		logger.debug(">siteminderPostLogin "+result);
		return result;
	}

	private void handleSiteminderErrorRedirect(URL parentUrl, String location, Set<Cookie> cookies) throws IOException, Oauth2ClientException {
		logger.debug("<handleSiteminderErrorRedirect");

		int responseCode;
		String responseString = null;
		
			long startTime = System.currentTimeMillis();

			URL url = new URL(parentUrl.getProtocol(), parentUrl.getHost(), parentUrl.getPort(), location);
			logger.debug("url=" + url);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);

			try {
				conn.setRequestMethod("GET");
				
				if(log4jRequestIdMdcKey!=null&&log4jRequestIdMdcKey.trim().length()!=0&&requestIdHeader!=null&&requestIdHeader.trim().length()!=0) {
					String requestId = (String) ThreadContext.get(log4jRequestIdMdcKey);
					if(requestId!=null) {
						logger.debug("requestId=" + requestId);
						conn.setRequestProperty(requestIdHeader, requestId);
					}
				}

				conn.setDoInput(true);
				conn.setDoOutput(true);
				
				if(cookies!=null) {
					logger.debug("cookies=" + toString(cookies));
					conn.setRequestProperty("Cookie", toString(cookies));
				}

				responseCode = conn.getResponseCode();
				logger.info(responseCode + ":" + conn.getResponseMessage());
				
				if (responseCode < 500) {

					responseString = getResponseString(conn);
					logger.debug(responseString);
					
				} else {

					responseString = getErrorString(conn);
					logger.debug(responseString);
				}
				
			} finally {
				conn.disconnect();
			}

			long duration = System.currentTimeMillis() - startTime;

			double seconds = (double) duration / (double) 1000;

			logger.info("processing time: " + seconds + " seconds");

			throw new Oauth2ClientException(responseString, responseCode);
	}
	
    private static final String INTERNAL = "Internal";
	
	private Set<Cookie> siteminderPreLogin(URL parentUrl, String htmlPage, Set<Cookie> cookies, String username, String secret, String redirectUri) throws IOException, Oauth2ClientException {
		logger.debug("<siteminderPreLogin");

		Set<Cookie> result = null;
		
		long startTime = System.currentTimeMillis();
		
		String action = getHtmlValue("<form[^>]+action\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", htmlPage);
	    String instance = getHtmlValue("<input type=hidden name=\"instance\" value=\"(\\S+)\">", htmlPage);

		URL url = new URL(parentUrl.getProtocol(), parentUrl.getHost(), parentUrl.getPort(), action);
		logger.debug("url=" + url);

		String urlParameters  = "instance="+instance+"&user="+username+"&password="+secret;
		byte[] postData       = urlParameters.getBytes("UTF-8");
		int    postDataLength = postData.length;
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setInstanceFollowRedirects(false);

		try {
			conn.setRequestMethod("POST");
			
			if(log4jRequestIdMdcKey!=null&&log4jRequestIdMdcKey.trim().length()!=0&&requestIdHeader!=null&&requestIdHeader.trim().length()!=0) {
				String requestId = (String) ThreadContext.get(log4jRequestIdMdcKey);
				if(requestId!=null) {
					logger.debug("requestId=" + requestId);
					conn.setRequestProperty(requestIdHeader, requestId);
				}
			}
			
			if(cookies!=null) {
				logger.debug("cookies=" + toString(cookies));
				conn.setRequestProperty("Cookie", toString(cookies));
			}

			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
			conn.setUseCaches( false );
			
			StringReader sr = new StringReader(urlParameters);
			
			try (OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream())) {
				
				char[] c = new char[1024];
				int read = -1;
				while ((read = sr.read(c)) != -1) {
					os.write(c, 0, read);
				}
			}
			
			int responseCode = conn.getResponseCode();
			logger.info(responseCode + ":" + conn.getResponseMessage());
			
			if (responseCode == 200) {

				String responseString = getResponseString(conn);
					logger.debug(responseString);
					
					Map<String, List<String>> headerFields = conn.getHeaderFields();
					for(String key:headerFields.keySet()) {
						List<String> values = headerFields.get(key);
						for(String value:values) {
							if("Set-Cookie".equals(key)) {
								logger.debug(key+"="+value);
								String cookie = value.substring(0, value.indexOf(";"));
						        String cookieName = cookie.substring(0, cookie.indexOf("="));
						        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
						        cookies.add(new Cookie(cookieName, cookieValue));
							}
						}
					}
					
					if(responseString.indexOf("logon.fcc")!=-1) {
						
						result = siteminderLogin(url, responseString, cookies, username, secret, redirectUri);
						
					} else {
						throw new IllegalStateException("Unknown Siteminder response.");
					}
				
			} else if (responseCode == 302) {
				
				String location = conn.getHeaderField("location");
				logger.debug("location=" + location);
				
				Map<String, List<String>> headerFields = conn.getHeaderFields();
				for(String key:headerFields.keySet()) {
					List<String> values = headerFields.get(key);
					for(String value:values) {
						if("Set-Cookie".equals(key)) {
							logger.debug(key+"="+value);
							String cookie = value.substring(0, value.indexOf(";"));
					        String cookieName = cookie.substring(0, cookie.indexOf("="));
					        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
					        cookies.add(new Cookie(cookieName, cookieValue));
						}
					}
				}
				
				if(location.indexOf("error.cgi")!=-1) {
					handleSiteminderErrorRedirect(url, location, cookies);
				} else {
					throw new IllegalStateException("Unknown Siteminder response.");
				}
					
			} else {

				String responseString = getErrorString(conn);
				logger.debug(responseString);

				throw new Oauth2ClientException(responseString, responseCode);
			}
			
		} finally {
			conn.disconnect();
		}

		long duration = System.currentTimeMillis() - startTime;

		double seconds = (double) duration / (double) 1000;

		logger.info("processing time: " + seconds + " seconds");
		
		logger.debug(">siteminderPreLogin "+result);
		return result;
	}

	private static String getHtmlValue(String patternString, String text) {

		String result = null;
		
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(text);
	    
	    if(matcher.find()) {
	    	
	    	result = matcher.group(1);
	    	
	    } else {
	    	throw new IllegalStateException("Failed to find html value: "+patternString);
	    }
	    
		return result;
	}

	@Override
	public AccessToken getImplicitToken(String scope, String redirectUri, Long organizationId, String siteMinderUserType, String siteMinderUserIdentifier, String siteMinderAuthoritativePartyIdentifier, String username, String secret) throws Oauth2ClientException {
		return getImplicitToken(scope,
				redirectUri, organizationId, siteMinderUserType,
				siteMinderUserIdentifier, siteMinderAuthoritativePartyIdentifier,
				username, secret,
				null);
	}
	
	@Override
	public AccessToken getImplicitToken(String scope,
			String redirectUri, Long organizationId, String siteMinderUserType,
			String siteMinderUserIdentifier, String siteMinderAuthoritativePartyIdentifier,
			String username, String secret,
			String state) throws Oauth2ClientException {
		logger.debug("<getImplicitToken");

		AccessToken result = null;

		try {

			long startTime = System.currentTimeMillis();

			URL url = new URL(
					authorizeUrl
							+ '?'
							+ ((clientId == null) ? "" : "client_id="
									+ URLEncoder.encode(clientId, "UTF-8")
									+ '&')
							+ ((scope == null) ? "" : "scope="
									+ URLEncoder.encode(scope, "UTF-8") + '&')
							+ ((organizationId == null) ? ""
									: "user_oauth_organization="
											+ organizationId + '&')
							+ ((redirectUri == null) ? "" : "redirect_uri="
									+ URLEncoder.encode(redirectUri, "UTF-8")
									+ '&')
							+ ((state == null) ? "" : "state="
									+ URLEncoder.encode(state, "UTF-8") + '&')
							+ "response_type=token&disableDeveloperFilter=true");
			logger.debug("url=" + url);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);

			try {
				conn.setRequestMethod("GET");
				
				if(log4jRequestIdMdcKey!=null&&log4jRequestIdMdcKey.trim().length()!=0&&requestIdHeader!=null&&requestIdHeader.trim().length()!=0) {
					String requestId = (String) ThreadContext.get(log4jRequestIdMdcKey);
					if(requestId!=null) {
						logger.debug("requestId=" + requestId);
						conn.setRequestProperty(requestIdHeader, requestId);
					}
				}
				
				if (siteMinderUserType != null) {
					conn.setRequestProperty("SMGOV_USERTYPE",
							siteMinderUserType);
				}
				if (siteMinderUserIdentifier != null) {
					conn.setRequestProperty("SMGOV_USERIDENTIFIER",
							siteMinderUserIdentifier);
				}

				if (siteMinderAuthoritativePartyIdentifier != null) {
					conn.setRequestProperty("SMGOV_AUTHORITATIVEPARTYIDENTIFIER", siteMinderAuthoritativePartyIdentifier);
				}

				conn.setDoInput(true);
				conn.setDoOutput(true);

				int responseCode = conn.getResponseCode();
				logger.info(responseCode + ":" + conn.getResponseMessage());

				if (responseCode == 200) {
					// OK indicates that we are being prompted for approval or
					// organization selection

					String responseString = getResponseString(conn);
					logger.debug(responseString);

					throw new java.lang.IllegalStateException(
							"Unexpected HTML response.");

				} else if (responseCode == 302) {
					// Redirect indicates that we have our authorization code or we have hit the Siteminder Login page
					
					String location = conn.getHeaderField("location");
					logger.debug("location=" + location);

					Set<Cookie> cookies = new HashSet<Cookie>();
					
					Map<String, List<String>> headerFields = conn.getHeaderFields();
					for(String key:headerFields.keySet()) {
						List<String> values = headerFields.get(key);
						for(String value:values) {
							if("Set-Cookie".equals(key)) {
								logger.debug(key+"="+value);
								String cookie = value.substring(0, value.indexOf(";"));
						        String cookieName = cookie.substring(0, cookie.indexOf("="));
						        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
						        cookies.add(new Cookie(cookieName, cookieValue));
							}
						}
					}

					if(location.startsWith(redirectUri)) {
						// We are being redirected to our redirectUrl so we either have our code or we have an error
						
						result = handleImplicitOauthRedirect(responseCode, location);
						
					} else {
						// We will assume that we are being redirected by Siteminder
						
						cookies = handleSiteminderRedirect(location, cookies, siteMinderUserType, username, secret, redirectUri);
						
						result = authorizeImplicit(url, cookies, redirectUri);
					}

				} else if (responseCode > 399) {

					String responseString = getErrorString(conn);
					logger.debug(responseString);

					throw new Oauth2ClientException(responseString,
							responseCode);
				}

			} finally {
				conn.disconnect();
			}

			long duration = System.currentTimeMillis() - startTime;

			double seconds = (double) duration / (double) 1000;

			logger.info("processing time: " + seconds + " seconds");

		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		logger.debug(">getImplicitToken " + result);
		return result;
	}

	/**
	 * Parses an URL query string and returns a map with the parameter values.
	 * The URL query string is the part in the URL after the first '?' character
	 * up to an optional '#' character. It has the format
	 * "name=value&name=value&...". The map has the same structure as the one
	 * returned by javax.servlet.ServletRequest.getParameterMap(). A parameter
	 * name may occur multiple times within the query string. For each parameter
	 * name, the map contains a string array with the parameter values.
	 * 
	 * @param s an URL query string.
	 * @return a map containing parameter names as keys and parameter values as
	 *         map values.
	 * @author Christian d'Heureuse, Inventec Informatik AG, Switzerland,
	 *         www.source-code.biz.
	 */
	private static Map<String, String[]> parseUrlQueryString(String s) {
		if (s == null)
			return new HashMap<String, String[]>(0);
		// In map1 we use strings and ArrayLists to collect the parameter
		// values.
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		int p = 0;
		while (p < s.length()) {
			int p0 = p;
			while (p < s.length() && s.charAt(p) != '=' && s.charAt(p) != '&')
				p++;
			String name = urlDecode(s.substring(p0, p));
			if (p < s.length() && s.charAt(p) == '=')
				p++;
			p0 = p;
			while (p < s.length() && s.charAt(p) != '&')
				p++;
			String value = urlDecode(s.substring(p0, p));
			if (p < s.length() && s.charAt(p) == '&')
				p++;
			Object x = map1.get(name);
			if (x == null) {
				// The first value of each name is added directly as a string to
				// the map.
				map1.put(name, value);
			} else if (x instanceof String) {
				// For multiple values, we use an ArrayList.
				ArrayList<String> a = new ArrayList<String>();
				a.add((String) x);
				a.add(value);
				map1.put(name, a);
			} else {
				@SuppressWarnings("unchecked")
				ArrayList<String> a = (ArrayList<String>) x;
				a.add(value);
			}
		}
		// Copy map1 to map2. Map2 uses string arrays to store the parameter
		// values.
		HashMap<String, String[]> map2 = new HashMap<String, String[]>(
				map1.size());
		for (Map.Entry<String, Object> e : map1.entrySet()) {
			String name = e.getKey();
			Object x = e.getValue();
			String[] v;
			if (x instanceof String) {
				v = new String[] { (String) x };
			} else {
				@SuppressWarnings("unchecked")
				ArrayList<String> a = (ArrayList<String>) x;
				v = new String[a.size()];
				v = a.toArray(v);
			}
			map2.put(name, v);
		}
		return map2;
	}

	private static String urlDecode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Error in urlDecode.", e);
		}
	}

	static class ImplicitResponse {
		public final String accessToken;
		public final String tokenType;
		public final String expiresIn;
		public final String scope;
		public final String state;
		public final String error;
		public final String errorDescription;
		public final String errorUri;

		public ImplicitResponse(Map<String, String[]> queryParameters) {
			String errors[] = queryParameters.get("error");
			if (errors != null && errors.length == 1) {
				error = errors[0];

				String errorDescriptions[] = queryParameters
						.get("error_description");
				errorDescription = (errorDescriptions != null
						&& errorDescriptions.length > 0 ? errorDescriptions[0]
						: null);

				String errorUris[] = queryParameters.get("error_uri");
				errorUri = (errorUris != null && errorUris.length > 0 ? errorUris[0]
						: null);

				accessToken = tokenType = expiresIn = scope = null;
			} else {
				String[] access_tokens = queryParameters.get("access_token");
				accessToken = (access_tokens != null && access_tokens.length == 1) ? access_tokens[0]
						: null;

				String[] token_types = queryParameters.get("token_type");
				tokenType = (token_types != null && token_types.length == 1) ? token_types[0]
						: null;

				String[] expires_in_values = queryParameters.get("expires_in");
				expiresIn = (expires_in_values != null && expires_in_values.length == 1) ? expires_in_values[0]
						: null;

				String[] scopes = queryParameters.get("scope");
				scope = (scopes != null && scopes.length == 1) ? scopes[0]
						: null;

				errorDescription = error = errorUri = null;
			}

			String[] states = queryParameters.get("state");
			state = (states != null && states.length == 1) ? states[0] : null;
		}
	}

	static class AuthorizationCodeResponse {
		public final String code;
		public final String state;
		public final String error;
		public final String errorDescription;
		public final String errorUri;

		public AuthorizationCodeResponse(Map<String, String[]> queryParameters) {
			String[] codes = queryParameters.get("code");
			code = (codes != null && codes.length == 1) ? codes[0] : null;

			String errors[] = queryParameters.get("error");
			if (errors != null && errors.length == 1) {
				String errorDescriptions[] = queryParameters
						.get("error_description");
				errorDescription = (errorDescriptions != null
						&& errorDescriptions.length > 0 ? errorDescriptions[0]
						: null);

				String errorUris[] = queryParameters.get("error_uri");
				errorUri = (errorUris != null && errorUris.length > 0 ? errorUris[0]
						: null);

				error = errors[0];
			} else {
				errorDescription = null;
				error = null;
				errorUri = null;
			}

			String[] states = queryParameters.get("state");
			state = (states != null && states.length == 1) ? states[0] : null;
		}
	}

	public void setRequestIdHeader(String requestIdHeader) {
		this.requestIdHeader = requestIdHeader;
	}

	public void setLog4jRequestIdMdcKey(String log4jRequestIdMdcKey) {
		this.log4jRequestIdMdcKey = log4jRequestIdMdcKey;
	}
}
