package ca.bc.gov.brmb.common.rest.endpoints.filters;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ca.bc.gov.brmb.common.rest.resource.HeaderConstants;
import ca.bc.gov.brmb.common.rest.endpoints.proxy.RequestProxy;

public class VersionForwardingFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(VersionForwardingFilter.class);

	public static final String RESPONSE_VERSION_PARAM = "response_version";
	public static final String DEFAULT_REQUEST_VERSION_PARAM = "default_request_version";
	public static final String SUCCESSOR_URI_PROPERTY_PARAM = "successor_uri_property";
	public static final String PREDECESSOR_URI_PROPERTY_PARAM = "predecessor_uri_property";

	private int restVersion;
	private int defaultRequestVersion;
	private String successorUri;
	private String predecessorUri;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.debug("<init");
		
		String restVersionString = filterConfig.getInitParameter(RESPONSE_VERSION_PARAM);
		if(restVersionString==null||restVersionString.trim().length()==0) {
			throw new ServletException("Filter '"+filterConfig.getFilterName()+"' must define the '"+RESPONSE_VERSION_PARAM+"' init-param.");
		}
		try {
			this.restVersion = Integer.parseInt(restVersionString);
		} catch(NumberFormatException e) {
			throw new ServletException("Filter '"+filterConfig.getFilterName()+"' init-param '"+RESPONSE_VERSION_PARAM+"' must be an integer value: "+restVersionString);
		}
		
		String defaultRequestVersionString = filterConfig.getInitParameter(DEFAULT_REQUEST_VERSION_PARAM);
		if(defaultRequestVersionString==null||defaultRequestVersionString.trim().length()==0) {
			// do nothing
		} else {
			try {
				this.defaultRequestVersion = Integer.parseInt(defaultRequestVersionString);
			} catch(NumberFormatException e) {
				throw new ServletException("Filter '"+filterConfig.getFilterName()+"' init-param '"+DEFAULT_REQUEST_VERSION_PARAM+"' must be an integer value: "+defaultRequestVersionString);
			}
		}		
		
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		logger.debug("applicationContext="+applicationContext);
		
		Properties applicationProperties = applicationContext.getBean("applicationProperties", Properties.class);
		logger.debug("applicationProperties="+applicationProperties);

		String successorUriProperty = filterConfig.getInitParameter(SUCCESSOR_URI_PROPERTY_PARAM);
		logger.debug("successorUriProperty="+successorUriProperty);
		
		if(successorUriProperty!=null) {
			
			successorUri = applicationProperties.getProperty(successorUriProperty);
			logger.debug("successorUri="+successorUri);
			if((successorUriProperty.trim().length()>0)&&(successorUri==null||successorUri.trim().length()==0)) {
				throw new ServletException("Filter '"+filterConfig.getFilterName()+"' init-param '"+SUCCESSOR_URI_PROPERTY_PARAM+"' value '"+successorUri+"' does not corrispond to an application property.");
			}
		}

		String predecessorUriProperty = filterConfig.getInitParameter(PREDECESSOR_URI_PROPERTY_PARAM);
		logger.debug("predecessorUriProperty="+predecessorUriProperty);
		
		if(predecessorUriProperty!=null) {
			
			predecessorUri = applicationProperties.getProperty(predecessorUriProperty);
			logger.debug("predecessorUri="+predecessorUri);
			if((predecessorUriProperty.trim().length()>0)&&(predecessorUri==null||predecessorUri.trim().length()==0)) {
				throw new ServletException("Filter '"+filterConfig.getFilterName()+"' init-param '"+PREDECESSOR_URI_PROPERTY_PARAM+"' value '"+predecessorUri+"' does not corrispond to an application property.");
			}
		}
		
		logger.debug(">init");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		logger.info("Begin");
		
		try {

			HttpServletRequest httpServletRequest = null;
			HttpServletResponse httpServletResponse = null;
			if(request instanceof HttpServletRequest) {
				httpServletRequest = (HttpServletRequest) request;
				httpServletResponse = (HttpServletResponse) response;
				
				logger.debug("Request Headers");
				logger.debug("---------------------------------------------------------");
				for(Enumeration<String> headerNames = httpServletRequest.getHeaderNames();headerNames.hasMoreElements();) {
					String headerName = headerNames.nextElement();
					for(Enumeration<String> headerValues = httpServletRequest.getHeaders(headerName);headerValues.hasMoreElements();){
						String headerValue = headerValues.nextElement();
						logger.debug(headerName+":"+headerValue);
					}
				}
				logger.debug("---------------------------------------------------------");
				
				String requestVersionString = httpServletRequest.getHeader(HeaderConstants.VERSION_HEADER);
				logger.debug("requestVersion="+requestVersionString);
				
				int requestVersion;
				if(requestVersionString!=null&&requestVersionString.trim().length()>0) {
					try {
						requestVersion = Integer.parseInt(requestVersionString);
						logger.info("The "+HeaderConstants.VERSION_HEADER+" request header was found: "+requestVersion);
					} catch(NumberFormatException e) {
						logger.warn("The "+HeaderConstants.VERSION_HEADER+" request header value '"+requestVersionString+"' could not be parsed so falling back to default: "+this.defaultRequestVersion);
						requestVersion = this.defaultRequestVersion;
					}
				} else {
					logger.info("The "+HeaderConstants.VERSION_HEADER+" request header was not found so falling back to default: "+this.defaultRequestVersion);
					requestVersion = this.defaultRequestVersion;
				}
				
				if(requestVersion>this.restVersion) {
					
					logger.warn("The requested version '"+requestVersion+"' is greater than what is supported by this api '"+this.restVersion+"' so the request should be forwarded to the successor api.");
				
					if(this.successorUri!=null&&this.successorUri.trim().length()>0) {
						RequestProxy proxy = new RequestProxy(this.successorUri);
						proxy.proxy(httpServletRequest, httpServletResponse);
					} else {
						
						httpServletResponse.setHeader(HeaderConstants.VERSION_HEADER, Integer.toString(this.restVersion));
						httpServletResponse.setStatus(406);
					}
					
				} else if(requestVersion<this.restVersion) {
					
					logger.warn("The requested version '"+requestVersion+"' is less than what is supported by this api '"+this.restVersion+"' so the request should be forwarded to the predecessor api.");
				
					if(this.predecessorUri!=null&&this.predecessorUri.trim().length()>0) {
						RequestProxy proxy = new RequestProxy(this.predecessorUri);
						proxy.proxy(httpServletRequest, httpServletResponse);
					} else {
					
						httpServletResponse.setHeader(HeaderConstants.VERSION_HEADER, Integer.toString(this.restVersion));
						httpServletResponse.setStatus(406);
					}
					
				} else {

					logger.info("The request versions matches the version supported by this api: "+this.restVersion);
					
					httpServletResponse.setHeader(HeaderConstants.VERSION_HEADER, Integer.toString(this.restVersion));
					
					chain.doFilter(request, response);
				}
				
			} else {
				
				chain.doFilter(request, response);
			}
			
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			if(t instanceof RuntimeException) {
				throw (RuntimeException)t;
			} else if(t instanceof IOException) {
				throw (IOException)t;
			} else if(t instanceof ServletException) {
				throw (ServletException)t;
			} else {
				throw new ServletException(t);
			}
		} finally {
			logger.info("End");
		}
	}

	@Override
	public void destroy() {
		logger.debug("<destroy");
		logger.debug(">destroy");
	}

}
