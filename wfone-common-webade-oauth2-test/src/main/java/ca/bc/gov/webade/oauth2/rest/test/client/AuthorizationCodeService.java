package ca.bc.gov.webade.oauth2.rest.test.client;

import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.Oauth2ClientException;
import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.resource.AccessToken;
import ca.bc.gov.webade.oauth2.rest.test.resource.AuthorizationCode;

/**
 * This class exists to provide OAUTH2 utilities for integration tests.
 * 
 * @author phowells
 * 
 */
public interface AuthorizationCodeService {

	public final String SITEMINDER_USER_TYPE_INTERNAL = "Internal";
	public final String SITEMINDER_USER_TYPE_BUSINESS = "Business";
	public final String SITEMINDER_USER_TYPE_INDIVIDUAL = "Individual";
	public final String SITEMINDER_USER_TYPE_VERIFIED_INDIVIDUAL = "VerifiedIndividual";
	
	public AuthorizationCode getAuthorizationCode(String scope,
			String redirectUri, Long organizationId, String siteMinderUserType,
			String siteMinderUserIdentifier, String siteMinderAuthoritativePartyIdentifier, String username, String secret) throws Oauth2ClientException;

	public AuthorizationCode getAuthorizationCode(String scope,
			String redirectUri, Long organizationId, String siteMinderUserType,
			String siteMinderUserIdentifier, String siteMinderAuthoritativePartyIdentifier, String username, String secret,
			String state) throws Oauth2ClientException;

	public AccessToken getImplicitToken(String scope,
			String redirectUri, Long organizationId, String siteMinderUserType,
			String siteMinderUserIdentifier, String siteMinderAuthoritativePartyIdentifier,
			String username, String secret,
			String state)
			throws Oauth2ClientException;

	public AccessToken getImplicitToken(String scope,
			String redirectUri, Long organizationId, String siteMinderUserType,
			String siteMinderUserIdentifier, String siteMinderAuthoritativePartyIdentifier,
			String username, String secret)
			throws Oauth2ClientException;
}
