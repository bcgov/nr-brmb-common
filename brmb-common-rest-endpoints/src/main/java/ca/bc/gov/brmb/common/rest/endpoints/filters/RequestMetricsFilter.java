package ca.bc.gov.brmb.common.rest.endpoints.filters;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;

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
import org.slf4j.MDC;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class RequestMetricsFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(RequestMetricsFilter.class);

	public static final String ID_SOURCE_PARAM = "id_source";
	
	public static final String REQUEST_ID_HEADER_PARAMETER = "org.apache.logging.log4j.request.id.header";
	public static final String DEFAULT_REQUEST_ID_HEADER = "requestId";

	public static final String LOG4J_REQUEST_ID_MDC_KEY_PARAMETER = "org.apache.logging.log4j.request.id.mdc.key";
	public static final String DEFAULT_REQUEST_IDLOG4J_MDC_KEY = "requestId";

	private String idSource;
	private String requestIdHeader;
	private String log4jRequestIdMdcKey;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.debug("<init");
		
		this.idSource = filterConfig.getInitParameter(ID_SOURCE_PARAM);
		if(this.idSource==null||this.idSource.trim().length()==0) {
			throw new ServletException("Filter '"+filterConfig.getFilterName()+"' must define the '"+ID_SOURCE_PARAM+"' init-param.  This value will be used to prefix generated request ids and allow the source of the id to be easily identified.");
		}
		
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		logger.debug("applicationContext="+applicationContext);
		
		Properties applicationProperties = applicationContext.getBean("applicationProperties", Properties.class);
		logger.debug("applicationProperties="+applicationProperties);
		
		requestIdHeader = applicationProperties.getProperty(REQUEST_ID_HEADER_PARAMETER);
		if(requestIdHeader==null||requestIdHeader.trim().length()==0) {
			requestIdHeader = filterConfig.getInitParameter(REQUEST_ID_HEADER_PARAMETER);
		}
		if(requestIdHeader==null||requestIdHeader.trim().length()==0) {
			requestIdHeader = DEFAULT_REQUEST_ID_HEADER;
		}
		logger.info("Setting Request ID Header to '"+requestIdHeader+"'");
		
		log4jRequestIdMdcKey = applicationProperties.getProperty(LOG4J_REQUEST_ID_MDC_KEY_PARAMETER);
		if(log4jRequestIdMdcKey==null||log4jRequestIdMdcKey.trim().length()==0) {
			log4jRequestIdMdcKey = filterConfig.getInitParameter(LOG4J_REQUEST_ID_MDC_KEY_PARAMETER);
		}
		if(log4jRequestIdMdcKey==null||log4jRequestIdMdcKey.trim().length()==0) {
			log4jRequestIdMdcKey = DEFAULT_REQUEST_IDLOG4J_MDC_KEY;
		}
		logger.info("Setting Log4J Request ID MDC key to '"+log4jRequestIdMdcKey+"'");
		
		logger.debug(">init");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		long startTime = System.currentTimeMillis();
		
		MDC.remove(log4jRequestIdMdcKey);
		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String requestId = servletRequest.getHeader(requestIdHeader);
		if(requestId==null||requestId.trim().length()==0) {
			requestId = idSource+UUID.randomUUID().toString().substring(24).toUpperCase();
		}
		
		MDC.put(log4jRequestIdMdcKey, requestId);
		
		String out = "\nMethod: "+servletRequest.getMethod()+"\nContext: "+servletRequest.getContextPath()+servletRequest.getServletPath()+(servletRequest.getPathInfo()==null?"":servletRequest.getPathInfo())+"\nParameters:";
		
		for(Enumeration<String> iter = servletRequest.getParameterNames();iter.hasMoreElements();) {
			String parametername = iter.nextElement();
			out += "\n\t"+parametername+":";
			String[] parametervalues = servletRequest.getParameterValues(parametername);
			for(int i=0;i<parametervalues.length;++i) {
				String parameterValue = parametervalues[i];
				out += "'"+parameterValue+"'";
				if(i<parametervalues.length) {
					out += ",";
				}
			}
		}
			
		if(logger.isDebugEnabled() && ("POST".equals(servletRequest.getMethod())||"PUT".equals(servletRequest.getMethod()))) {
			
			String contentType = servletRequest.getContentType();
			out += "\nContent Type: "+contentType;
			String characterEncoding = servletRequest.getCharacterEncoding();
			out += "\nCharacter Encoding: "+characterEncoding;
			
			if(contentType!=null && (contentType.contains("xml")||contentType.contains("json"))) {
			
				servletRequest = new BufferedRequestWrapper(servletRequest);
				
				out += "\nBody:\n"+((BufferedRequestWrapper)servletRequest).getRequestBody();
			}
		}
		
		logger.info("Begin Servlet request"+out);
		
		ServletResponse servletResponse = response;
		String responseOut = "";
		try {
			
			if(logger.isDebugEnabled() && response instanceof HttpServletResponse) {
				servletResponse = new HttpServletResponseCopier((HttpServletResponse) response);
			}
			
			chain.doFilter(servletRequest, servletResponse);
			
			if(logger.isDebugEnabled() && response instanceof HttpServletResponse) {
				
				((HttpServletResponseCopier)servletResponse).flushBuffer();

				int status = ((HttpServletResponseCopier)servletResponse).getStatus();
				String contentType = response.getContentType();
				String characterEncoding = response.getCharacterEncoding();
				byte[] copy = ((HttpServletResponseCopier)servletResponse).getCopy();
				
				responseOut += "\nResponse Status: "+status+"\nContent Type: "+contentType+"\nCharacter Encoding: "+characterEncoding+"\nResponse Size: "+copy.length;
				if(contentType!=null && (contentType.contains("xml")||contentType.contains("json"))) {
					String responseBody = new String(copy, response.getCharacterEncoding());
					responseOut += "\nResponse Body:\n"+responseBody;
				}
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
			
			logger.info("End Servlet request: "+(System.currentTimeMillis()-startTime)/1000.0+" sec"+responseOut);
		}
	}

	@Override
	public void destroy() {
		logger.debug("<destroy");
		logger.debug(">destroy");
	}

}
