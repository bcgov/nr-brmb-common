package ca.bc.gov.brmb.common.rest.client;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.brmb.common.utils.HttpServletRequestHolder;

public final class ChainedAuthorizationExchangeFilterFunction extends AuthorizationHeaderExchangeFilterFunction {
	
	private static final Logger logger = LoggerFactory.getLogger(ChainedAuthorizationExchangeFilterFunction.class);

	@Override
	public String getAuthorizationHeaderValue() {
		String result = null;
		logger.debug("<getAuthorizationHeaderValue");
		
		HttpServletRequest httpServletRequest = HttpServletRequestHolder.getHttpServletRequest();
		logger.debug("httpServletRequest="+httpServletRequest);
		
		if(httpServletRequest!=null) {
			
			result = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
		}
		
		logger.debug(">getAuthorizationHeaderValue "+result);
		return result;
	}
}
