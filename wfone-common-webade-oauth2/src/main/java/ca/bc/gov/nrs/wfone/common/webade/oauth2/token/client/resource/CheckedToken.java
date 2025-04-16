package ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.resource;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckedToken implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] authorities;
	private String[] resourceIds;
	private String jti;
	private String sub;
	private String[] scope;
	private String clientId;
	private String cid;
	private String clientAppCode;
	private String grantType;
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
	private Long iat;
	private Long exp;
	private String iss;
	private String[] aud;

	@JsonProperty("authorities")
	public String[] getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String[] authorities) {
		this.authorities = authorities;
	}

	@JsonProperty("resource_ids")
	public String[] getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(String[] resourceIds) {
		this.resourceIds = resourceIds;
	}

	@JsonProperty("jti")
	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}

	@JsonProperty("sub")
	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	@JsonProperty("scope")
	public String[] getScope() {
		return scope;
	}

	public void setScope(String[] scope) {
		this.scope = scope;
	}

	@JsonProperty("client_id")
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@JsonProperty("cid")
	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	@JsonProperty("client_app_code")
	public String getClientAppCode() {
		return clientAppCode;
	}

	public void setClientAppCode(String clientAppCode) {
		this.clientAppCode = clientAppCode;
	}

	@JsonProperty("grant_type")
	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	@JsonProperty("user_id")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@JsonProperty("user_name")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JsonProperty("given_name")
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	@JsonProperty("family_name")
	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("user_type")
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@JsonProperty("user_guid")
	public String getUserGuid() {
		return userGuid;
	}

	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
	}

	@JsonProperty("on_behalf_of_organization_id")
	public Long getOnBehalfOfOrganizationId() {
		return onBehalfOfOrganizationId;
	}

	public void setOnBehalfOfOrganizationId(Long onBehalfOfOrganizationId) {
		this.onBehalfOfOrganizationId = onBehalfOfOrganizationId;
	}

	@JsonProperty("on_behalf_of_organization_code")
	public String getOnBehalfOfOrganizationCode() {
		return onBehalfOfOrganizationCode;
	}

	public void setOnBehalfOfOrganizationCode(String onBehalfOfOrganizationCode) {
		this.onBehalfOfOrganizationCode = onBehalfOfOrganizationCode;
	}

	@JsonProperty("on_behalf_of_organization_name")
	public String getOnBehalfOfOrganizationName() {
		return onBehalfOfOrganizationName;
	}

	public void setOnBehalfOfOrganizationName(String onBehalfOfOrganizationName) {
		this.onBehalfOfOrganizationName = onBehalfOfOrganizationName;
	}
	
	@JsonProperty("on_behalf_of_organization_additional_info")
	public Map<String, String> getOnBehalfOfOrganizationAdditionalInfo() {
		return onBehalfOfOrganizationAdditionalInfo;
	}

	public void setOnBehalfOfOrganizationAdditionalInfo(
			Map<String, String> onBehalfOfOrganizationAdditionalInfo) {
		this.onBehalfOfOrganizationAdditionalInfo = onBehalfOfOrganizationAdditionalInfo;
	}

	@JsonProperty("business_number")
	public String getBusinessNumber() {
		return businessNumber;
	}

	public void setBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}

	@JsonProperty("business_guid")
	public String getBusinessGuid() {
		return businessGuid;
	}

	public void setBusinessGuid(String businessGuid) {
		this.businessGuid = businessGuid;
	}

	@JsonProperty("iat")
	public Long getIat() {
		return iat;
	}

	public void setIat(Long iat) {
		this.iat = iat;
	}

	@JsonProperty("exp")
	public Long getExp() {
		return exp;
	}

	public void setExp(Long exp) {
		this.exp = exp;
	}

	@JsonProperty("iss")
	public String getIss() {
		return iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	@JsonProperty("aud")
	public String[] getAud() {
		return aud;
	}

	public void setAud(String[] aud) {
		this.aud = aud;
	}
	
	@Override
	public String toString() {
		String result = "{";
		result += "\"jti\":"+(this.jti==null?"null":"\""+this.jti+"\"")+",";
		result += "\"sub\":"+(this.sub==null?"null":"\""+this.sub+"\"")+",";
		result += "\"clientId\":"+(this.clientId==null?"null":"\""+this.clientId+"\"")+",";
		result += "\"cid\":"+(this.cid==null?"null":"\""+this.cid+"\"")+",";
		result += "\"clientAppCode\":"+(this.clientAppCode==null?"null":"\""+this.clientAppCode+"\"")+",";
		result += "\"grantType\":"+(this.grantType==null?"null":"\""+this.grantType+"\"")+",";
		result += "\"userId\":"+(this.userId==null?"null":"\""+this.userId+"\"")+",";
		result += "\"userName\":"+(this.userName==null?"null":"\""+this.userName+"\"")+",";
		result += "\"givenName\":"+(this.givenName==null?"null":"\""+this.givenName+"\"")+",";
		result += "\"familyName\":"+(this.familyName==null?"null":"\""+this.familyName+"\"")+",";
		result += "\"email\":"+(this.email==null?"null":"\""+this.email+"\"")+",";
		result += "\"userType\":"+(this.userType==null?"null":"\""+this.userType+"\"")+",";
		result += "\"userGuid\":"+(this.userGuid==null?"null":"\""+this.userGuid+"\"")+",";
		result += "\"onBehalfOfOrganizationId\":"+(this.onBehalfOfOrganizationId==null?"null":this.onBehalfOfOrganizationId.toString())+",";
		result += "\"onBehalfOfOrganizationName\":"+(this.onBehalfOfOrganizationName==null?"null":"\""+this.onBehalfOfOrganizationName+"\"")+",";
		result += "\"businessNumber\":"+(this.businessNumber==null?"null":"\""+this.businessNumber+"\"")+",";
		result += "\"businessGuid\":"+(this.businessGuid==null?"null":"\""+this.businessGuid+"\"")+",";
		result += "\"iat\":"+(this.iat==null?"null":this.iat.toString())+",";
		result += "\"exp\":"+(this.exp==null?"null":this.exp.toString())+",";
		result += "\"iss\":"+(this.iss==null?"null":"\""+this.iss+"\"")+",";
		
		{
			result += "\"authorities\":";
			if(this.authorities==null) {
	
				result += "null";
			} else {
				
				String[] copy = Arrays.copyOf(this.authorities, this.authorities.length);
				Arrays.sort(copy);
				
				result += "[";
				
				for(int i=0;i<copy.length;++i) {
					result += (copy[i]==null?"null":"\""+copy[i]+"\"");
					if((i+1)<copy.length) {
						result += ",";
					}
				}
				
				result += "]";
			}
			result += ",";
		}
		
		{
			result += "\"scope\":";
			if(this.scope==null) {
	
				result += "null";
			} else {
				
				String[] copy = Arrays.copyOf(this.scope, this.scope.length);
				Arrays.sort(copy);
				
				result += "[";
				
				for(int i=0;i<copy.length;++i) {
					result += (copy[i]==null?"null":"\""+copy[i]+"\"");
					if((i+1)<copy.length) {
						result += ",";
					}
				}
				
				result += "]";
			}
			result += ",";
		}
		
		{
			result += "\"aud\":";
			if(this.aud==null) {
	
				result += "null";
			} else {
				
				String[] copy = Arrays.copyOf(this.aud, this.aud.length);
				Arrays.sort(copy);
				
				result += "[";
				
				for(int i=0;i<copy.length;++i) {
					result += (copy[i]==null?"null":"\""+copy[i]+"\"");
					if((i+1)<copy.length) {
						result += ",";
					}
				}
				
				result += "]";
			}
		}
		
		result += "}";
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result = true;
		
		if(obj instanceof CheckedToken) {
			CheckedToken other = (CheckedToken) obj;
			
			if(!this.toString().equals(other.toString())) {
				result = false;
			}
			
		} else {
			result = false;
		}
		
		return result;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
