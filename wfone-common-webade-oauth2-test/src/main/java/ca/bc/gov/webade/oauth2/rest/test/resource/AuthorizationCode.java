package ca.bc.gov.webade.oauth2.rest.test.resource;

public class AuthorizationCode
{
	private final String code;
	private final String state;
	
	public AuthorizationCode(String code, String state)
	{
		this.code = code;
		this.state = state;
	}
	
	public String getCode()
	{
		return code;
	}
	
	public String getState()
	{
		return state;
	}
}
