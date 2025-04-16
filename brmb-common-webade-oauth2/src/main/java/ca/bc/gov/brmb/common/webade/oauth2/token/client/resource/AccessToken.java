package ca.bc.gov.brmb.common.webade.oauth2.token.client.resource;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accessToken;
	private String tokenType;

	private String refreshToken;
	private Long expiresIn;

	private String scope;
	private String jti;
	
	/** value passed with authorize request (if any) in implicit flow. Otherwise, <code>null</code>. */  
	private final String state;

	/**
	 * Constructor used when creating an access token from an implicit response. These access tokens will
	 * never have a <code>jti</code> value or refresh token.
	 * 
	 * @param accessToken
	 * @param tokenType
	 * @param expiresIn
	 * @param scope
	 * @param state
	 */
	public AccessToken(String accessToken, String tokenType, Long expiresIn, String scope, String state) {
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
		this.scope = scope;
		this.state = state;
	}

	public AccessToken() {
		this.state = null;
	}

	public String getState() {
		return state;
	}
	
	@JsonProperty("access_token")
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@JsonProperty("token_type")
	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	@JsonProperty("refresh_token")
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@JsonProperty("expires_in")
	public Long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	@JsonProperty("scope")
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@JsonProperty("jti")
	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}
	
	@Override
	public String toString() {
		String result = "{";
		result += "\"accessToken\":"+(this.accessToken==null?"null":"\""+this.accessToken+"\"")+",";
		result += "\"tokenType\":"+(this.tokenType==null?"null":"\""+this.tokenType+"\"")+",";
		result += "\"refreshToken\":"+(this.refreshToken==null?"null":"\""+this.refreshToken+"\"")+",";
		result += "\"expiresIn\":"+(this.expiresIn==null?"null":this.expiresIn.toString())+",";
		result += "\"scope\":"+(this.scope==null?"null":"\""+this.scope+"\"")+",";
		result += "\"jti\":"+(this.jti==null?"null":"\""+this.jti+"\"")+",";
		result += "\"state\":"+(this.state==null?"null":"\""+this.state+"\"");
		result += "}";
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result = true;
		
		if(obj instanceof AccessToken) {
			
			AccessToken other = (AccessToken) obj;
			
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
