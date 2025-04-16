package ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.stub;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.Oauth2ClientException;
import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.TokenService;
import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.resource.AccessToken;
import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.resource.CheckedToken;
import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.resource.transformers.JsonTransformer;
import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.resource.transformers.TransformerException;

public class TokenServiceStub implements TokenService {

	private static final Logger logger = LoggerFactory.getLogger(TokenServiceStub.class);

	private static Map<String, AccessToken> accessTokens = new HashMap<String, AccessToken>();
	private static Map<String, RefreshToken> refreshTokens = new HashMap<String, RefreshToken>();
	private static Map<String, CheckedToken> checkedTokens = new HashMap<String, CheckedToken>();

	private String clientAppCode;
	private String issuer;

	private UserInfoData users;
	
	private User selectedUser;
	
	private String defaultScopes;

	private Long onBehalfOfOrganizationId;
	private String onBehalfOfOrganizationCode;
	private String onBehalfOfOrganizationName; 
	private Map<String,String> onBehalfOfOrganizationAdditionalInfo; 

	public TokenServiceStub(String clientAppCode) {
		this(clientAppCode, "http://localhost:8080/webade-oauth2");
	}

	public TokenServiceStub(String clientAppCode, String issuer) {
		logger.debug("<TokenServiceImpl");

		this.clientAppCode = clientAppCode;
		this.issuer = issuer;

		String userInfoFileLocation = System
				.getProperty("user-info-file-location");
		
		this.users = UserInfoDataFactory.getUserInfoData(userInfoFileLocation);

		logger.debug(">TokenServiceImpl");
	}
	
	public ServiceClientUser selectServiceClient(String accountName, String guid, String scopes) {
		
		this.selectedUser = null;
		this.onBehalfOfOrganizationId = null;
		this.onBehalfOfOrganizationCode = null;
		this.onBehalfOfOrganizationName = null;
		this.onBehalfOfOrganizationAdditionalInfo = null;
		this.selectedUser = new ServiceClientUser();
		this.selectedUser.setAccountName(accountName);
		this.selectedUser.setGuid(guid);
		this.selectedUser.setSourceDirectory("SCL");
		
		this.defaultScopes = scopes;
		
		return (ServiceClientUser) this.selectedUser;
	}
	
	public UserInfo selectUser(String userType, String userGuid, Long organizationId, String organizationName) {
		String organizationCode = null;
		Map<String,String> organizationAdditionalInfo = new HashMap<String,String>();
		return selectUser(userType, userGuid, organizationId, organizationCode, organizationName, organizationAdditionalInfo);
	}
		
	public UserInfo selectUser(String userType, String userGuid, Long organizationId, String organizationCode, String organizationName, Map<String,String> organizationAdditionalInfo) {
		
		if(this.users==null) {
			throw new RuntimeException("System property for user-info-file-location has not been set.");
		}
		this.selectedUser = null;
		this.onBehalfOfOrganizationId = null;
		this.onBehalfOfOrganizationCode = null;
		this.onBehalfOfOrganizationName = null;
		this.onBehalfOfOrganizationAdditionalInfo = null;
		
		for(UserInfo userInfo:users.getUserInfos()) {
			if(userType.equals(userInfo.getUserType()) && userGuid.equals(userInfo.getGuid())) {
				this.selectedUser = userInfo;
				this.onBehalfOfOrganizationId = organizationId;
				this.onBehalfOfOrganizationCode = organizationCode;
				this.onBehalfOfOrganizationName = organizationName;
				this.onBehalfOfOrganizationAdditionalInfo = organizationAdditionalInfo==null?null:Collections.unmodifiableMap(organizationAdditionalInfo);
				break;
			}
		}
		if(this.selectedUser == null) {
			throw new RuntimeException("User not found: "+userType+" "+userGuid);
		}
		
		return (UserInfo) this.selectedUser;
	}
	
	@Override
	public CheckedToken checkToken(String accessToken)
			throws Oauth2ClientException {
		logger.debug("<checkToken");

		CheckedToken checkedToken = null;

		checkedToken = checkedTokens.get(accessToken);

		if (checkedToken == null) {
			throw new Oauth2ClientException("Invalid token", 400);
		}
		
		JsonTransformer jt = new JsonTransformer();
		try {
			jt.marshall(checkedToken);
		} catch (TransformerException e) {
			throw new Oauth2ClientException(e.getMessage(), 500);
		}

		logger.debug(">checkToken " + checkedToken);
		return checkedToken;
	}

	@Override
	public AccessToken getToken(
			String clientId,
			String clientSecret,
			String scope) throws Oauth2ClientException {
		return getToken(clientId, clientSecret, scope, null, null);
	}

	@Override
	public AccessToken getToken(
			String clientId,
			String clientSecret,
			String scope, 
			String authorizationCode,
			String redirectUri) throws Oauth2ClientException {
		String grantType = authorizationCode == null ? "client_credentials"
				: "authorization_code";
		return getToken(clientId, clientSecret, scope, grantType, authorizationCode, redirectUri);
	}

	@Override
	public AccessToken getToken(
			String clientId,
			String clientSecret,
			String scope, 
			String grantType,
			String authorizationCode, 
			String redirectUri)
			throws Oauth2ClientException {
		logger.debug("<getToken " + scope);

		AccessToken accessToken = null;

		String userType = null;
		String userGuid = null; 
		String userId = null;  
		String userName = null;  
		String givenName = null; 
		String familyName = null; 
		String email = null; 
		String businessNumber = null;
		String businessGuid = null;
		
		if(selectedUser!=null) {

			userType = selectedUser.getUserType();
			userGuid = selectedUser.getGuid(); 
			userId = selectedUser.getUserId();  
			userName = userType+"\\"+selectedUser.getAccountName()+(this.onBehalfOfOrganizationId==null?"":"\\"+this.onBehalfOfOrganizationId);  
			
			if(selectedUser instanceof UserInfo) {
				givenName = ((UserInfo)selectedUser).getFirstName(); 
				familyName = ((UserInfo)selectedUser).getSurname(); 
				email = ((UserInfo)selectedUser).getContactEmail(); 
		
				if(selectedUser instanceof BusinessPartnerUserInfo) {
					businessNumber = ((BusinessPartnerUserInfo)selectedUser).getBusinessNumber();
					businessGuid = ((BusinessPartnerUserInfo)selectedUser).getBusinessGuid();
				}
			}
		} else {
			userId = "SCL\\"+clientId;
		}
		if (scope != null && scope.indexOf('*') != -1) {
			
			scope = defaultScopes;
		}
		
		String accessTokenString = createAccessToken(scope, clientId,
				clientAppCode, grantType, userType, userGuid, userId, userName,
				givenName, familyName, email, this.onBehalfOfOrganizationId, this.onBehalfOfOrganizationCode,
				this.onBehalfOfOrganizationName, this.onBehalfOfOrganizationAdditionalInfo, businessNumber, businessGuid,
				this.issuer);

		accessToken = accessTokens.get(accessTokenString);

		logger.debug(">getToken " + accessToken);
		return accessToken;
	}

	@Override
	public AccessToken refreshToken(String refreshToken)
			throws Oauth2ClientException {
		return refreshToken(refreshToken, null /* get same scope as before */);
	}

	@Override
	public AccessToken refreshToken(String refreshToken, String scope)
			throws Oauth2ClientException {
		logger.debug("<refreshToken");

		AccessToken accessToken = null;

		if (scope != null && scope.indexOf('*') != -1) {
			throw new Oauth2ClientException("Scope expansion not supported.",
					400);
		}

		RefreshToken rt = refreshTokens.get(refreshToken);

		Date now = new Date();
		if (rt != null) {
			if (now.before(rt.getExpiryTimestamp())) {

				String accessTokenString = createAccessToken(
						scope == null ? rt.getScope() : scope,
						rt.getClientId(), rt.getClientAppCode(),
						rt.getGrantType(), rt.getUserType(), rt.getUserGuid(),
						rt.getUserId(), rt.getUserName(), rt.getGivenName(),
						rt.getFamilyName(), rt.getEmail(),
						rt.getOnBehalfOfOrganizationId(),
						rt.getOnBehalfOfOrganizationCode(),
						rt.getOnBehalfOfOrganizationName(),
						rt.getOnBehalfOfOrganizationAdditionalInfo(),
						rt.getBusinessNumber(), rt.getBusinessGuid(),
						rt.getIssuer());

				accessToken = accessTokens.get(accessTokenString);
			}
		}

		logger.debug(">refreshToken " + accessToken);
		return accessToken;
	}

	private static String createAccessToken(String scope, String clientId,
			String clientAppCode, String grantType, String userType,
			String userGuid, String userId, String userName, String givenName,
			String familyName, String email, Long onBehalfOfOrganizationId, String onBehalfOfOrganizationCode,
			String onBehalfOfOrganizationName, Map<String,String> onBehalfOfOrganizationAdditionalInfo, String businessNumber,
			String businessGuid, String issuer) {

		String accessTokenString = UUID.randomUUID().toString();

		AccessToken accessToken = new AccessToken();
		accessToken.setAccessToken(accessTokenString);
		accessToken.setTokenType("Bearer");
		accessToken.setExpiresIn(Long.valueOf(1000 * 60 * 10));

		accessToken.setScope(scope);
		accessToken.setJti(null);

		CheckedToken checkedToken = new CheckedToken();

		String[] scopes = scope.split(" ");
		Set<String> aud = new HashSet<String>();
		for (String tmp : scopes) {
			String[] split = tmp.split("\\.");
			aud.add(split[0]);
		}

		checkedToken.setJti(null);
		checkedToken.setSub(userId);
		checkedToken.setScope(scopes);
		checkedToken.setClientId(clientId);
		checkedToken.setCid(clientId);
		checkedToken.setClientAppCode(clientAppCode);
		checkedToken.setGrantType(grantType);
		if ("client_credentials".equals(grantType)) {
			checkedToken.setAuthorities(scopes);
		}
		checkedToken.setUserId(userId);
		checkedToken.setUserType(userType);
		checkedToken.setUserGuid(userGuid);
		if (isRefreshTokenSupported(grantType)) {
			checkedToken.setUserName(userName);
			checkedToken.setGivenName(givenName);
			checkedToken.setFamilyName(familyName);
			checkedToken.setEmail(email);
			checkedToken.setOnBehalfOfOrganizationId(onBehalfOfOrganizationId);
			checkedToken.setOnBehalfOfOrganizationCode(onBehalfOfOrganizationCode);
			checkedToken.setOnBehalfOfOrganizationName(onBehalfOfOrganizationName);
			checkedToken.setOnBehalfOfOrganizationAdditionalInfo(onBehalfOfOrganizationAdditionalInfo);
			checkedToken.setBusinessNumber(businessNumber);
			checkedToken.setBusinessGuid(businessGuid);
		}
		checkedToken.setIat(Long.valueOf(System.currentTimeMillis() / 1000));
		checkedToken.setExp(Long.valueOf(System.currentTimeMillis()
				+ (10 * 60 * 1000L)));
		checkedToken.setIss(issuer + "/oauth/token");
		checkedToken.setAud(aud.toArray(new String[] {}));

		if (isRefreshTokenSupported(grantType)) {

			String refreshTokenString = UUID.randomUUID().toString();
			accessToken.setRefreshToken(refreshTokenString);

			RefreshToken refreshToken = new RefreshToken();
			refreshToken.setIssuer(issuer);
			refreshToken.setUserId(userId);
			refreshToken.setUserName(userName);
			refreshToken.setGivenName(givenName);
			refreshToken.setFamilyName(familyName);
			refreshToken.setEmail(email);
			refreshToken.setUserType(userType);
			refreshToken.setUserGuid(userGuid);
			refreshToken.setOnBehalfOfOrganizationId(onBehalfOfOrganizationId);
			refreshToken.setOnBehalfOfOrganizationCode(onBehalfOfOrganizationCode);
			refreshToken.setOnBehalfOfOrganizationName(onBehalfOfOrganizationName);
			refreshToken.setOnBehalfOfOrganizationAdditionalInfo(onBehalfOfOrganizationAdditionalInfo);
			refreshToken.setBusinessNumber(businessNumber);
			refreshToken.setBusinessGuid(businessGuid);
			refreshToken.setExpiryTimestamp(new Date(System.currentTimeMillis()
					+ (60 * 1000L)));
			refreshToken.setScope(scope);
			refreshToken.setClientId(clientId);
			refreshToken.setClientAppCode(clientAppCode);
			refreshToken.setGrantType(grantType);

			refreshTokens.put(refreshTokenString, refreshToken);
		}

		checkedTokens.put(accessTokenString, checkedToken);

		accessTokens.put(accessTokenString, accessToken);

		return accessTokenString;
	}

	protected static boolean isRefreshTokenSupported(String grantType) {
		return "authorization_code".equals(grantType)
				|| "password".equals(grantType)
				|| "refresh_token".equals(grantType);
	}

}
