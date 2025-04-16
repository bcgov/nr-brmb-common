package ca.bc.gov.brmb.common.webade.oauth2.authentication;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.util.Assert;

import ca.bc.gov.nrs.wfone.common.webade.authentication.WebAdeAuthentication;

public class WebAdeOAuth2Authentication extends AbstractOAuth2TokenAuthenticationToken<OAuth2AccessToken> implements WebAdeAuthentication {

	private static final long serialVersionUID = 1L;

	private Map<String, Object> attributes;

	private String clientAppCode;
	private String clientId;
	private String userId;
	private String userTypeCode;
	private String userGuid;
	private String givenName;
	private String familyName;
	private Long onBehalfOfOrganizationId;
	private String onBehalfOfOrganizationCode;
	private String onBehalfOfOrganizationName;
	private Map<String,String> onBehalfOfOrganizationAdditionalInfo;
	private String businessNumber;
	private String businessGuid;
	
	public WebAdeOAuth2Authentication(
			OAuth2AuthenticatedPrincipal principal, 
			OAuth2AccessToken credentials,
			Collection<? extends GrantedAuthority> authorities,
			String clientAppCode, 
			String clientId, 
			String userId, 
			String userTypeCode, 
			String userGuid, 
			String givenName, 
			String familyName, 
			Long onBehalfOfOrganizationId, 
			String onBehalfOfOrganizationCode, 
			String onBehalfOfOrganizationName, 
			Map<String,String> onBehalfOfOrganizationAdditionalInfo, 
			String businessNumber, 
			String businessGuid) {
		super(credentials, principal, credentials, authorities);
		Assert.isTrue(credentials.getTokenType() == OAuth2AccessToken.TokenType.BEARER, "credentials must be a bearer token");
		this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(principal.getAttributes()));
		setAuthenticated(true);
		
		this.clientAppCode = clientAppCode;
		this.clientId = clientId;
		this.userId = userId;
		this.userTypeCode = userTypeCode;
		this.userGuid = userGuid;
		this.givenName = givenName;
		this.familyName = familyName;
		this.onBehalfOfOrganizationId = onBehalfOfOrganizationId;
		this.onBehalfOfOrganizationCode = onBehalfOfOrganizationCode; 
		this.onBehalfOfOrganizationName = onBehalfOfOrganizationName; 
		this.onBehalfOfOrganizationAdditionalInfo = onBehalfOfOrganizationAdditionalInfo; 
		this.businessNumber = businessNumber;
		this.businessGuid = businessGuid;
	}

	@Override
	public Map<String, Object> getTokenAttributes() {
		return this.attributes;
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public String getClientAppCode() {
		return clientAppCode;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public String getUserTypeCode() {
		return userTypeCode;
	}

	@Override
	public String getUserGuid() {
		return userGuid;
	}

	@Override
	public String getGivenName() {
		return givenName;
	}

	@Override
	public String getFamilyName() {
		return familyName;
	}

	@Override
	public Long getOnBehalfOfOrganizationId() {
		return onBehalfOfOrganizationId;
	}

	@Override
	public String getOnBehalfOfOrganizationCode() {
		return onBehalfOfOrganizationCode;
	}

	@Override
	public String getOnBehalfOfOrganizationName() {
		return onBehalfOfOrganizationName;
	}

	@Override
	public Map<String, String> getOnBehalfOfOrganizationAdditionalInfo() {
		return onBehalfOfOrganizationAdditionalInfo;
	}

	@Override
	public String getBusinessNumber() {
		return businessNumber;
	}

	@Override
	public String getBusinessGuid() {
		return businessGuid;
	}

	@Override
	public boolean isAuthenticatedUser(String type, String guid) {
		boolean result = false;

		if(getUserTypeCode().equals(type)&&getUserGuid().equals(guid)) {
			
			result = true;
		}

		return result;
	}

	@Override
	public boolean hasAuthority(String... authorityName) {
		boolean result = false;
		
		List<String> authorityNames = Arrays.asList(authorityName);
		
		for (GrantedAuthority grantedAuthority : getAuthorities()) {
			
			String authority = grantedAuthority.getAuthority();

			if (authorityNames.contains(authority)) {
				result = true;
				break;
			}
		}

		return result;
	}
}
