package ca.bc.gov.nrs.wfone.common.rest.endpoints.jersey;

import java.util.Properties;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import ca.bc.gov.nrs.wfone.common.rest.endpoints.CheckHealthEndpointImpl;
import ca.bc.gov.nrs.wfone.common.rest.endpoints.filters.CorsFilter;
import ca.bc.gov.nrs.wfone.common.rest.endpoints.filters.RequestMetricsFilter;

public abstract class JerseyResourceConfig extends ResourceConfig {

	private static final Logger logger = LoggerFactory
			.getLogger(JerseyResourceConfig.class);

	private CorsFilter corsFilter;
	
    /**
     * Register JAX-RS application components.
     */
    public JerseyResourceConfig() {
    	
    	
        register(RequestContextFilter.class).
        register(ObjectMapperProvider.class).
        register(JacksonFeature.class);
        register(CheckHealthEndpointImpl.class);
        
        this.corsFilter = new CorsFilter();
        register(this.corsFilter);
        
    	WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
    	if(ctx==null) {
    		logger.warn("No Spring CurrentWebApplicationContext");
    	} else {

    		if(!ctx.containsBean("bootstrapProperties")) {
        		logger.warn("No 'bootstrapProperties' in Spring CurrentWebApplicationContext");
    		} else {
    			
        		Object bean = ctx.getBean("bootstrapProperties");
    			
    			if (bean instanceof Properties) {
    			
	    			Properties properties = (Properties) bean;
	    			
	    			String requestIdHeader = properties.getProperty(RequestMetricsFilter.REQUEST_ID_HEADER_PARAMETER);
	    			if(requestIdHeader==null||requestIdHeader.trim().length()==0) {
	    				logger.warn("No '"+RequestMetricsFilter.REQUEST_ID_HEADER_PARAMETER+"' bootstrap property.");
	    			} else {
		    			String allowedHeaders = this.corsFilter.getAllowedHeaders();
		    			logger.debug("allowedheaders="+allowedHeaders);
		    			allowedHeaders += ", requestId";
		    			logger.debug("allowedheaders="+allowedHeaders);
		    			this.corsFilter.setAllowedHeaders(allowedHeaders);
	    			}
	    		} else {
	        		logger.warn("Expecting 'bootstrapProperties' to be java.util.Properties but found "+bean.getClass());
	    		}
    		}
    	}
    }

	public CorsFilter getCorsFilter() {
		return corsFilter;
	}
}