package ca.bc.gov.nrs.wfone.common.webade.authentication;

import java.util.Map;

import org.springframework.security.core.Authentication;

public interface WebAdeAuthentication extends Authentication {
	
	public String getClientAppCode();
	
	public String getClientId();
	
	public String getUserId();
	
	public String getUserTypeCode();
	
	public String getUserGuid();
	
	public String getGivenName();
	
	public String getFamilyName();

	public Long getOnBehalfOfOrganizationId();

	public String getOnBehalfOfOrganizationCode();

	public String getOnBehalfOfOrganizationName();

	public Map<String, String> getOnBehalfOfOrganizationAdditionalInfo();

	public String getBusinessNumber();

	public String getBusinessGuid();

	public boolean hasAuthority(String... authorityName);
	
	public boolean isAuthenticatedUser(String userTypeCode, String userGuid);
}