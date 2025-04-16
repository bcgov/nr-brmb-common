package ca.bc.gov.brmb.common.webade.oauth2.token.client.stub;

import java.util.Date;
import java.util.Map;

class RefreshToken {

	private String issuer;
	private String userId;
	private String userName;
	private String givenName;
	private String familyName;
	private String email;
	private String userType;
	private String userGuid;
	private Long onBehalfOfOrganizationId;
	private String onBehalfOfOrganizationCode;
	private String onBehalfOfOrganizationName;
	private Map<String,String> onBehalfOfOrganizationAdditionalInfo;
	private String businessNumber;
	private String businessGuid;
	private Date expiryTimestamp;
	private String scope;
	private String clientId;
	private String clientAppCode;
	private String grantType;

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserGuid() {
		return userGuid;
	}

	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
	}

	public Long getOnBehalfOfOrganizationId() {
		return onBehalfOfOrganizationId;
	}

	public void setOnBehalfOfOrganizationId(Long onBehalfOfOrganizationId) {
		this.onBehalfOfOrganizationId = onBehalfOfOrganizationId;
	}

	public String getOnBehalfOfOrganizationCode() {
		return onBehalfOfOrganizationCode;
	}

	public void setOnBehalfOfOrganizationCode(String onBehalfOfOrganizationCode) {
		this.onBehalfOfOrganizationCode = onBehalfOfOrganizationCode;
	}

	public String getOnBehalfOfOrganizationName() {
		return onBehalfOfOrganizationName;
	}

	public void setOnBehalfOfOrganizationName(String onBehalfOfOrganizationName) {
		this.onBehalfOfOrganizationName = onBehalfOfOrganizationName;
	}
	
	public Map<String, String> getOnBehalfOfOrganizationAdditionalInfo() {
		return onBehalfOfOrganizationAdditionalInfo;
	}

	public void setOnBehalfOfOrganizationAdditionalInfo(
			Map<String, String> onBehalfOfOrganizationAdditionalInfo) {
		this.onBehalfOfOrganizationAdditionalInfo = onBehalfOfOrganizationAdditionalInfo;
	}

	public String getBusinessNumber() {
		return businessNumber;
	}

	public void setBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}

	public String getBusinessGuid() {
		return businessGuid;
	}

	public void setBusinessGuid(String businessGuid) {
		this.businessGuid = businessGuid;
	}

	public Date getExpiryTimestamp() {
		return expiryTimestamp;
	}

	public void setExpiryTimestamp(Date expiryTimestamp) {
		this.expiryTimestamp = expiryTimestamp;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public String getClientAppCode() {
		return clientAppCode;
	}

	public void setClientAppCode(String clientAppCode) {
		this.clientAppCode = clientAppCode;
	}
}
