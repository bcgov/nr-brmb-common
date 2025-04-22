package ca.bc.gov.brmb.common.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProvidedAuthorizationExchangeFilterFunction extends AuthorizationHeaderExchangeFilterFunction {
	
	private static final Logger logger = LoggerFactory.getLogger(ProvidedAuthorizationExchangeFilterFunction.class);

	private String authorizationHeaderValue;
	
	public ProvidedAuthorizationExchangeFilterFunction(String headerValue){
		this.authorizationHeaderValue = headerValue;
	}

	@Override
	public String getAuthorizationHeaderValue() {
		String result = null;
		logger.debug("<getAuthorizationHeaderValue");
		
		result = authorizationHeaderValue;

		logger.debug(">getAuthorizationHeaderValue "+result);
		return result;
	}
}
