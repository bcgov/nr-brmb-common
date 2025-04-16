package ca.bc.gov.brmb.common.webade.oauth2.token.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import ca.bc.gov.brmb.common.webade.oauth2.token.client.Oauth2ClientException;
import ca.bc.gov.brmb.common.webade.oauth2.token.client.TokenService;
import ca.bc.gov.brmb.common.webade.oauth2.token.client.resource.AccessToken;
import ca.bc.gov.brmb.common.webade.oauth2.token.client.resource.CheckedToken;
import ca.bc.gov.brmb.common.webade.oauth2.token.client.resource.transformers.JsonTransformer;
import ca.bc.gov.brmb.common.webade.oauth2.token.client.resource.transformers.Transformer;
import ca.bc.gov.brmb.common.webade.oauth2.token.client.resource.transformers.TransformerException;

public class TokenServiceImpl implements TokenService {

	private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

	public static final String REQUEST_ID_HEADER_PARAMETER = "org.apache.logging.log4j.request.id.header";
	public static final String DEFAULT_REQUEST_ID_HEADER = "requestId";

	public static final String LOG4J_REQUEST_ID_MDC_KEY_PARAMETER = "org.apache.logging.log4j.request.id.mdc.key";
	public static final String DEFAULT_REQUEST_IDLOG4J_MDC_KEY = "requestId";

	private String requestIdHeader;
	private String log4jRequestIdMdcKey;
	
	private boolean doSendCredentialsInBody;
	private String defaultClientId;
	private String defaultClientSecret;
	private String checkTokenUrl;
	private String tokenUrl;
	private Transformer transformer;
	private Cache<CheckedToken> checkedTokenCache;

	public TokenServiceImpl(String defaultClientId, String defaultClientSecret,
			String checkTokenUrl, String tokenUrl) {
		logger.debug("<TokenServiceImpl");

		this.requestIdHeader = DEFAULT_REQUEST_ID_HEADER;
		this.log4jRequestIdMdcKey = DEFAULT_REQUEST_IDLOG4J_MDC_KEY;
		
		this.defaultClientId = defaultClientId;
		this.defaultClientSecret = defaultClientSecret;
		this.checkTokenUrl = checkTokenUrl;
		this.tokenUrl = tokenUrl;

		this.transformer = new JsonTransformer();
		
		this.checkedTokenCache = new CheckedTokenCache();

		logger.debug(">TokenServiceImpl");
	}

	@Override
	public CheckedToken checkToken(String accessToken)
			throws Oauth2ClientException {
		logger.debug("<checkToken");

		CheckedToken result = null;
		
		if(accessToken==null||accessToken.trim().length()==0) {
			throw new IllegalArgumentException("accessToken cannot be null or blank");
		}

		long startTime = System.currentTimeMillis();
		
		CheckedToken cachedResult = this.checkedTokenCache.get(accessToken);
		logger.debug("cachedResult="+cachedResult);

		if(cachedResult!=null) {
			logger.debug("returning cached CheckedToken: "+accessToken);
			result = cachedResult;
		} else {

			try {
				
				DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
				UriBuilder uriBuilder = uriBuilderFactory.uriString(checkTokenUrl);
				
				uriBuilder.replaceQueryParam("token", accessToken);
				
				logger.info("CheckToken call: "+uriBuilder.build().toString());
				
				URL url = uriBuilder.build().toURL();
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
	
					String basicAuth = "Basic "
							+ new String(
									Base64.getEncoder()
											.encode((defaultClientId + ":" + defaultClientSecret)
													.getBytes()));
					conn.setRequestProperty("Authorization", basicAuth);
	
					conn.setDoInput(true);
					conn.setDoOutput(true);
	
					int responseCode = conn.getResponseCode();
					logger.info("CheckToken response: "+ responseCode + ":" + conn.getResponseMessage());
	
					if (responseCode == 200) {
	

						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try(InputStream is = conn.getInputStream()) {
	
							byte[] b = new byte[1024];
							int read = -1;
							while ((read = is.read(b)) != -1) {
								baos.write(b, 0, read);
							}
						}
	
						String responseString = baos.toString();
	
						logger.debug("responseString=\n" + responseString);
	
						result = (CheckedToken) this.transformer.unmarshall(
								responseString, CheckedToken.class);
						
						
	
					} else if (responseCode > 399) {
	
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try (InputStream is = conn.getErrorStream()) {
							
							if(is!=null) {
	
								byte[] b = new byte[1024];
								int read = -1;
								while ((read = is.read(b)) != -1) {
									baos.write(b, 0, read);
								}
							}
						}

						String responseString = baos.toString();
						logger.debug(responseString);
	
						throw new Oauth2ClientException(responseString,
								responseCode);
					}
	
				} finally {
					conn.disconnect();
				}
	
			} catch (TransformerException e) {
				throw new RuntimeException(e);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			if(result !=null) {
				this.checkedTokenCache.put(accessToken, result);
			}
		}
		
		long duration = System.currentTimeMillis() - startTime;

		double seconds = (double) duration / (double) 1000;

		logger.info("CheckToken completed: " + seconds + " seconds");

		logger.debug(">checkToken " + result);
		return result;
	}

	@Override
	public AccessToken getToken(String scope) throws Oauth2ClientException {
		return this.getToken(defaultClientId, defaultClientSecret, scope);
	}

	@Override
	public AccessToken getToken(
			String clientId,
			String clientSecret,
			String scope) throws Oauth2ClientException {
		return getToken(clientId, clientSecret, scope, null, null);
	}

	@Override
	public AccessToken getToken(
			String clientId,
			String clientSecret,
			String scope, 
			String authorizationCode,
			String redirectUri) throws Oauth2ClientException {
		String grantType = authorizationCode == null ? "client_credentials"
				: "authorization_code";
		return getToken(clientId, clientSecret, scope, grantType, authorizationCode, redirectUri);
	}

	@Override
	public AccessToken getToken(
			String clientId,
			String clientSecret,
			String scope, 
			String grantType,
			String authorizationCode, 
			String redirectUri) throws Oauth2ClientException {
		logger.debug("<getToken " + scope);

		AccessToken result = null;
		long startTime = System.currentTimeMillis();

		try {
			
			DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
			UriBuilder uriBuilder = uriBuilderFactory.uriString(tokenUrl);
			
			if(grantType==null) {
				uriBuilder.replaceQueryParam("grant_type");
			} else {
				uriBuilder.replaceQueryParam("grant_type", URLEncoder.encode(grantType, "UTF-8"));
			}
			if(authorizationCode==null) {
				uriBuilder.replaceQueryParam("code");
			} else {
				uriBuilder.replaceQueryParam("code", URLEncoder.encode(authorizationCode, "UTF-8"));
			}
			if(scope==null) {
				uriBuilder.replaceQueryParam("scope");
			} else {
				uriBuilder.replaceQueryParam("scope", URLEncoder.encode(scope, "UTF-8"));
			}
			if(redirectUri==null) {
				uriBuilder.replaceQueryParam("redirect_uri");
			} else {
				uriBuilder.replaceQueryParam("redirect_uri", URLEncoder.encode(redirectUri, "UTF-8"));
			}
			
			URL url = uriBuilder.build().toURL();
			logger.debug("url=" + url);
			
			logger.info("GetToken call: "+url.toString());

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			try {
				conn.setInstanceFollowRedirects(false);

				String basicAuth = "Basic "
						+ new String(
								Base64.getEncoder()
										.encode((clientId + ":" + clientSecret)
												.getBytes()));
				conn.setRequestProperty("Authorization", basicAuth);
				conn.setDoOutput(true);

				if (doSendCredentialsInBody) {
					conn.setRequestMethod("POST");

					try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
						wr.writeBytes("client_id=" + clientId + "&client_secret="
								+ clientSecret);
						wr.flush();
					}
					
				} else {
					conn.setRequestMethod("GET");
				}
				
				if(log4jRequestIdMdcKey!=null&&log4jRequestIdMdcKey.trim().length()!=0&&requestIdHeader!=null&&requestIdHeader.trim().length()!=0) {
					String requestId = (String) ThreadContext.get(log4jRequestIdMdcKey);
					if(requestId!=null) {
						logger.debug("requestId=" + requestId);
						conn.setRequestProperty(requestIdHeader, requestId);
					}
				}

				int responseCode = conn.getResponseCode();
				logger.info("GetToken response: "+ responseCode + ": " + conn.getResponseMessage());
				if (responseCode == 200) {

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try(InputStream is = conn.getInputStream()) {

						byte[] b = new byte[1024];
						int read = -1;
						while ((read = is.read(b)) != -1) {
							baos.write(b, 0, read);
						}
					}

					String responseString = baos.toString();

					logger.debug("responseString=\n" + responseString);
					result = (AccessToken) this.transformer.unmarshall(
							responseString, AccessToken.class);

				} else if (responseCode >= 400) {

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

					throw new Oauth2ClientException(baos.toString(),
							responseCode);
				} else {
					throw new Oauth2ClientException("Unexpected response",
							responseCode);
				}
			} finally {
				conn.disconnect();
			}
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		long duration = System.currentTimeMillis() - startTime;
		double seconds = (double) duration / (double) 1000;


		logger.info("GetToken completed: " + seconds + " seconds");

		logger.debug(">getToken " + result);
		return result;
	}

	@Override
	public AccessToken refreshToken(String refreshToken)
			throws Oauth2ClientException {
		return refreshToken(refreshToken, null /* get same scope as before */);
	}

	@Override
	public AccessToken refreshToken(String refreshToken, String scope)
			throws Oauth2ClientException {
		logger.debug("<refreshToken");

		AccessToken result = null;

		try {

			long startTime = System.currentTimeMillis();
			
			DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
			UriBuilder uriBuilder = uriBuilderFactory.uriString(tokenUrl);
			
			uriBuilder.replaceQueryParam("grant_type", "refresh_token");
			if(refreshToken==null) {
				uriBuilder.replaceQueryParam("refresh_token");
			} else {
				uriBuilder.replaceQueryParam("refresh_token", URLEncoder.encode(refreshToken, "UTF-8"));
			}
			if(scope==null) {
				uriBuilder.replaceQueryParam("scope");
			} else {
				uriBuilder.replaceQueryParam("scope", URLEncoder.encode(scope, "UTF-8"));
			}
			
			URL url = uriBuilder.build().toURL();
			logger.debug("url=" + url);
			
			logger.info("RefreshToken call: "+url.toString());

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

				String basicAuth = "Basic "
						+ new String(
								Base64.getEncoder()
										.encode((defaultClientId + ":" + defaultClientSecret)
												.getBytes()));
				conn.setRequestProperty("Authorization", basicAuth);

				conn.setDoInput(true);
				conn.setDoOutput(true);

				int responseCode = conn.getResponseCode();
				logger.info("RefreshToken response: "+responseCode + ":" + conn.getResponseMessage());

				if (responseCode == 200) {

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try(InputStream is = conn.getInputStream()) {

						byte[] b = new byte[1024];
						int read = -1;
						while ((read = is.read(b)) != -1) {
							baos.write(b, 0, read);
						}
					}

					String responseString = baos.toString();

					logger.debug("responseString=\n" + responseString);

					result = (AccessToken) this.transformer.unmarshall(
							responseString, AccessToken.class);

				} else if (responseCode > 399) {

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

					String responseString = baos.toString();
					logger.debug(responseString);

					throw new Oauth2ClientException(responseString,
							responseCode);
				}

			} finally {
				conn.disconnect();
			}

			long duration = System.currentTimeMillis() - startTime;

			double seconds = (double) duration / (double) 1000;

			logger.info("RefreshToken completed: "+ seconds + " seconds");

		} catch (TransformerException e) {
			throw new RuntimeException(e);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		logger.debug(">refreshToken " + result);
		return result;
	}

	public boolean isDoSendCredentialsInBody() {
		return doSendCredentialsInBody;
	}

	public void setDoSendCredentialsInBody(boolean doSendCredentialsInBody) {
		this.doSendCredentialsInBody = doSendCredentialsInBody;
	}

	public void setCheckedTokenCache(Cache<CheckedToken> checkedTokenCache) {
		this.checkedTokenCache = checkedTokenCache;
	}

	public void setRequestIdHeader(String requestIdHeader) {
		this.requestIdHeader = requestIdHeader;
	}

	public void setLog4jRequestIdMdcKey(String log4jRequestIdMdcKey) {
		this.log4jRequestIdMdcKey = log4jRequestIdMdcKey;
	}

	@Override
	public void setDefaultClientSecret(String defaultClientSecret) {
		this.defaultClientSecret = defaultClientSecret;
	}
}
