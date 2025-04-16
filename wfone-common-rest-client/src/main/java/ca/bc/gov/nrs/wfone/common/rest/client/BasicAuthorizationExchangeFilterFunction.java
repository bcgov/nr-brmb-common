package ca.bc.gov.nrs.wfone.common.rest.client;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BasicAuthorizationExchangeFilterFunction extends AuthorizationHeaderExchangeFilterFunction {
	
	private static final Logger logger = LoggerFactory.getLogger(BasicAuthorizationExchangeFilterFunction.class);

	private String username;
	private String password;

	public BasicAuthorizationExchangeFilterFunction(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public String getAuthorizationHeaderValue() {
		String result = null;
		logger.debug("<getAuthorizationHeaderValue");
		
		result = "Basic "
				+ new String(
						Base64
						.getEncoder()
						.encode((username + ":" + password).getBytes()));

		logger.debug(">getAuthorizationHeaderValue "+result);
		return result;
	}
}
