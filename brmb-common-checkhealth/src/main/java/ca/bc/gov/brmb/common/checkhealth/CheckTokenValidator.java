package ca.bc.gov.brmb.common.checkhealth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.brmb.common.rest.resource.HealthCheckResponseRsrc;
import ca.bc.gov.brmb.common.webade.oauth2.token.client.Oauth2ClientException;
import ca.bc.gov.brmb.common.webade.oauth2.token.client.TokenService;

public class CheckTokenValidator extends AbstractValidator {

	private final Logger logger = LoggerFactory.getLogger(CheckTokenValidator.class);

	private TokenService tokenService;
	
	@Override
	public void init() {
		if(tokenService==null) {
			throw new IllegalArgumentException("tokenService cannot be null.");
		}
	}
	
	@Override
	public HealthCheckResponseRsrc validate(final String callstack) {
		logger.debug("<validate "+this.getComponentName());
		
		HealthCheckResponseRsrc result = new HealthCheckResponseRsrc();
		result.setComponentIdentifier(getComponentIdentifier());
		result.setComponentName(getComponentName());
		
		try {
			
			try {
				
				tokenService.checkToken("INVALIDTOKEN");
			} catch(Oauth2ClientException e) {
				if("invalid_token".equals(e.getError())) {
					result.setValidationStatus(this.getGreenMapping());
					result.setStatusDetails("Token Service request successfull.");
				} else {
					throw e;
				}
			}
		} catch(Throwable t) {
			result.setValidationStatus(this.getRedMapping());
			result.setStatusDetails("Token Service request failed: "+t.getMessage());
		}

		logger.debug(">validate "+this.getComponentName());
		return result;
	}

	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	public String getComponentIdentifier() {
		return "Token Service";
	}

	@Override
	public String getComponentName() {
		return "Token Service";
	}

}
