package ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Oauth2ClientException extends Exception
{
	private static final long serialVersionUID = 2464899452550074694L;
	
	private static ObjectMapper mapper = new ObjectMapper();

	private final int httpResponseCode;
	private String error = null;
	private String errorDescription = null;
	private String errorUri = null;
	private String state = null;

	public Oauth2ClientException(String message, int httpResponseCode)
	{
		super(message);
		this.httpResponseCode = httpResponseCode;

		doParseMessage();
	}

	public Oauth2ClientException(int httpResponseCode, String error, String errorDescription, String errorUri, String state)
	{
		super(error + ": " + errorDescription);
		this.httpResponseCode = httpResponseCode;
		
		this.error = error;
		this.errorDescription = errorDescription;
		this.errorUri = errorUri;
		this.state = state;
	}

	public int getHttpResponseCode()
	{
		return httpResponseCode;
	}

	public String getState()
	{
		return state;
	}
	
	public String getError()
	{
		return error;
	}

	public String getErrorDescription()
	{
		return errorDescription;
	}
	
	public String getErrorUri()
	{
		return errorUri;
	}

	private static String cloudFoundryErrorHtmlPrefix = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<!DOCTYPE html";
	private static Pattern cloudFoundryErrorDivPattern = Pattern.compile("<div class=\"error\" title=\"error=&#034;(.*)&#034;, error_description=&#034;(.*)&#034;\">");
	
	private void doParseMessage()
	{
		String message = getMessage();
		if (message.startsWith("{"))
		{
			try
			{
				JsonNode node = mapper.readTree(getMessage());

				JsonNode errorNode = node.findValue("error");
				if (errorNode != null)
					error = errorNode.asText();

				JsonNode errorDescriptionNode = node.findValue("error_description");
				if (errorDescriptionNode != null)
					errorDescription = errorDescriptionNode.asText();
			}
			catch (IOException ioe)
			{
				// silently fail, leaving error and errorDescription null.
			}
		}
		else if (message.startsWith(cloudFoundryErrorHtmlPrefix))
		{
			Matcher matcher = cloudFoundryErrorDivPattern.matcher(getMessage());
			if (matcher.find())
			{
				error = matcher.group(1);
				errorDescription = matcher.group(2);
			}
		}
	}
}
