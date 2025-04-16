package ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client;

import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.resource.AccessToken;
import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.resource.CheckedToken;

public interface TokenService {
	
	/**
	 * Calls the WebADE Oauth2 checkToken endpoint to retrieve the details of an
	 * access token.
	 * 
	 * @param accessToken Access Token returned by call to getToken or getImplicitToken
	 * @return A CheckToken object containing the details of the Access Token
	 * @throws Oauth2ClientException when not able to check an access token
	 */
	public CheckedToken checkToken(String accessToken) throws Oauth2ClientException;
	
		/**
	 * Calls the WebADE Oauth2 token endpoint to request an access token.
	 * Assumes use of default secret and client ID
	 * 
	 * @param clientId the identifier for the client requesting the token
	 * @param scope the scopes being requested
	 *            
	 * @return An AccessToken object containing the results of the token request
	 * @throws Oauth2ClientException when not able to return an access token
	 */
	public AccessToken getToken(String scope) throws Oauth2ClientException;

	/**
	 * Calls the WebADE Oauth2 token endpoint to request an access token.
	 * 
	 * @param clientId the identifier for the client requesting the token
	 * @param clientSecret the password for the client requesting the token
	 * @param scope the scopes being requested
	 *            
	 * @return An AccessToken object containing the results of the token request
	 * @throws Oauth2ClientException when not able to return an access token
	 */
	public AccessToken getToken(
			String clientId,
			String clientSecret,
			String scope) throws Oauth2ClientException;
	
	/**
	 * Calls the WebADE Oauth2 token endpoint to request an access token.
	 * 
	 * @param clientId the identifier for the client requesting the token
	 * @param clientSecret the password for the client requesting the token
	 * @param scope the scopes being requested
	 * @param authorizationCode an authorization code provided by a user to request scopes on
	 *            their behalf. The parameter should be null if requesting a client token.
	 * @param redirectUri a redirect URL may be required to ensure the clients identity
	 *            
	 * @return An AccessToken object containing the results of the token request
	 * @throws Oauth2ClientException when not able to return an access token
	 */
	public AccessToken getToken(
			String clientId,
			String clientSecret,
			String scope, 
			String authorizationCode, 
			String redirectUri) throws Oauth2ClientException;

	/**
	 * Calls the WebADE Oauth2 token endpoint to request an access token.
	 * 
	 * As in getToken (above), plus:
	 * 
	 * @param clientId the identifier for the client requesting the token
	 * @param clientSecret the password for the client requesting the token
	 * @param scope the scopes being requested
	 * @param grantType the grant type to request 
	 * @param authorizationCode an authorization code provided by a user to request scopes on
	 *            their behalf. The parameter should be null if requesting a client token.
	 * @param redirectUri a redirect URL may be required to ensure the clients identity
	 *            
	 * @return An AccessToken object containing the results of the token request
	 * @throws Oauth2ClientException when not able to return an access token
	 */
	public AccessToken getToken(
			String clientId,
			String clientSecret,
			String scope, 
			String grantType, 
			String authorizationCode, 
			String redirectUri) throws Oauth2ClientException;
	
	/**
	 * Calls the WebADE Oauth2 token endpoint to refresh an expired access
	 * token. As below, plus:
	 * 
	 * @param refreshToken Refresh Token returned by call to getToken with an Authorization Code
	 * 
	 * @return An AccessToken object containing the results of the token request
	 * @throws Oauth2ClientException when not able to refresh an access token
	 */
	public AccessToken refreshToken(String refreshToken) throws Oauth2ClientException;
	
	/**
	 * Calls the WebADE Oauth2 token endpoint to refresh an expired access
	 * token.
	 * 
	 * @param refreshToken Refresh Token returned by call to getToken with an Authorization Code
	 * @param scope Scope requested for the refresh token
	 * 
	 * @return An AccessToken object containing the results of the token request
	 * @throws Oauth2ClientException when not able to refresh an access token
	 */
	public AccessToken refreshToken(String refreshToken, String scope) throws Oauth2ClientException;
	
	/* Update the default client secret used by the token service */
	public void setDefaultClientSecret(String defaultClientSecret);

}
