package ca.bc.gov.nrs.wfone.common.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import reactor.core.publisher.Mono;

public abstract class AuthorizationHeaderExchangeFilterFunction implements ExchangeFilterFunction {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationHeaderExchangeFilterFunction.class);
	
	public static final String AUTHORIZATION_HEADER = "Authorization";

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		logger.debug("<filter");
		Mono<ClientResponse> result = null;
		
		String authorizationHeaderValue = getAuthorizationHeaderValue();
		logger.debug("authorizationHeaderValue="+authorizationHeaderValue);
		
		if(authorizationHeaderValue!=null) {
			
	        ClientRequest newRequest = ClientRequest.from(request).header(AUTHORIZATION_HEADER, getAuthorizationHeaderValue()).build();
	        
	        result = next.exchange(newRequest);
		} else {
	        
	        result = next.exchange(request);
		}
		
		logger.debug(">filter "+result);
		return result;
	}

	public abstract String getAuthorizationHeaderValue();
}
