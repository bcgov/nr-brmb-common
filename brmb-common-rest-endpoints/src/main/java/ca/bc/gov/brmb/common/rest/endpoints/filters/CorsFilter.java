package ca.bc.gov.brmb.common.rest.endpoints.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.brmb.common.rest.resource.HeaderConstants;

public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
	public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";

	private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

	protected boolean allowCredentials = true;
	protected String allowedMethods = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
	protected String allowedHeaders = HeaderConstants.ORIGIN_HEADER + ", "
			+ HeaderConstants.CONTENT_TYPE_HEADER + ", "
			+ HeaderConstants.ACCEPT_HEADER + ", "
			+ HeaderConstants.AUTHORIZATION_HEADER + ", "
			+ HeaderConstants.IF_MATCH_HEADER + ", "
			+ HeaderConstants.VERSION_HEADER + ", "
			+ HeaderConstants.REQUEST_ID_HEADER;
	protected String exposedHeaders = HeaderConstants.ETAG_HEADER + ", "
			+ HeaderConstants.VERSION_HEADER + ", "
			+ HeaderConstants.REQUEST_ID_HEADER;
	protected int corsMaxAge = -1;
	protected Set<String> allowedOrigins = new HashSet<String>();
	
	public CorsFilter() {
		logger.debug("<CorsFilter");
		
		this.allowedOrigins.add("*");
		
		logger.debug(">CorsFilter");
	}

	/**
	 * Put "*" if you want to accept all origins
	 *
	 * @return
	 */
	public Set<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(Set<String> allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	/**
	 * Defaults to true
	 *
	 * @return
	 */
	public boolean isAllowCredentials() {
		return allowCredentials;
	}

	public void setAllowCredentials(boolean allowCredentials) {
		this.allowCredentials = allowCredentials;
	}

	/**
	 * Will allow all by default
	 *
	 * @return
	 */
	public String getAllowedMethods() {
		return allowedMethods;
	}

	/**
	 * Will allow all by default comma delimited string for
	 * Access-Control-Allow-Methods
	 *
	 * @param allowedMethods
	 */
	public void setAllowedMethods(String allowedMethods) {
		this.allowedMethods = allowedMethods;
	}

	public String getAllowedHeaders() {
		return allowedHeaders;
	}

	/**
	 * Will allow all by default comma delimited string for
	 * Access-Control-Allow-Headers
	 *
	 * @param allowedHeaders
	 */
	public void setAllowedHeaders(String allowedHeaders) {
		this.allowedHeaders = allowedHeaders;
	}

	public int getCorsMaxAge() {
		return corsMaxAge;
	}

	public void setCorsMaxAge(int corsMaxAge) {
		this.corsMaxAge = corsMaxAge;
	}

	public String getExposedHeaders() {
		return exposedHeaders;
	}

	/**
	 * comma delimited list
	 *
	 * @param exposedHeaders
	 */
	public void setExposedHeaders(String exposedHeaders) {
		this.exposedHeaders = exposedHeaders;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		logger.debug("<filter(request)");
		
		String origin = requestContext.getHeaderString(HeaderConstants.ORIGIN_HEADER);
		if (origin == null) {
			// do nothing
		} else {
			if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
				preflight(origin, requestContext);
			} else {
				checkOrigin(requestContext, origin);
			}
		}
		logger.debug(">filter(request)");
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		logger.debug("<filter(request, response)");
		
		String origin = requestContext.getHeaderString(HeaderConstants.ORIGIN_HEADER);
		if (origin == null || requestContext.getMethod().equalsIgnoreCase("OPTIONS")
				|| requestContext.getProperty("cors.failure") != null) {
			// don't do anything if origin is null, its an OPTIONS request, or
			// cors.failure is set
		} else {
			responseContext.getHeaders().putSingle(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
			if (allowCredentials)
				responseContext.getHeaders().putSingle(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
	
			if (exposedHeaders != null) {
				responseContext.getHeaders().putSingle(ACCESS_CONTROL_EXPOSE_HEADERS, exposedHeaders);
			}
		}
		
		logger.debug(">filter(request, response)");
	}
	
	protected void preflight(String origin, ContainerRequestContext requestContext) {
		checkOrigin(requestContext, origin);

		Response.ResponseBuilder builder = Response.ok();
		builder.header(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
		if (allowCredentials)
			builder.header(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		String requestMethods = requestContext.getHeaderString(ACCESS_CONTROL_REQUEST_METHOD);
		if (requestMethods != null) {
			if (allowedMethods != null) {
				requestMethods = this.allowedMethods;
			}
			builder.header(ACCESS_CONTROL_ALLOW_METHODS, requestMethods);
		}
		String allowHeaders = requestContext.getHeaderString(ACCESS_CONTROL_REQUEST_HEADERS);
		if (allowHeaders != null) {
			if (allowedHeaders != null) {
				allowHeaders = this.allowedHeaders;
			}
			builder.header(ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
		}
		if (corsMaxAge > -1) {
			builder.header(ACCESS_CONTROL_MAX_AGE, Integer.valueOf(corsMaxAge));
		}
		requestContext.abortWith(builder.build());

	}

	protected void checkOrigin(ContainerRequestContext requestContext, String origin) {
		logger.debug("<checkOrigin");
		
		if (!allowedOrigins.contains("*") && !allowedOrigins.contains(origin)) {
			requestContext.setProperty("cors.failure", Boolean.TRUE);
			throw new ForbiddenException("Origin not allowed: " + origin);
		}

		logger.debug(">checkOrigin");
	}
}
